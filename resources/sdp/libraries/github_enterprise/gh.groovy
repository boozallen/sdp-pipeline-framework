/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import org.kohsuke.github.*

def call() {
  withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'PAT', usernameVariable: 'USER')]) {
    def ghUrl = "${env.GIT_URL.split("/")[0..-3].join("/")}/api/v3"
    return org.kohsuke.github.GitHub.connectToEnterprise(ghUrl, PAT)
  }
}

