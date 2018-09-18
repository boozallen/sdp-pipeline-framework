/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.SdpBinding

import org.jenkinsci.plugins.workflow.cps.CpsThreadGroup
import org.jenkinsci.plugins.workflow.cps.CpsGroovyShell
import java.lang.reflect.Field

/*
    We do a lot of executing the code inside files and we're also
    dependent on the binding being preserved (as it stores step impls).

    The default CpsGroovyShell leveraged in CpsScript's evaluate method
    is insufficient for our needs because it instantiates each shell with
    a new Binding() instead of using getBinding().

    Of course, there's no setContext() method on GroovyShell or 
    CpsGroovyShell to override the binding used in the constructor, 
    so we've gotta use reflection to override it directly. 

    /rant
*/
def call(String script, SdpBinding b){
    def shell = CpsThreadGroup.current().getExecution().getShell()
    Field contextF = GroovyShell.class.getDeclaredField("context")
    contextF.setAccessible(true)
    contextF.set(shell, b)
    shell.evaluate script 
}