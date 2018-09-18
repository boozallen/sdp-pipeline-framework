/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp

import groovy.lang.Binding
import org.jenkinsci.plugins.workflow.cps.CpsThread
import org.jenkinsci.plugins.workflow.cps.DSL

/*
    Custom binding implementation to protect library
    steps from overwriting each other in the step binding.

    Also has the benefit of allowing us to record all steps
    contributed by libraries (be they SDP or external) and 
    record them in a step registry for logging/debugging. 
*/
class SdpBinding extends Binding{
    public final String STEPS_VAR = "steps"
    public def step_registry = [:]

    /*
        Following the implementation in CpsScript, the binding
        initializes a STEPS_VAR variable representing the jenkins
        pipeline DSL class with default steps like "node" or "sh" 

        the correct way to override the binding implementation is
        setBinding(new SdpBinding().initialize())
    */
    public SdpBinding initialize(){
        CpsThread c = CpsThread.current()
        if (c) setVariable(STEPS_VAR, new DSL(c.getExecution().getOwner()))
        return this
    }

    @Override
    @NonCPS
    public void setVariable(String name, Object value, String library = null) {
        /*
            this is a hack. in order to prevent library steps contributed by
            external shared libraries from being accidentally overridden, they 
            are added to the step registry which would effectively prevent their
            value from being set in the future. 

            The problem is that instantiations of the UserDefinedGlobalVariable into 
            the binding isn't performed until the first time the step is executed.

            This class of this instance will be that of the step name. SO to bypass
            this restriction, we only check for collisions if the class of a value 
            of a variable to be set is named something other than the variable name
            to be stored in the binding.  

            .. let's hope no one creates a class named "build" and tries to instantiate
            this class into a variable named "build".  i'm fine with this edge case. 
        */
        if (!value.getClass().getName().equals(name)) preventCollision(name, library) 
        if (library) addStep(name, library)
 
        /*
          TODO: 
            if Jenkins CPS ever allows ComposedClosures, we can better implement before/after 
            AOP style functionality in SDP by allowing libraries to register before/after 
            methods and leveraging closure composition via value << before and value >> after.

            for now - any implementation of this sort of functionality would need to be provided
            in load_libraries() when the transformed implementation is evaluated. 
        */ 
        super.setVariable(name, value)
    }

    /*
        Validates the variable being set is not already defined via a library.
        Two different exception messages are to differentiate between an error
        during library loading of steps colliding versus a library developer creating
        a variable that would overwrite a step. 
    */
    @NonCPS 
    private void preventCollision(name, library){
        if (step_registry.containsKey(name)){
            if (library){
                throw new SdpLibraryException("Library Step Collision. The step ${name} already defined via the ${step_registry.get(name)} library.")
            } else {
                throw new SdpLibraryException("Library Step Collision. The variable ${name} is reserved as a library step via the ${step_registry.get(name)} library.")
            }
        }
    }
    
    /*
        Used to register steps from external libraries into the step registry
    */
    @NonCPS
    public void addStepToRegistry(String name, String library){
        preventCollision(name, library) 
        addStep(name,library)
    }

    /*
        Actually inserts the step into the step registry
    */
    @NonCPS
    private void addStep(String name, String library){
        step_registry[name] = library
    }

}