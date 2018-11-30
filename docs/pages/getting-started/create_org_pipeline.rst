------------------------------
Create Organizational Pipeline
------------------------------

We will define our branching strategy via the organizational pipeline located in the pipeline configuration repository at ``/resources/sdp-org/Jenkinsfile``.

The branching strategy we'll define will:

* For every commit to a feature branch:

  * build a container image
  * do static code quality analysis

* For each pull requests from a feature branch to master:

  * build the container image
  * do static code quality analysis
  * build a container image
  * deploy to development
  * perform penetration testing
  * perform accessibility compliance scanning

* For each merge to master:

  * deploy to production

To enforce this branching strategy, our Jenkinsfile will look like this:

.. code::

    on_commit{
        build()
        static_code_analysis()
    }

    on_pull_request to: master, {
        build()
        static_code_analysis()
        deploy_to dev
        parallel "Accessibility Scanning": { accessibility_compliance_test() },
                 "Penetration Testing": { penetration_test() }
    }

    on_merge to: master, {
        deploy_to prod
    }

.. important:: Remember to ``git push`` your pipeline-configuration repository after making the final changes.

.. note:: The options available for defining a branching strategy this way can be seen in the :ref:`GitHub Enterpise<GitHub Enterprise Library>` library documentation.
