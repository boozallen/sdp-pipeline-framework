/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.binding

import sdp.extensions.*
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;

/*
    represents a library step
*/
class StepWrapper implements SdpBindingItem{
    def impl
    def script
    String name
    String library 

    StepWrapper(script, impl, name, library){ 
        this.script = script  
        this.impl = impl
        this.name = name
        this.library = library 
    }

    def call(Object... args){
        def result 
        def sdp_context = [
            step: name, 
            library: library,
            status: script.currentBuild.result
        ]
        try{
            script.extensions.invoke BeforeStep, script.getBinding(), sdp_context
            script.steps.echo "[SDP] Executing step ${name} from the ${library} Library" 
            result = InvokerHelper.getMetaClass(impl).invokeMethod(impl, "call", args);
        } catch (Exception x) {
            script.currentBuild.result = "Failure"
            throw new InvokerInvocationException(x);
        } finally{
            script.extensions.invoke AfterStep, script.getBinding(), sdp_context
            script.extensions.invoke Notifier,  script.getBinding(), sdp_context
        }
        return result
    }

    void throwPreLockException(){
        throw new SdpBindingException("Library Step Collision. The step ${name} already defined via the ${library} library.")
    }
    void throwPostLockException(){
        throw new SdpBindingException("Library Step Collision. The variable ${name} is reserved as a library step via the ${library} library.")
    }
}

