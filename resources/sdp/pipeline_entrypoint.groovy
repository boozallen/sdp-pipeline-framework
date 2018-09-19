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
  /*
    TODO: 
      this should probably be a stash_scm_info() step
      contributed by whatever SCM library is loaded. 
  */
  stash name: "git-info",
        includes: ".git/**",
        useDefaultExcludes: false
  aggregate_pipeline_config()  
  /*
    TODO: 
      The pipeline template needs to also become an
      SdpBindingItem so that library constructors can't
      override it by declaring pipeline_template = "whatever"
  */
  pipeline_template = get_pipeline_template() 
}

/*
  These steps could probably be a part of the
  Config File DSL functionality. but right now it's cleaner
  to separate the capabilities of the DSL from what we choose
  to do while parsing the aggregated configuration.
*/
load_libraries                   this
create_application_environments  this
create_stages                    this
create_jenkinsfile_variables     this
create_default_steps             this

getBinding().lock()

// execute pipeline template
try{
  sdp_evaluate(pipeline_template, getBinding())
}catch(ex){
  currentBuild.result = "FAILURE"
  println ex
}

/*
  these were rushed.  can look into parallel
  execution of registered extensions and try
  catching individual method calls to make sure 
  all steps get executed. 
*/
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
