/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

void call(String step){

  if (!(step in pipeline_config().steps.keySet())) return


  error_msg = """
     step ${step} not defined in Pipeline Config
     step definition specification:

     steps{
       unit_test{
         stage = "Unit Test"            // optional. display name for step. defaults to $step
         image = "maven"                // required. docker image to use for testing
         command = "mvn clean verify"   // either command or script
         script = ./tests/unit_test.sh  // not both. one required.
         stash{                         // stash is optional
           name = "test-results"        // stash name required if stash is configured
           includes = "./target"        // optional. defaults to everything in pwd
           excludes = "./src"           // optional. defaults to nothing
           useDefaultExcludes = false   // optional. defaults to true
           allowEmpty = true            // optional. defaults to false
         }
       }
     }
     """

  // get step configuration
  def config = pipeline_config().steps.get(step)

  stage(config.stage ?: step){
    // get docker image for step
    def img = config.image ?:
                { error "Image not defined for ${step}. \n ${error_msg}" }()

    // validate only one of command or script is set
    if (!config.subMap(["command", "script"]).size().equals(1)){
      error error_msg
    }

    // get command to run inside image
    String script_text
    if (config.command)
      script_text = config.command

    if (config.script)
    if (fileExists(config.script))
      script_text = readFile config.script
    else
      error "Script ${config.script} not found"

    // execute step
    docker.image(img).inside{
      unstash "workspace" 
      sh script_text

      // stash results if configured
      def s = config.stash
      if (s){
        // validate stash configuration
        def n = s.name ?: {error "Step ${step} stash name not configured: \n ${error_msg}"}()
        def i = s.includes ?: "**"
        def e = s.excludes ?: " "
        def d = s.useDefaultExcludes ?: true
        def p = s.allowEmpty ?: false

        stash name: n,
              includes: i,
              excludes: e,
              useDefaultExcludes: d,
              allowEmpty: p
      }
    }
  }
}

