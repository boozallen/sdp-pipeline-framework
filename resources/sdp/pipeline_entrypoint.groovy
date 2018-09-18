/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

@Library("solutions_delivery_platform") 
import sdp.binding.*
import sdp.extensions.*

currentBuild.result = "SUCCESS" 
setBinding(new SdpBinding())

node{
  cleanWs()
  checkout scm 
  stash "workspace"
  stash name: "git-info",
        includes: ".git/**",
        useDefaultExcludes: false
  aggregate_pipeline_config()  
  pipeline_template = get_pipeline_template() 
}

load_libraries                   this
create_application_environments  this
create_stages                    this
create_jenkinsfile_variables     this
create_default_steps             this

getBinding().lock()

// execute pipeline
try{
  sdp_evaluate(pipeline_template, getBinding())
}catch(ex){
  currentBuild.result = "FAILURE"
  println ex
}

// run cleanup 
try{
  extensions.invoke CleanUp, getBinding()
}catch(any){
  println "there was a problem with one of the cleanup steps" 
}

// run notifiers
try{
  extensions.invoke Notifier, getBinding()
}catch(any){
  println "there was a problem with one of the notifier steps"
}
