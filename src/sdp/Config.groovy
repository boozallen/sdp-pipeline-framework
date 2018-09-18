/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp

import org.codehaus.groovy.control.CompilerConfiguration
import org.kohsuke.groovy.sandbox.SandboxTransformer
import org.kohsuke.groovy.sandbox.GroovyValueFilter
import org.kohsuke.groovy.sandbox.GroovyInterceptor
import org.kohsuke.groovy.sandbox.GroovyInterceptor.Invoker
import groovy.lang.Script

class Config{
  def config = [:]
  def object_stack = []
  def node_stack = []
  def merge = [ "libraries" ]
  def override = []
  
  /*
    would make this the constructor instead of an instance method
    but CPS limitations don't allow constructors. 
  */
  @NonCPS
  Config parse (script, String script_text){
    
    def instance = new Config()
    def cc = new CompilerConfiguration()
    cc.addCompilationCustomizers(new SandboxTransformer())
    cc.scriptBaseClass = CustomScript.class.name
    def our_binding = new Binding(
      node_stack:   instance.node_stack,
      object_stack: instance.object_stack,
      config:       instance.config,
      merge:        instance.merge,
      override:     instance.override,
      script: script
    )
    GroovyShell sh = new GroovyShell( this.class.classLoader, our_binding, cc);
    
    def sandbox = new ConfigSandbox()
    sandbox.register();
    try {
        sh.evaluate script_text
    } finally {
        sandbox.unregister();
    }
    
    return instance
  }
}

abstract class CustomScript extends Script{
  @NonCPS
  Object methodMissing(String name, args){
    object_stack.push([:])
    node_stack.push(name)

    args[0]()

    def node_config = object_stack.pop()
    def node_name = node_stack.pop()

    if (object_stack.size()) 
      object_stack.last() << [ (node_name): node_config ]
    else 
      config << [ (name): node_config]
  }
  @NonCPS
  void setProperty(String name, value){
    if (name.equals("merge") && value.equals(true)) 
      merge.push(node_stack.join("."))
    else if (name.equals("override") && value.equals(true))
      override.push(node_stack.join("."))
    else if (object_stack.size()) 
      object_stack.last()[name] = value
    else 
      config[name] = value
  }
  @NonCPS
  void propertyMissing(String name){
    if (object_stack.size()) 
      object_stack.last()[name] = [:]
    else 
      config[name] = [:]
  }
}

class ConfigSandbox extends GroovyInterceptor {
  @Override
  @NonCPS
  Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
    if (!(receiver instanceof Script)){
      throw new SecurityException("""
        invoker -> ${invoker}
        receiver -> ${receiver}
        method -> ${method}
        args -> ${args}
      """)
    }
    return invoker.call(receiver,method,args);
  }

  @Override
  @NonCPS
  Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
    throw new SecurityException("""
      invoker -> ${invoker}
      receiver -> ${receiver}
      method -> ${method}
      args -> ${args}
    """)
  }
  
  @Override
  @NonCPS
  public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
    if (!receiver.equals(CustomScript.class) && !(receiver instanceof Script)){
      throw new SecurityException("""
        invoker -> ${invoker}
        receiver -> ${receiver}
        args -> ${args}
      """)
    }
  }
  
  @Override
  @NonCPS
  public Object onSuperCall(Invoker invoker, Class senderType, Object receiver, String method, Object... args) throws Throwable {
    throw new SecurityException("""
      invoker -> ${invoker}
      senderType -> ${senderType}
      receiver -> ${receiver}
      method -> ${method}
      args -> ${args}
    """)
  }

  @Override
  @NonCPS
  public void onSuperConstructor(Invoker invoker, Class receiver, Object... args) throws Throwable {
    if (!receiver.equals(CustomScript.class)){
      throw new SecurityException("""
        invoker -> ${invoker}
        receiver -> ${receiver}
        args -> ${args}
      """)
    }
  }

  @Override
  @NonCPS
  public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
    if (!(receiver instanceof Script)){
      throw new SecurityException("""
        invoker -> ${invoker}
        receiver -> ${receiver}
        property -> ${property}
      """)
    }
    return invoker.call(receiver,property);
  }

  @Override
  @NonCPS
  public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
    if (!(receiver instanceof Script)){
      throw new SecurityException("""
        invoker -> ${invoker}
        receiver -> ${receiver}
        method -> ${method}
        args -> ${args}
      """)
    }
    return invoker.call(receiver,property,value);
  }

  @Override
  @NonCPS
  public Object onGetAttribute(Invoker invoker, Object receiver, String attribute) throws Throwable {
    throw new SecurityException("""
      invoker -> ${invoker}
      receiver -> ${receiver}
      attribute -> ${attribute}
    """)
  }

  @Override
  @NonCPS
  public Object onSetAttribute(Invoker invoker, Object receiver, String attribute, Object value) throws Throwable {
    throw new SecurityException("""
      invoker -> ${invoker}
      receiver -> ${receiver}
      attribute -> ${attribute}
      value -> ${value}
    """)
  }

  @Override
  @NonCPS
  public Object onGetArray(Invoker invoker, Object receiver, Object index) throws Throwable {
    throw new SecurityException("""
      invoker -> ${invoker}
      receiver -> ${receiver}
      index -> ${index}
    """)
  }
  
  @Override
  @NonCPS
  public Object onSetArray(Invoker invoker, Object receiver, Object index, Object value) throws Throwable {
    throw new SecurityException("""
      invoker -> ${invoker}
      receiver -> ${receiver}
      index -> ${index}
      value -> ${value}
    """)
  }
}
