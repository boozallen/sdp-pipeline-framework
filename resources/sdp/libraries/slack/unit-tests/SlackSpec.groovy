import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification

public class SlackSpec extends JenkinsPipelineSpecification {

  def SlackTest = null

  def setup() {
    SlackTest = loadPipelineScriptForTest("sdp/libraries/slack/slack.groovy")
    explicitlyMockPipelineStep("echo")
  }

  def "Successful Build Sends Success Result" () {
    setup:
      SlackTest.getBinding().setVariable("currentBuild", [ result: "SUCCESS" ])
    when:
      SlackTest()
    then:
      1 * getPipelineMock("slackSend")(_ as Map) >> { _arguments ->
        assert _arguments[0]["message"] =~ /Build Successful:.*/
      }
  }

  def "Failed Build Sends Fail Result" () {
    setup:
      SlackTest.getBinding().setVariable("currentBuild", [ result: "FAILURE" ])
    when:
      SlackTest()
    then:
      1 * getPipelineMock("slackSend")(_ as Map) >> { _arguments ->
        assert _arguments[0]["message"] =~ /Build Failure:.*/
      }
    }

  def "Other Builds Send No Result" () {
    setup:
      SlackTest.getBinding().setVariable("currentBuild", [ result: "ILLOGICAL" ])
      explicitlyMockPipelineVariable("out") //not sure why, but this tests fails w/o this mock
    when:
      SlackTest()
    then:
      0 * getPipelineMock("slackSend")(_)
  }

}
