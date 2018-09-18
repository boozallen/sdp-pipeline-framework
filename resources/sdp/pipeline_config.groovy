/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

use_pipeline_template    = false


application_image_repository = "dtr.microcaas.net" 
application_image_repository_credential = "app-registry" 

sdp_image_repository = "dtr.microcaas.net"
sdp_image_repository_credential = "sdp-registry"

keywords{
    master  =  /^[Mm]aster$/
    develop =  /^[Dd]evelop(ment|er|)$/ 
    hotfix  =  /^[Hh]ot[Ff]ix-/ 
    release =  /^[Rr]elease-(\d+.)*\d$/
}

pipeline_template_methods{
    unit_test
    static_code_analysis
    build    
    scan_container_image
    penetration_test
    accessibility_compliance_test
    performance_test
    functional_test
}

steps
