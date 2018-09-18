/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.binding.*
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import java.util.ArrayList
import java.lang.annotation.Annotation

/*
    returns an array of maps: 
    [
        library: the library contributing this step 
        impl: the step contributed,
        name: the name of the step 
        method: the method within the step annotated 
    ]
*/
def discover(Class annotation, SdpBinding b){
    if (!(annotation in Annotation))
        error "${annotation} is not an Annotation"

    ArrayList discovered = []
    b.getVariables().collect{ it.value }.findAll{ it instanceof StepWrapper }.each{ step ->
        step.impl.class.methods.each{ m ->
            if (m.getAnnotation(annotation)){
                discovered.push([
                    library: step.library,
                    impl: step.impl,
                    name: step.name,
                    method: m.name 
                ])
            }            
        }
    }
    return discovered
}

def invoke(annotation, SdpBinding b, sdp_context = [:]){
    def discovered = discover(annotation, b)
    if (discovered){
        discovered.each{ step -> 
            try{
                println "[${annotation.getSimpleName()}] Running ${step.method} method within ${step.name} step contributed by the ${step.library} Library"
                InvokerHelper.getMetaClass(step.impl).invokeMethod(step.impl, step.method, sdp_context);
            } catch (Exception x) {
                throw new InvokerInvocationException(x);
            } finally {
                sdp_context = [:]
            }
        }
    }
}