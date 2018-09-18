/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.config

import org.codehaus.groovy.control.CompilerConfiguration
import org.kohsuke.groovy.sandbox.SandboxTransformer
import groovy.lang.Script
import java.util.ArrayList

class SdpConfigDsl implements Serializable{
  
  static SdpConfig parse(String script_text){
    SdpConfig sdp_config = new SdpConfig()
    Binding our_binding = new Binding(sdp_config: sdp_config)
    CompilerConfiguration cc = new CompilerConfiguration()
    cc.addCompilationCustomizers(new SandboxTransformer())
    cc.scriptBaseClass = SdpConfigBuilder.class.name
    
    GroovyShell sh = new GroovyShell(SdpConfigDsl.classLoader, our_binding, cc);
    
    SdpConfigSandbox sandbox = new SdpConfigSandbox()
    sandbox.register();
    try {
      sh.evaluate script_text
    }finally {
      sandbox.unregister();
    }
    
    return sdp_config
  }

}
