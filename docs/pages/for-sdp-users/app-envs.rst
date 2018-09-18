Application Environments
------------------------

Application environment objects can be dynamically defined in the pipeline configuration 
under an ``application_environments`` key.  Every key will be available as a variable in
the pipeline templates. 

For example:

.. code:: groovy
   
   application_environments{
        dev{
            short_name = "dev"
            long_name = "Development"
        }
        test{
            short_name = "test" 
            long_name = "Test"
        }
        staging{
            short_name = "stage"
            long_name = "Staging"
        }
        prod{
            short_name = "prod"
            long_name = "Production"
        }
   }

would result in the variables ``dev``, ``test``, ``staging``, and ``prod`` being available your Jenkinsfile.

.. warning:: 

    The variables representing application environments are immutable hash maps.  
    Do not try to modify fields on these objects. 

Your Jenkinsfile could then say something like:

.. code:: groovy

    on_commit{
        unit_test()
        static_code_analysis()
        build()
        /*
            use dev object to specify deployment location
        */
        deploy_to dev
    }

and the corresponding library implementing the ``deploy_to`` method would be able to access
the fields ``dev.short_name`` && ``dev.long_name``. 

.. note:: 

    Having ``short_name`` and ``long_name`` are always required. The different pipeline libraries
    supplying a ``deploy_to`` method will inform you if there are configurations you must put on
    your application environment definitions.
    
