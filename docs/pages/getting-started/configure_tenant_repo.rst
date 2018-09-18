-----------------------------
Configure a Tenant Repository
-----------------------------

Congratulations! You've successfully configured SDP for your organization.  All that's left to do is create a ``pipeline_config.groovy`` file for the individual application/tenant repositories.

For this example pipeline, tenants would simply need to configure:

* the organization they belong to (soon to be deprecated)
* the url to test against for the testing libraries

.. code:: groovy

    organization = "demo"
    libraries{
        owasp_zap{
          target = <url to test>
        }
        a11y{
          url = <url to test>
        }
    }
