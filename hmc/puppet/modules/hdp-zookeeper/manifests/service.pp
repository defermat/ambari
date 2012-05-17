class hdp-zookeeper::service(
  $ensure = 'running',
  $myid
)
{
  include $hdp-zookeeper::params
  $user = $hdp-zookeeper::params::zk_user
  $conf_dir = $hdp-zookeeper::params::conf_dir
  $zk_bin = $hdp::params::zk_bin
  $cmd = "/bin/env ZOOCFGDIR=${conf_dir} ZOOCFG=zoo.cfg ${zk_bin}/zkServer.sh"

  $pid_file = $hdp-zookeeper::params::zk_pid_file  

  if ($ensure == 'running') {
    $daemon_cmd = "su - ${user} -c  'source ${conf_dir}/zookeeper-env.sh ; ${cmd} start'"
    $no_op_test = "ls ${pid_file} >/dev/null 2>&1 && ps `cat ${pid_file}` >/dev/null 2>&1"
    #not using $no_op_test = "su - ${user} -c  '${cmd} status'" because checks more than whether there is a service started up
  } elsif ($ensure == 'stopped') {
    $daemon_cmd = "su - ${user} -c  '${cmd} stop'"
    #TODO: put in no_op_test for stopped
    $no_op_test = undef
  } else {
    $daemon_cmd = undef
  }

  hdp::directory_recursive_create { $hdp-zookeeper::params::zk_pid_dir: 
    owner        => $user,
    context_tag => 'zk_service'
  }
  hdp::directory_recursive_create { $hdp-zookeeper::params::zk_log_dir: 
    owner        => $user,
    context_tag => 'zk_service'
  }
   hdp::directory_recursive_create { $hdp-zookeeper::params::zk_data_dir: 
    owner        => $user,
    context_tag => 'zk_service'
  }
  
  class { 'hdp-zookeeper::set_myid': myid => $myid}
 
  if ($daemon_cmd != undef) {
    hdp::exec { $daemon_cmd:
      command => $daemon_cmd,
      unless  => $no_op_test,
      initial_wait => $initial_wait
    }
  }

  anchor{'hdp-zookeeper::service::begin':} -> Hdp::Directory_recursive_create<|context_tag == 'zk_service'|> -> 
    Class['hdp-zookeeper::set_myid'] -> anchor{'hdp-zookeeper::service::end':}

  if ($daemon_cmd != undef) {
    Class['hdp-zookeeper::set_myid'] -> Hdp::Exec[$daemon_cmd] -> Anchor['hdp-zookeeper::service::end']
  }

}

class hdp-zookeeper::set_myid($myid)
{
  $create_file = "${hdp-zookeeper::params::zk_data_dir}/myid"
  $cmd = "echo '${myid}' > ${create_file}"
  hdp::exec{ $cmd:
    command => $cmd,
    creates  => $create_file
  }
}


