/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.binding

import sdp.config.SdpConfigException
import groovy.lang.Binding
import org.jenkinsci.plugins.workflow.cps.CpsThread
import org.jenkinsci.plugins.workflow.cps.DSL
import org.codehaus.groovy.runtime.InvokerHelper
import com.cloudbees.groovy.cps.NonCPS


class SdpBinding extends Binding implements Serializable{
    public final String STEPS_VAR = "steps"
    public def registry = [] as Set
    private Boolean locked = false

    SdpBinding(){
        CpsThread c = CpsThread.current()
        if (c) setVariable(STEPS_VAR, new DSL(c.getExecution().getOwner()))
    }

    public void lock(){ locked = true }

    @Override
    @NonCPS
    public void setVariable(String name, Object value) {
        if (name in registry){
            if (locked) variables.get(name).throwPostLockException()
            else variables.get(name).throwPreLockException()
        }
        if (value in SdpBindingItem) registry << name
        super.setVariable(name, value)
    }

    @Override
    @NonCPS
    public Object getVariable(String name){
        if (!variables)
            throw new MissingPropertyException(name, this.getClass());

        Object result = variables.get(name)

        if (!result && !variables.containsKey(name))
            throw new MissingPropertyException(name, this.getClass());

        if (result in SdpBindingItem && InvokerHelper.getMetaClass(result).respondsTo(result, "getValue", (Object[]) null))
            result = result.getValue()

        return result
    }
}
