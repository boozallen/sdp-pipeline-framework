/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp

/*
  stores the aggregated & immutable pipeline configuration. 
*/
@Singleton
class PipelineConfig implements Serializable{
    private def config
    public void setConfig(_config){
      if (this.config) throw new SdpConfigException("Runtime modifications to the PipelineConfig are not permitted.")
      this.config = _config
    }    
}
