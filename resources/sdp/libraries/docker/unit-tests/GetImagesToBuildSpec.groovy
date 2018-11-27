import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification

public class GetImagesToBuildTestSpec extends JenkinsPipelineSpecification {

  def GetImagesToBuildTest = null

  public static class DummyException extends RuntimeException {
    public DummyException(String _message) { super( _message ); }
  }

  def setup() {
    GetImagesToBuildTest = loadPipelineScriptForTest("sdp/libraries/docker/get_images_to_build.groovy")
    explicitlyMockPipelineVariable("pipeline_config")
  }

  def "Missing application_image_repository Throws Error" () {
    setup:
      GetImagesToBuildTest.getBinding().setVariable("config", [:])
      getPipelineMock("pipeline_config.call")() >> [application_image_repository: null]
    when:
      GetImagesToBuildTest()
    then:
      1 * getPipelineMock("error")("application_image_repository not defined in pipeline config.")
  }

  def "Invalid build_strategy Throws Error" () {
    setup:
      GetImagesToBuildTest.getBinding().setVariable("config", [build_strategy: x])
      getPipelineMock("pipeline_config.call")() >> [application_image_repository: "Enterprise"]
    when:
      GetImagesToBuildTest()
    then:
      y * getPipelineMock("error")("build strategy: ${x} not one of [docker-compose, modules, dockerfile]")
    where:
      x                | y
      "docker-compose" | 0
      "Kobayashi Maru" | 1
      "modules"        | 0
      "dockerfile"     | 0
      "Starfleet"      | 1
  }

  def "docker-compose build_strategy Throws Error" () {
    setup:
      GetImagesToBuildTest.getBinding().setVariable("config", [build_strategy: "docker-compose"])
      getPipelineMock("pipeline_config.call")() >> [application_image_repository: "Enterprise"]
    when:
      GetImagesToBuildTest()
    then:
      1 * getPipelineMock("error")("docker-compose build strategy not implemented yet")
  }

  def "modules build_strategy Builds Correct Image List" () {
    setup:
      GetImagesToBuildTest.getBinding().setVariable("config", [build_strategy: "modules"])
      GetImagesToBuildTest.getBinding().setVariable("env", [REPO_NAME: "Vulcan", GIT_SHA: "1234abcd"])
      getPipelineMock("findFiles")([glob: "*/Dockerfile"]) >> [[path: "planet/Romulus"], [path: "planet2/Earth"]]
      getPipelineMock("pipeline_config.call")() >> [application_image_repository: "Enterprise"]
    when:
      def imageList = GetImagesToBuildTest()
    then:
      imageList == [
        [
          repo: "Enterprise",
          path: "Vulcan_planet",
          context: "planet",
          tag: "1234abcd"
        ], [
          repo: "Enterprise",
          path: "Vulcan_planet2",
          context: "planet2",
          tag: "1234abcd"
        ]
      ]

  }

  def "dockerfile build_strategy Builds Correct Image List" () {
    setup:
      GetImagesToBuildTest.getBinding().setVariable("config", [build_strategy: "dockerfile"])
      GetImagesToBuildTest.getBinding().setVariable("env", [REPO_NAME: "Vulcan", GIT_SHA: "5678efgh"])
      getPipelineMock("findFiles")([glob: "*/Dockerfile"]) >> [[path: "planet/Romulus"]]
      getPipelineMock("pipeline_config.call")() >> [application_image_repository: "Enterprise"]
    when:
      def imageList = GetImagesToBuildTest()
    then:
      imageList == [[repo: "Enterprise", path: "Vulcan", context: ".", tag: "5678efgh"]]
  }

}
