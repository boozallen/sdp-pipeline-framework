/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

def call(){
  def config = pipeline_config()
  
  /*
     if tenants are allowed to use Jenkinsfiles defined
     in their repositories - check for one and use it
     if present.
  */
  if (fileExists("Jenkinsfile")){
    if (config.allow_tenant_jenkinsfile){
      return readFile("Jenkinsfile")
    }else{
      println "Warning: Tenant provided Jenkinsfile that will not be used, per organizational policy."
    }
  }
  
  /*
    orgs can define multiple pipeline templates in: /resources/sdp-org/pipeline_templates
    if tenant has specified a particular template, use it. 
    if the template doesn't exist, err out. 
  */
  if (config.pipeline_template){ 
    try{
      return libraryResource("sdp-org/pipeline_templates/${config.pipeline_template}")
    }catch(any){
      error "Pipeline Template ${config.pipeline_template} does not exist" 
    }
  }

  /*
    If organization has a default Jenkinsfile located at 
    /resources/sdp-org/Jenkinsfile then use that. 
  */
  try{
    return libraryResource("sdp-org/Jenkinsfile")
  }catch(any){}
   

  /*
    If you've gotten this far, default to SDP Jenkinsfile
  */
  return libraryResource("sdp/Jenkinsfile")

}
