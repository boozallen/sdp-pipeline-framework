/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.config.*

/*
  the logic to aggregate the organizational hierarchy of configs.

  right now.. tenants specify their org (bad) 
  soon.. job hierarchy = org hierarchy: 
  
  first folder in the hierarchy will have their pipeline_configuration
  repository loaded as a library implicitly

  folder structure within that library reflect the same hierarchy so 
  config files can be found.

  a better way might be to add a property to the Folder class in Jenkins
  to put the config files right on the folders themselves.

  TBD

*/
def call(script){
  
  // tenant must have a pipeline_config.groovy 
  if (!fileExists("pipeline_config.groovy"))
    error "pipeline_config.groovy not found" 

  // load tenant configuration 
  SdpConfig tenant = SdpConfigDsl.parse(readFile("pipeline_config.groovy")) 
    
  /*
    if no organization was specified, the aggregated
    configuration is the SDP default with the tenant's 
    libraries. 
  */
  def organization_name = tenant.config?.organization
  if (!organization_name){
    error "you must define your organization"
  }

  library organization_name  
  SdpConfig organization = SdpConfigDsl.parse(libraryResource("sdp-org/pipeline_config.groovy"))
  
  PipelineConfig.join(organization)
  PipelineConfig.join(tenant)

  writeYaml data: pipeline_config(), file: "aggregated_pipeline_config.yaml"
  archive "aggregated_pipeline_config.yaml"

}