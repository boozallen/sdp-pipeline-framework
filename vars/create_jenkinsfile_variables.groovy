/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

/*
  an opportunity to provide keywords to the pipeline template (Jenkinsfile)
  each key/value in keywords will be injected into the script binding as a variable
*/
void call(script){
  pipeline_config().keywords.each{ key, value ->
    script.binding.setVariable(key, value)  
  }
}
