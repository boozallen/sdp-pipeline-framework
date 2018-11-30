.. _Docker Library:
------
Docker
------

The `Docker`_ library **builds** docker images and **push** them to a docker repository.

.. _Docker: https://docs.docker.com/get-started/

Steps Provided
==============

- build()
- get_images_to_build()
- login_to_registry()
- retag()

Library Configuration Options
=============================

.. csv-table::  Docker Library Configuration Options
   :header: "Field", "Description", "Default Value", "Options"

   "build_strategy", "This defines the type of docker build to be used", "dockerfile", "docker-compose \| modules \| dockerfile"

Example Configuration Snippet
=============================

.. code:: groovy

   libraries{
     docker {
        build_strategy = docker-compose | modules | dockerfile
     }
   }

Extra Configurations
====================

If you are using a separate set of credentials change the variables listed below inside the configuration file.

.. csv-table::  Docker Extra Configuration Options
   :header: "Field", "Description", "Default Value"

   "sdp_image_repository", "The location where the container images required for the different pipeline tools are stored.", "none"
   "sdp_image_repository_credential", "Credentials used for the repository where pipeline builds are going to be pushed to", "none"
   "application_image_repository", "Where the container images produced during the pipeline builds are going to be pushed to", "none"
   "application_image_repository_credential", "Credentials used for the repository where different docker pipeline tools are stored.", "none"

External Dependencies
=====================

- A Docker repository must be setup and configured. Credentials to the repository are also needed.
- Github Enterprise library needs to loaded.

Troubleshooting
===============

FAQ
===
