Pipeline Templates
------------------

Pipeline templates (Jenkinsfiles) define the business logic of your pipeline. They're responsible for 
deciding what happens when and invoking the generic pipeline steps that need to be called. 

This is where you'll codify your software delivery process for your organization. 

Defining an Organizational Jenkinsfile
======================================

SDP provides a `default Jenkinsfile`_.  If you wish to override this with your own, you can do so by
creating a ``Jenkinsfile`` in your organizational pipeline configuration repository located at 
``/resources/sdp-org/Jenkinsfile``. 

.. _default Jenkinsfile: https://github.com/boozallen/sdp-pipeline-framework/blob/master/resources/sdp/Jenkinsfile

The presence of this file is all that's required to override the default SDP pipeline. 

Defining Additional Pipeline Templates
======================================

Sometimes a single pipeline template isn't enough to meet your organization's needs. 

It should be possible to support any tech stack with a single pipeline template.  Use cases
for having multiple templates defined include: 
* Pipeline template per branching strategy
* Pipeline template per type of workflow (Data, Infrastructure, Software) 

To create multiple pipeline templates, create a directory called ``pipeline_templates`` in your pipeline
configuration repository under ``/resources/sdp-org``. 

Within this directory, any file can be referenced by tenants in their repository's ``pipeline_config.groovy`` file. 

For example, if my pipeline configuration repository contained: 

.. code:: groovy

    | resources
    |--- sdp-org 
    |------ Jenkinsfile
    |------ pipeline_templates
    |--------- data
    |--------- infrastructure

where 

.. csv-table:: Example Pipeline Templates
   :header: "File", "Purpose" 

   "/resources/sdp-org/Jenkinsfile", "Software Pipeline Template" 
   "/resources/sdp-org/pipeline_templates/data", "Data Ingestion Pipeline Template" 
   "/resources/sdp-org/pipeline_templates/infrastructure", "Infrastructure as Code Pipeline Template"

Then application development teams would not need to say anything in their configuration file, given that
the default organizational pipeline template is for software.  

The data repositories would configure their pipeline templating by setting ``pipeline_template`` equal to path relative
to the ``pipeline_templates`` directory. 

.. code:: groovy

    pipeline_template = "data" 

and the infrastructure teams would do the same: 

.. code:: groovy
    
    pipeline_template = "infrastructure" 


Tenants Bringing Their Own Jenkinsfiles
=======================================

If an organization wants to leverage the reusability of SDP without needing
the governance of a centralized pipeline definition, they can allow tenants
to bring their own ``Jenkinsfile`` by setting: 

.. code:: 

    allow_tenant_jenkinsfile = true 

Tenants would then be able to define a ``Jenkinsfile`` at the root of their 
repository. 
