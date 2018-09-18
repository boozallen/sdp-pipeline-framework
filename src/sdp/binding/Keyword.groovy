/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.binding

import sdp.config.SdpConfigException

/*
    represents a protected variable in the jenkinsfile
*/
class Keyword implements SdpBindingItem{
    String var_name
    Object value


    Keyword(String var_name, Object value){ 
        this.var_name = var_name 
        this.value = value
    }

    void throwPreLockException(){
        throw new SdpBindingException("Keyword ${var_name} already defined.")
    }

    void throwPostLockException(){
        throw new SdpBindingException("Variable ${var_name} is reserved as an SDP Keyword.")
    }



}