/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.config

import java.util.ArrayList

class SdpConfig implements Serializable{
    def config = [:]
    ArrayList merge = []
    ArrayList override = []


    /*
    ArrayList getMerge(){
        if(config.libraries) merge << getNestedLibraryKeys(config.libraries)
        return merge 
    }

    def getNestedLibraryKeys(map, result = [], String keyPrefix = 'libraries.') {
        map.each { key, value ->
            if (value instanceof Map) {
                result << "${keyPrefix}${key}"
                getNestedLibraryKeys(value, result, keyPrefix += "$key.")
                keyPrefix = 'libraries.'
            }
        }
        return result
    }
    */
}