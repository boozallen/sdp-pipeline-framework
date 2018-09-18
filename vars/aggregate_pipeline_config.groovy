/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.Config
import sdp.PipelineConfig
/* 
    aggregates the SDP, Organization, and Tenant configurations. 
    
    Organization's can choose to have any key be overridable or
    mergeable. 
    
    When overriden - the tenant configuration will replace the
    organizations.
    
    When mergeable - the key will be merged with the organization's
    and keys defined by the organization will take precedence.
*/
def call(script){
  
  // load SDP default configuration 
  def sdp = new Config().parse(script, libraryResource("sdp/pipeline_config.groovy"))
  
  // tenant must have a pipeline_config.groovy 
  if (!fileExists("pipeline_config.groovy"))
    error "pipeline_config.groovy not found" 

  // load tenant configuration 
  def tenant = new Config().parse(script, readFile("pipeline_config.groovy")) 
    
  /*
    if no organization was specified, the aggregated
    configuration is the SDP default with the tenant's 
    libraries. 
  */
  def organization_name = tenant.config?.organization
  if (!organization_name){
    pipeline_config = sdp.config + [ libraries: (tenant.config.libraries ?: [:]) ]
    return pipeline_config
  }
  
  /*
    load organization's configuration
    the configuration is expected to be located at: /resources/sdp-org/pipeline_config.groovy
  */
  library organization_name  
  def organization = new Config().parse(script, libraryResource("sdp-org/pipeline_config.groovy"))
  
  /*
    begin to aggregate pipeline configurations: sdp, organization, and tenant
    They will be aggregated on this pipeline_config object. 
  */
  def pipeline_config = tenant.config + sdp.config + organization.config

  /*
    handle tenant overriding of organization configurations if allowable
    For each organization key that was specified to be overridable, 
    if the tenant has defined that key then remove the key from the 
    aggregated configuration and replace it with the tenants value
  */
  organization.override.each{ key ->
    if (get_prop(tenant.config, key)){
      clear_prop(pipeline_config, key)
      get_prop(pipeline_config, key) << get_prop(tenant.config, key) 
    }
  }
  
  /*
    handle merging of tenant configurations into organizational configurations
    if allowable. For each organization key that was specified as mergeable, 
    check if the tenant has defined that key.  If they have, append the tenants
    additional configurations to the configuration. 
  */
  organization.merge.each{ key ->
    if (get_prop(tenant.config, key)){
      get_prop(pipeline_config, key) << (get_prop(tenant.config, key) + get_prop(pipeline_config, key))
    }
  }

  /*
    write the aggregated configuration to a file in the workspace and archive it.
    This is primarily done for troubleshooting to validate the aggregated configuration
    is what's expected by the tenant/platform administrators. 
  */
  writeYaml data: pipeline_config, file: "aggregated_pipeline_config.yaml"
  archive "aggregated_pipeline_config.yaml"

  /*
    save the aggregated pipeline configuration in memory on the PipelineConfig
    object, so that we don't have to either:
    1. do this aggregation again OR 
    2. read the aggregated yaml configuration 
    every time we want to get the configuration. 

    Future calls to this configuration can leverage the pipeline_config() pipeline step
    which will return the aggregated config. 
  */
  PipelineConfig.instance.setConfig(pipeline_config.asImmutable())
}

/*
  params: 
    o:  An object accessible via dot notation (a hashmap) 
    p:  A property to access on object o in dot notation
        For example, "a.b.c.d"  would access "o.a.b.c.d" 

  returns: the property p on object o

  This method works by tokenizing p based on the "." notation 
  and iterating on the subsequent array using the inject method
  to recursively access the next property. 

  null is returned if the property p on object o does not exist. 
*/
def get_prop(o, p){
  return p.tokenize('.').inject(o){ obj, prop ->       
    obj?."$prop"
  }   
}

/*
  params: 
    o:  An object accessible via dot notation (a hashmap) 
    p:  A property to access on object o in dot notation
        For example, "a.b.c.d"  would access "o.a.b.c.d" 

  returns: nothing

  This method works by tokenizing p based on the "." notation 
  and iterating on the subsequent array using the inject method
  to recursively access the next property until clearing the
  contents of that property. 
*/
void clear_prop(o, p){
  /*
    we need the last token so when recursing, we know the base
    case to clear the value. if when tokenizing on "." we get null,
    that means the entire object should be cleared. 
  */
  if (p.tokenize('.')) last_token = p.tokenize('.').last()
  else o.clear()
  p.tokenize('.').inject(o){ obj, prop ->    
    if (prop.equals(last_token)) obj?."$prop".clear()
    obj?."$prop"
  }   
}
