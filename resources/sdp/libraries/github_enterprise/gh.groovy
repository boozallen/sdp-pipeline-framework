/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import org.kohsuke.github.*

def call() {

  def credId = scm.getUserRemoteConfigs()[0]?.getCredentialsId() ?:
               {echo "Could not find CredentialId. Using default ID \"github\""; 'github'}


  withCredentials([usernamePassword(credentialsId: credId, passwordVariable: 'PAT', usernameVariable: 'USER')]) {
    def ghUrlBase - "${env.GIT_URL.split("/")[0..-3].join("/")}"
    if (ghUrlBase =~ /https?:\/\/github\.com/ ) {
      def ghUrl = "https://api.github.com"
    } else {
      def ghUrl = "${env.GIT_URL.split("/")[0..-3].join("/")}/api/v3"
    }
    return org.kohsuke.github.GitHub.connectToEnterprise(ghUrl, PAT)
  }
}
