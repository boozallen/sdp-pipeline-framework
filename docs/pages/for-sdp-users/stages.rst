Stages
------

In order to support the goal of keeping pipeline templates as streamlined
and easy to read as possible, we've incorporated the concept of stages into 
SDP. 

A stage is just a grouping of pipeline methods callable by another name. 

Rather than having a pipeline template such as: 

.. code:: groovy

    on_commit{
        unit_test()
        static_code_analysis()
        build()
        scan_container_image()
    }

    on_pull_request{
        unit_test()
        static_code_analysis()
        build()
        scan_container_image()
        deploy_to dev
        functional_test()
    }

you could instead define a ``stage`` in your organization's ``pipeline_config.groovy`` file:

.. code:: groovy

    stages{
        continuous_integration{
            unit_test
            static_code_analysis
            build
            scan_container_image
        }
    }

This will create a continuous_integration method on the fly, able to be called from your
pipeline template. 

The pipeline template from before would be reduced to this: 

.. code:: groovy

    on_commit{
        continuous_integration()
    }

    on_pull_request{
        continuous_integration()
        deploy_to dev
        functional_test()
    }

