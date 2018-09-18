.. _conditional inheritance: 

Conditional Inheritance
-----------------------

While SDP derives much of its value from the reusable aspect of its design, another
significant feature is the organizational governance achievable through SDP. 

Different organizations have different requirements around the level of governance
that's required. 

SDP configuration files enable organizations to dial up or down the level of 
governance by allowing portions of the configuration to be appended to, or even
replaced entirely by using the ``merge`` and ``override`` keys. 

Overall Precedence
==================

There are three configuration files in SDP.  

1. SDP default configuration file 
2. The organization configuration file
3. The tenant configuration file 

* Any key not defined by the organization will inherit the SDP value. 
* Any key defined by the organization will **override** what's defined in the SDP default
* Any key defined by the tenant that's not defined by either SDP or the Organization will be used. 
* Any key defined by the tenant that's been defined by the organization or SDP will be used **if and only if** the organization has specified this in their own configuration. 

Let's look at merging and overriding organization configurations. 

Merge
=====

The merge key allows you to append tenant configurations to organization configurations. 

This is best illustrated by an example. 

Here is an example snippet from an **organization pipeline_config.groovy** file: 

.. code:: groovy

    application_environments{
        merge = true 
        dev{
            short_name = "dev"
            long_name = "Development"
        }
    }

and here's an example **tenant pipeline_config.groovy** file: 

.. code:: groovy

    application_environments{
        dev{
            short_name = "tenant custom!"
            long_name = "my own!" 
        }
        prod{
            short_name = "prod"
            long_name = "Production" 
        }
    }   

the **aggregated configuration** would then be 

.. code:: groovy

    application_environments{ 
        dev{ 
            short_name = "dev"
            long_name = "Development" 
        }
        prod{
            short_name = "prod" 
            long_name  = "Production" 
        }
    }

**What Happened?** 

The organization set merge to true for application environments.  This means that existing 
application environments defined by the organization will not be tampered with but new keys
can be added.  

The tenant tried to change the ``dev`` environment and add a ``prod`` environment.  The aggregation
did not fail, but only accepted the addition of a new ``prod`` key. 

Overriding
==========

The override key allows a tenant to replace an existing configuration with their own.

Let's use the same example from before except change the merge to an override. 

Here is an example snippet from an **organization pipeline_config.groovy** file: 

.. code:: groovy

    application_environments{
        override = true 
        dev{
            short_name = "dev"
            long_name = "Development"
        }
    }

The **tenant pipeline_config.groovy** file can remain the same: 

.. code:: groovy

    application_environments{
        dev{
            short_name = "tenant custom!"
            long_name = "my own!" 
        }
        prod{
            short_name = "prod"
            long_name = "Production" 
        }
    }   

The **aggregated pipeline configuration** will now be: 

.. code:: groovy

    application_environments{
        dev{
            short_name = "tenant custom!"
            long_name = "my own!" 
        }
        prod{
            short_name = "prod"
            long_name = "Production" 
        }
    }   

**What Happened?**

In this case, the tenant's application_environments totally replaces
the organization's defined application_environments. 


.. note:: 

    The ability to use the ``merge`` and ``override`` keys is not specific to any 
    field in particular.  You can set these values anywhere in the file, and they will
    be handled appropriately.   