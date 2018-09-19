/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.config 

import java.util.ArrayList

/*
    Base class during Config File DSL execution.
    Basically just turns the nested closure syntax
    into a nested hash map while recognizing the keys
    "merge" and "override" to put onto the SdpConfig object

    the sdp_config variable here comes from the instance
    being created and is instantiated in SdpConfigDsl
*/
abstract class SdpConfigBuilder extends Script{
    ArrayList object_stack = []
    ArrayList node_stack = []

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
            sdp_config.config << [ (name): node_config]
    }

    @NonCPS
    void setProperty(String name, value){
        if (name.equals("merge") && value.equals(true))
            sdp_config.merge.push(node_stack.join("."))
        else if (name.equals("override") && value.equals(true)) 
            sdp_config.override.push(node_stack.join("."))
        else if (object_stack.size()) 
            object_stack.last()[name] = value
        else 
            sdp_config.config[name] = value
    }

    @NonCPS
    void propertyMissing(String name){
        if (object_stack.size()) object_stack.last()[name] = [:]
        else sdp_config.config[name] = [:]
    }

}