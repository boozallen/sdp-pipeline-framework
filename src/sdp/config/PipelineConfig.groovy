/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.config

import org.jenkinsci.plugins.workflow.cps.CpsThread
import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.jenkinsci.plugins.workflow.cps.DSL

import java.util.ArrayList

/*
  stores the aggregated & immutable pipeline configuration. 
*/
class PipelineConfig implements Serializable{
    static PipelineConfig instance 
    static String DEFAULT_SDP_CONFIG = "sdp/pipeline_config.groovy"
    static DSL steps 
    SdpConfig current

    private PipelineConfig(){}

    static PipelineConfig getInstance(){
      steps.echo "instance -> ${instance}"
      if (!instance) initialize()
      return instance
    }

    static initialize(){
      instance = new PipelineConfig()
      CpsThread c = CpsThread.current()
      if (c) instance.steps = new DSL(c.getExecution().getOwner())
      else throw new SdpConfigException("current CpsThread is null.")
      instance.current = SdpConfigDsl.parse(steps.libraryResource(DEFAULT_SDP_CONFIG))
      instance.current.override = instance.current.config.keySet()
    }

    static void join(SdpConfig child){
      if (!instance) initialize()
      def pipeline_config = child.config + instance.current.config 

      instance.current.override.each{ key ->
        if (get_prop(child.config, key)){
          clear_prop(pipeline_config, key)
          get_prop(pipeline_config, key) << get_prop(child.config, key) 
        }
      }

      instance.current.merge.each{ key ->
        if (get_prop(child.config, key)){
          get_prop(pipeline_config, key) << (get_prop(child.config, key) + get_prop(pipeline_config, key))
        }
      }

      child.setConfig(pipeline_config)
      instance.current = child

    }

    static def get_prop(o, p){
      return p.tokenize('.').inject(o){ obj, prop ->       
        obj?."$prop"
      }   
    }

    static void clear_prop(o, p){
      def last_token
      if (p.tokenize('.')) last_token = p.tokenize('.').last()
      else o.clear()
      p.tokenize('.').inject(o){ obj, prop ->    
        if (prop.equals(last_token)) obj?."$prop".clear()
        obj?."$prop"
      }   
    }

}
