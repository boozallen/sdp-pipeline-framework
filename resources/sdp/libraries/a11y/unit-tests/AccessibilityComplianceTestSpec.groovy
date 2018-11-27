import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification

public class AccessibilityComplianceTestSpec extends JenkinsPipelineSpecification {

  def AccessibilityComplianceTest = null

  def setup() {
    AccessibilityComplianceTest = loadPipelineScriptForTest("sdp/libraries/a11y/accessibility_compliance_test.groovy")
  }

  def "Scan Runs With Given URL" () {
    setup:
      AccessibilityComplianceTest.getBinding().setVariable("config", [ url: "https://www.example.com" ])
    when:
      AccessibilityComplianceTest()
    then:
      1 * getPipelineMock("sh")("a11ym -o accessibility_compliance https://www.example.com")
  }

  def "Scan results are archived" () {
    setup:
      AccessibilityComplianceTest.getBinding().setVariable("config", [ url: "https://www.example.com" ])
    when:
      AccessibilityComplianceTest()
    then:
      1 * getPipelineMock("archive")("accessibility_compliance/**")
  }

  def "env.FRONTEND_URL Takes Priority Over config.url" () {
    setup:
      AccessibilityComplianceTest.getBinding().setVariable("env", [ FRONTEND_URL: "FRONTEND" ])
      AccessibilityComplianceTest.getBinding().setVariable("config", [ url: "config" ])
    when:
      AccessibilityComplianceTest()
    then:
      1 * getPipelineMock("sh")("a11ym -o accessibility_compliance FRONTEND")
      1 * getPipelineMock("archive")("accessibility_compliance/**")
  }

  def "Scan Fails Without URL" () {
    setup:
      AccessibilityComplianceTest.getBinding().setVariable("config", [ url: null ])
    when:
      AccessibilityComplianceTest()
    then:
      1 * getPipelineMock("error")(_)
    }

}
