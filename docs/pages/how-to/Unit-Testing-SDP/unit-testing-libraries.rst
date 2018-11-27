======================
Unit Testing a Library
======================

-------------------------
The Purpose of Unit Tests
-------------------------

The purpose of writing unit tests for pipeline libraries is to confirm that
they function the way we expect. It also gives us a way to confirm that any
features that were added to a library didn't inadvertently break other features.

--------------------
Where Are The Tests?
--------------------

Unit tests are in the ``/unit-test`` directory of each pipeline library repo. Each
pipeline step (e.g. "build", "penetration_test") has a groovy file containing
its test specification. Each Spec file contains one or more unit tests designed
to verify the step functions as intended.

--------------------------
How Are the Tests Written?
--------------------------

The Testing Framework
=====================

We test pipeline libraries using Jenkins-Spock, a variation of the Spock testing
framework that has been designed around testing Jenkins pipelines. You can view
the documentation for Jenkins-Spock on its |GitHub Repository|, while more
general Spock documentation is available at |this link|.

.. |GitHub Repository| raw:: html

   <a href="https://github.com/homeaway/jenkins-spock" target="_blank">GitHub Repository</a>

.. |this link| raw:: html

  <a href="http://spockframework.org/spock/docs" target="_blank">this link</a>

Writing a Specification File
===============================

A "specification" is a list of features derived from business requirements. A
specification file contains that list of features as unit tests, and those
tests validate that the features work as expected. There should be a separate
file for each pipeline step in your library.

Below is an outline of a specification file. It shows what you need to include
in order to run tests, as well as some conventions for what to name methods and
variables. Create a groovy file with the same name as the class (such as
MyPipelineStepSpec.groovy) and use this outline to get you started, making sure
to swap names with ones for your library.

.. code-block:: groovy

  // Start by importing the Jenkins-Spock framework Code
  import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification

  // Create a new class for the Spec
  // The naming convention is the pipeline step's name, followed by Spec,
  // all camel-cased starting w/ a capital.
  public class MyPipelineStepSpec extends JenkinsPipelineSpecification {

    // Define the variable that will store the step's groovy code. This variable
    // follows the same naming variable as the class name, with Spec omitted.
    def MyPipelineStep = null

    // setup() is a fixture method that gets run before every test.
    // http://spockframework.org/spock/docs/1.2/spock_primer.html#_fixture_methods
    def setup() {
      // It's required to load the pipeline script as part of setup()
      // With the library monorepo, pipeline step groovy files can be found in "sdp/libraries"
      MyPipelineStep = loadPipelineScriptForTest("sdp/libraries/my_library/my_pipeline_step.groovy")
    }

    // Write a test (i.e. Feature Method) for each feature you wish to validate
    // http://spockframework.org/spock/docs/1.2/spock_primer.html#_feature_methods
    def "Successful Build Sends Success Result" () {
      setup:
        // unlike in the pipeline, the config object is not loaded during
        // unit tests. Use this to set it manually
        MyPipelineStep.getBinding().setVariable("config", [ field: "String" ])
      when:
        // This is the "stimulus". It does things so we can test what happens
        // Typically, you execute the step's groovy code like this
        MyPipelineStep()
      then:
        // Here's where you describe the expected response
        // If everything in here is valid, the test passes
        1 * getPipelineMock("sh")("echo \"field = String\"")
    }

  }

Running Tests
=============

It's assumed these spec files are in the "test" directory of the pipeline-framework
repository. This repo has been set up to run Jenkins-Spock tests using `Maven`_.
from the root of the pipeline-framework repo, run ``mvn clean verify``

.. _Maven: http://maven.apache.org/

Writing Tests
=============

Now that you've laid the groundwork for your tests, it's time to write them. These
are the "Feature Methods" because there should be one for each feature. Some of
the things to write tests for are:

  1. Things are built correctly (objects, string variables, maps, etc.)
  2. Conditional Hierarchies function as expected
  3. Variables get passed correctly
  4. Things fail when they're supposed to

Once you know the feature you're testing, like "Pipeline Fails When Config Is
Undefined", write a feature method for it

.. code-block:: groovy


   def "Pipeline Fails When Config Is Undefined" () {

   }

Now create a setup "block" to define some do some pre-test preparation not
covered by the ``setup()`` fixture method. In this example, the binding
variable "config" is set to null, and a mock for the ``error`` pipeline step
is created.

.. code-block:: groovy

   def "Pipeline Fails When Config Is Undefined" () {
     setup:
       explicitlyMockPipelineStep("error")
       MyPipelineStep.getBinding.setVariable("config", null)
   }

Now to execute the pipeline step and test the response. This happens in
the "when" and "then" blocks, respectively. In this example, the pipeline step
is called (with no parameters), and I state that I expect the ``error`` step to
be called exactly once with the message ``"ERROR: config is not defined"``

.. code-block:: groovy

    def "Pipeline Fails When Config Is Undefined" () {
      setup:
        explicitlyMockPipelineStep("error")
        MyPipelineStep.getBinding.setVariable("config", null)
      when:
        MyPipelineStep() // Run the pipeline step we loaded, with no parameters
      then:
        1 * getPipelineMock("error")("ERROR: config is not defined")
    }

And that's the jist of it. You can add as many feature methods as necessary
in the spec file, testing a variety of things. Be sure to check out the Spock
Documentation, Jenkins-Spock Documentation, and already-created spec files in
this repository for examples.

---
FAQ
---

This section covers some of the questions not easily answered in the Spock or
Jenkins-Spock documentation.

**Q: What's the difference between explicitlyMockPipelineStep and explicitlyMockPipelineVariable?**

**A: Practically speaking, the difference is you can omit ".call" for explicitlyMockPipelineStep() when you use getPipelineMock()**

In the example above, I used ``explicitlyMockPipelineStep()`` to mock ``error``.
Because of that, if I want to see if the ``error`` pipeline step is run, I use
``1 * getPipelineMock("error")``. If I were to create the mock using
``explicitlyMockPipelineVariable()`` I would instead use ``1 * getPipelineMock("error.call")``

There may be some additional differences as well, so try to use what makes the most sense

---

**Q: What if I don't know exactly what the parameters are going to be?**

**A: There are ways to match parameters to regex expressions, as well as test
parameters individually**

The standard format for interaction-based tests are

.. code-block:: groovy

  <count> * getPipelineMock(<method>)(<parameter(s)>)

While you can put the exact parameter value in the second parentheses, you can
also run arbitrary groovy code inside curly brackets. Whether or not it's a "match"
depends on if that code returns ``true`` or ``false``. A good example is in
PenetrationTestSpec.groovy. Use ``it`` to get the value of the parameter.
```1 * getPipelineMock("sh")({it =~ / (zap-cli open-url) Kirk (.+)/})```

---

**Q: Do I have to do interaction-based testing?**

**A: No, but you can't get variables the same way as traditional Spock tests**

This is because the script gets run in that ``loadPipelineScriptForTest`` object.
You can only access variables stored in the binding, which are few. It makes more
sense to see how variables are being used in pipeline steps, and make sure those
pipeline steps use the correct value for those variables.

Similarly, if you need to control how a variable is set, you need to stub whatever
method or pipeline step that sets the initial value for that variable

As an example, in PenetrationTestSpec.groovy, the ``target`` variable in
penetration_test.groovy is tested by checking the parameters to an ``sh`` step.

---

**Q: I keep getting "can't run method foo() on null; what do I do?"**
**A: You need to find a way to stub the method that sets the value for the object that calls foo()**

There should be an example in GetImagesToBuildSpec.groovy
