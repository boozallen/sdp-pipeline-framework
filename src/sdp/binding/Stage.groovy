/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.binding 

import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;

/*
    represents a group of library steps to be called. 
*/
class Stage implements SdpBindingItem{
    def script 
    String name
    def steps 

    Stage(script, String name, steps){
        this.script = script
        this.name = name
        this.steps = steps 
    }

    void call(){
        script.steps.echo "[SDP] Executing Stage ${name}" 
        steps.each{ step -> 
            script.invokeMethod(step, null);
        }
    }

    void throwPreLockException(){
        throw new SdpBindingException("The Stage ${name} is already defined.")
    }

    void throwPostLockException(){
        throw new SdpBindingException("The variable ${name} is reserved as a SDP Stage.")
    }

}