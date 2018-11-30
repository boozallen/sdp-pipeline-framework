=============
Configure SDP
=============

With your GitHub organization created and the SDP deployed, let's configure it for your organization.

This example DevSecOps pipeline will:

* build a container image with :ref:`Docker<Docker Library>`
* perform static code quality analysis with :ref:`SonarQube<SonarQube Library>`
* perform penetration testing with :ref:`Owasp Zap<Owasp Zap Library>`
* perform an accessibility compliance scan with :ref:`The A11y Machine<a11y Library>`
* deploy to application environments with :ref:`OpenShift<OpenShift Library>` using Helm

.. important:: The example pipeline outlined here is **not** all that SDP can do. Rather, it's what we are choosing to showcase as a sample SDP DevSecOps pipeline using a common configuration and only tools deployed via Helm.

-------------------------------
Specify Your Pipeline Libraries
-------------------------------

The SDP pipeline framework works by implementing functionality at runtime. A pipeline template calls generic methods that perform the actions of the :ref:`default step implementation<default step implementation>` unless a library is loaded implementing the method.  For the nerds out there who like academic names for things, this is an implementation of the `Template Method Design Pattern`_.

.. _Template Method Design Pattern: https://dzone.com/articles/design-patterns-template-method

For example, you may have a ``build`` step in your pipeline, in which you want the pipeline to build container images from your source code. You might select the "Docker" library to include in your pipeline, which contains a definition for that pipeline step. That way, whenever your pipeline executes that Build step, it looks to the Docker library for its implementation.

Start by opening your organization's configuration file in the pipeline configuration repository (``/resources/sdp-org/pipeline_config.groovy``) in your favorite text editor. SDP pipeline libraries are configured in a ``libraries`` block. For our example, that looks like:

.. code-block:: groovy

    libraries{
      github_enterprise
      sonarqube
      docker
      openshift
      owasp_zap{
        merge = true
      }
      a11y{
        merge = true
      }
    }


.. note:: The ``merge`` fields below ``owasp_zap`` and ``a11y`` indicate that we want to allow tenants to provide their own settings for these libraries. We want to allow this because these libraries require a URL to run tests against, and each tenants' will be different.

.. note:: For a minimal configuration only the github_enterprise, docker, and openshift libraries are needed.


-------------------------------------
Add Your Image Repository Information
-------------------------------------

To simplify tool management, all of the tools used by SDP are built into container images and leveraged by the pipeline. In this example pipeline, we'll also be building container images for our application.  We'll need to configure where the :ref:`SDP pipeline images<SDP Pipeline Images>` are stored and where the :ref:`application container images<Application Image Repository>` will be archived.  Please refer to the linked documentation pages to read about those configuration options.  If you're using the integrated OpenShift Registry and you deployed SDP to a project called ``sdp``, then it's likely your configuration file should now look like:

.. code-block:: groovy

    sdp_image_repository = "https://docker-registry.default.svc:5000"
    sdp_image_repository_credential = "sdp-jenkins-docker-registry"

    application_image_repository = "docker-registry.default.svc:5000/demo"
    application_image_repository_credential = "sdp-jenkins-docker-registry"

    libraries{
      github_enterprise
      sonarqube
      docker
      openshift
      owasp_zap{
        merge = true
      }
      a11y{
        merge = true
      }
    }


-----------------------------------------------
Add Jenkins Credentials for the Docker Registry
-----------------------------------------------

Assuming you are using OpenShift's Docker Registry (i.e. "docker-registry.default.svc:5000") for your sdp and application image repositories, you will need to add a global Jenkins credential. You can follow the steps described in the :ref:`How to Add Credentials to Jenkins Section<add credentials to jenkins>` to do this.

The credentials will have the following values for their respective fields:

   The **username** will be: ``service``

   To generate the **password**, you will need to run the following command in your terminal and copy/ paste the output into the password field:

   .. code-block:: bash

      oc sa get-token jenkins

   The **ID** will be: ``sdp-jenkins-docker-registry``

   The **description** field is optional and can be left blank if desired.

Click the **OK** button to create the credential.
