/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.binding.*
import org.jenkinsci.plugins.workflow.cps.GlobalVariable

/*
  defines steps not already defined by a library.
  uses the step org_default_step_implementation if defined
  otherwise uses SDP's default_step_implementation

  a Closure variable named after the step is injected into
  the binding and makes a call to the default implementation.

  it's up to the default implementation to take the step name
  and do something with it.
*/
void call(script){

  default_steps = []

  m = pipeline_config().pipeline_template_methods ? pipeline_config().pipeline_template_methods.keySet() : null
  if (m) default_steps += m

  s = pipeline_config().steps ? pipeline_config().steps.keySet() : null
  if (s) default_steps += s

  default_steps.each{ step ->
    def step_impl 
    def origin 
    if (library_step_exists("org_default_step_implementation")){
      if (!library_step_exists(script, step)){
        origin = "Organizational Default Step Implementation"
        step_impl = {
          org_default_step_implementation(step)
        }
      }
    } else{
      if (!library_step_exists(script, step)){
        origin = "SDP Default Step Implementation"
        step_impl = {
          default_step_implementation(step)
        }
      }
    }
    def step_wrapper = new StepWrapper(script, step_impl, step, origin)
    script.getBinding().setVariable(step, step_wrapper)
  }
}

def library_step_exists(script = null, String step){
  if (GlobalVariable.byName(step, $buildNoException())) return true
  if (script) if (script.binding.hasVariable(step)) return true
  else return false
}
