/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.binding.Stage
/*
  Let's users define pipeline stages
  
  stages{
    continuous_integration{
      compile
      package
      unit_test
      static_code_analysis
    }
  }
  
  would make continuous_integration() available in the pipeline template
  and would call the steps listed in order. 
*/
void call(script){
  pipeline_config().stages.each{name, steps ->
    script.getBinding().setVariable(name, new Stage(script, name, steps.keySet()))
  }
}
