/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.config

import org.kohsuke.groovy.sandbox.GroovyValueFilter
import org.kohsuke.groovy.sandbox.GroovyInterceptor
import org.kohsuke.groovy.sandbox.GroovyInterceptor.Invoker

class SdpConfigSandbox extends GroovyInterceptor {
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
    /*if (!(receiver instanceof SdpConfigBuilder)){
      throw new SecurityException("""
        invoker -> ${invoker}
        receiver -> ${receiver}
        args -> ${args}
      """)
    }*/
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
    if (!(receiver instanceof SdpConfigBuilder)){
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
