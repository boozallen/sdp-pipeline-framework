------------------------------------------
Create a Pipeline Configuration Repository
------------------------------------------

When using the SDP, the **Pipeline Configuration Repository** is where organization-wide settings are configured for your DevSecOps pipelines.

This repository will contain your business organization's configuration file, which specifies the organization-wide libraries to load, the application environments, your pipeline templates, and more.  Right now, let's focus on setting up the repository's structure. We'll dive more into the configuration files later.

==============================
Create a New Github Repository
==============================

Start by creating a new GitHub repository. It can be either public or private. While you can name it anything, *pipeline-configuration* would be sensible. This repository can be under any GitHub account or organization, but it makes sense to create the repository inside the GitHub organization created in the last step, or in the same organization as your applications if you opted to use a pre-existing one. After you've created the new repository, clone it to your computer so that we can begin adding to it.

.. note::

    To achieve the separation of duties promised by SDP, developers should have read-only access to this repository while platform administrators should have read/write.

    It would be best to :ref:`configure the repository <github repository configuration best practices>` with a protected master branch, and require a code review before modifications can be made.

    Changes to this repository will immediately impact every pipeline in your organization.


We need to populate this repository with the following file structure:

::

    ├── resources/
    │   └── sdp-org/
    │       ├── Jenkinsfile
    │       ├── pipeline_config.groovy
    │       └── pipeline_templates/
    └── vars/
        └── do_nothing.groovy


You can copy and paste this to setup your repo's file structure:

.. code:: shell

    # from the root of your pipeline config repo
    mkdir -p resources/sdp-org/pipeline_templates vars
    touch resources/sdp-org/{pipeline_config.groovy,Jenkinsfile} vars/do_nothing.groovy
    git add --all
    git commit -m "initializing repository"
    git push -u origin master

.. csv-table:: Pipeline Configuration Repository Files
   :header: "File", "Purpose", "Contents"

   "resources/sdp-org/pipeline_config.groovy", "Your organizational configuration file.", "To be discussed later in this guide. If curious, documentation for the config file syntax can be found under :ref:`For SDP Users<For SDP Users>` "
   "resources/sdp-org/Jenkinsfile", "Your org-wide Jenkinsfile", "Business logic of your pipeline. Specifies what happens when."
   "resources/sdp-org/pipeline_templates/", "A directory for additional pipeline templates (Jenkinsfiles)", "Named pipeline templates referencable by tenants"
   "vars/do_nothing.groovy", "This file does nothing, but is required because of how SDP loads the configuration at runtime", "This file should be empty."

.. note::

    This file structure follows this convention because the repository gets loaded
    as a `Jenkins shared library`_ into the pipeline. Without a *vars* directory containing
    at least one groovy file, the library will fail to load.

.. _Jenkins shared library: https://jenkins.io/doc/book/pipeline/shared-libraries/
