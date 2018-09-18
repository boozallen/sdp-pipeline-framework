/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.binding;
import sdp.config.SdpConfigException

/*
    represents an immutable application environment. 
*/
class ApplicationEnvironment implements SdpBindingItem{
    String var_name
    String short_name
    String long_name
    final def config
    
    ApplicationEnvironment(String var_name, Map _config){ 
        this.var_name = var_name

        if (_config.short_name) short_name = _config.short_name
        else short_name = var_name

        if (_config.long_name) long_name = _config.long_name
        else long_name = var_name
        
        config = _config - _config.subMap(["short_name", "long_name"])
        /*
            TODO: 
                this makes it so that changing <inst>.config.whatever = <some value> 
                will throw an UnsupportOperationException.  Need to figure out how to 
                throw SdpConfigException instead for the sake of logging.
        */
        config = config.asImmutable()
    }
    
    Object getProperty(String name){
        def meta = ApplicationEnvironment.metaClass.getMetaProperty(name)
        if (meta) {
            meta.getProperty(this)
        } else {
            if (config.containsKey(name)) return config.get(name)
            else return null
        }
    }

    void setProperty(String name, Object value){
        throw new SdpConfigException("Can't modify Application Environment '${long_name}'. Application Environments are immutable.")
    }

    void throwPreLockException(){
        throw new SdpBindingException("Application Environment ${long_name} already defined.")
    }

    void throwPostLockException(){
        throw new SdpBindingException("Variable ${var_name} is reserved as an SDP Application Environment")
    }

}