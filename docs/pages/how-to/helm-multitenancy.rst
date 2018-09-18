.. _helm multitenancy: 
Helm Multitenancy
-----------------

The default Helm installation allows anyone with access to the tiller server to perform deployments interacting with any release. 
This model works well for individual entitites, but as soon as multiple parties are using a cluster with expectations of separation,
this model fails.  

Helm can be configured by multitenancy by deploying multiple tiller servers with different service accounts and using RBAC policies
to enforce separation of tenants. 

The `Helm docs on using RBAC`_ are a good resource for more information. 

.. _Helm docs on using RBAC: https://docs.helm.sh/using_helm/#role-based-access-control

.. _helm environment provisioning script: 
Environment Provisioning script
===============================

In order to simplify the process for deploying Helm securely, we have written a script_ that automates the deployment of

* An RBAC secured Tiller server
* Application environment projects
* A project to store all image streams

It then configures permissions so that the Tiller server can deploy to the application environments, and each application
environment has permissions to pull images from the image stream project.  

.. _script: https://github.com/boozallen/sdp-helm-chart/blob/master/resources/helm/provision_app_envs.sh
