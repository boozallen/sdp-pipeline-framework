/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.binding.ApplicationEnvironment
/*
  Takes each key in application_environment and creates a variable
  in the script binding with the name of the key whose value is
  the key's value as an immutable hashmap. 
*/
void call(script){
  pipeline_config().application_environments.each{ name, config ->
    script.getBinding().setVariable(name, new ApplicationEnvironment(name, config))
  }
}
