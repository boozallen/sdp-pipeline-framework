.. _Getting Started:
---------------
Getting Started
---------------

Welcome to the Getting Started page for the Solutions Delivery Platform!

========
Overview
========

The Solutions Delivery Platform (SDP) is, first and foremost, a pipeline *framework*.  There are **no** requirements around what type of artifact, if any,
is built and there are **no** requirements around where those artifacts are deployed.

The SDP can be used for any workflow that requires organizational governance,
the ability to leverage plug and play functionality, and maximum reusability.

Examples of workflows that SDP could support

* Data Ingest Pipelines
* Infrastructure as Code Pipelines
* DevSecOps Pipelines

=============
Prerequisites
=============

*That being said*, we have focused primarily on creating libraries for building an end-to-end DevSecOps pipeline to deploy containerized applications to kubernetes, specifically OpenShift.

Assuming this is your use case, there are a few prerequisites:

* An OpenShift cluster has been deployed. If you would like some tips for deploying OpenShift in Booz Allen's AWS CSN environment, refer to our :ref:`walkthrough<deploy openshift on aws csn>`.
* You're using GitHub (public or enterprise) to host source code

  * If you do not already have source code feel free to fork the `sdp-website repo`_ from Red Hat Summit

* Your workstation is running OSX or Linux, either natively, in a VM, or in a container
* You (and your cluster) can access the `sdp`_ and `pipeline-framework`_ GitHub repositories

.. _sdp: https://github.boozallencsn.com/solutions-delivery-platform/sdp
.. _pipeline-framework: https://github.boozallencsn.com/solutions-delivery-platform/pipeline-framework
.. _sdp-website repo: https://github.boozallencsn.com/Red-Hat-Summit/sdp-website


.. note:: Support of other git based scm's and non-git scm's are on the roadmap, but have not yet been implemented.

=================
Let's Get Started
=================

Use the ``previous`` and ``next`` buttons to navigate through this guide.


.. toctree::
   :hidden:
   :titlesonly:

   create_github_organization
   create_pipeline_config_repo
   clone_sdp_repo
   configure_jenkins_in_chart
   deploy_sdp
   configure_sdp
   configure_openshift_library
   create_org_pipeline
   configure_tenant_repo
   verify
