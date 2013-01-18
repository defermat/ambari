/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ambari.server.controller.gsinstaller;

import org.apache.ambari.server.controller.internal.ResourceImpl;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;

import java.util.Map;
import java.util.Set;

/**
 * A service resource provider for a gsInstaller defined cluster.
 */
public class GSInstallerServiceProvider extends GSInstallerResourceProvider{

  // Services
  protected static final String SERVICE_CLUSTER_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("ServiceInfo", "cluster_name");
  protected static final String SERVICE_SERVICE_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("ServiceInfo", "service_name");


  // ----- Constructors ------------------------------------------------------

  /**
   * Construct a resource provider based on the given cluster definition.
   *
   * @param clusterDefinition  the cluster definition
   */
  public GSInstallerServiceProvider(ClusterDefinition clusterDefinition) {
    super(clusterDefinition);
    initServiceResources();
  }


  // ----- ResourceProvider --------------------------------------------------

  @Override
  public Set<String> getPropertyIdsForSchema() {
    return PropertyHelper.getPropertyIds(Resource.Type.Service);
  }

  @Override
  public Map<Resource.Type, String> getKeyPropertyIds() {
    return PropertyHelper.getKeyPropertyIds(Resource.Type.Service);
  }


  // ----- helper methods ----------------------------------------------------

  /**
   * Create the resources based on the cluster definition.
   */
  private void initServiceResources() {
    String      clusterName = getClusterDefinition().getClusterName();
    Set<String> services    = getClusterDefinition().getServices();

    for (String serviceName : services) {
      Resource service = new ResourceImpl(Resource.Type.Service);
      service.setProperty(SERVICE_CLUSTER_NAME_PROPERTY_ID, clusterName);
      service.setProperty(SERVICE_SERVICE_NAME_PROPERTY_ID, serviceName);
      addResource(service);
    }
  }
}