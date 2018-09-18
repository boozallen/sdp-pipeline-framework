/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.config.*

def call(script){
  
  // tenant must have a pipeline_config.groovy 
  if (!fileExists("pipeline_config.groovy"))
    error "pipeline_config.groovy not found" 

  // load tenant configuration 
  SdpConfig tenant = new SdpConfigDsl().parse(readFile("pipeline_config.groovy")) 
    
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
  SdpConfig organization = new SdpConfigDsl().parse(libraryResource("sdp-org/pipeline_config.groovy"))
  
  PipelineConfig.join(organization)
  PipelineConfig.join(tenant)

  writeYaml data: pipeline_config(), file: "aggregated_pipeline_config.yaml"
  archive "aggregated_pipeline_config.yaml"

}