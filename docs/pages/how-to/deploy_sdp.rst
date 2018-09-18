.. _Deploy SDP:
----------
Deploy SDP
----------

.. include:: ../getting-started/clone_sdp_repo.rst
   :start-line: 3

==================================
Customize the ``values.yaml`` File
==================================

If necessary, customize the *values.yaml* file further.  Please refer to the README `here`_ information on what each line of the *values.yaml* file does. If following the :ref:`Getting Started<Getting Started>`
guide, you should have already customized the :ref:`jenkins chart configuration<jenkins chart configuration>` with
a reference to the GitHub organizations you intend for SDP to watch and added your pipeline configuration repository as a library.

The only other field you should update is ``domain``, under ``global``. Set this to whatever domain is being used for the routes on your Openshift cluster.

================
Additional Tools
================

We have created several helm charts to deploy additional DevSecOps tooling.  The full list of tools and customizations
for the SDP helm chart can be seen in the ``README.rst`` of the deployment repository.  It renders best in the browser
and can be seen `here`_.

.. _here: https://github.com/boozallen/sdp-helm-chart/blob/master/README.rst

========================
Run the Installer Script
========================

The installer script to deploy SDP has a few prerequisites:

* You have installed the `OpenShift CLI`_
* You are logged into the target deployment cluster and have appropriate admin permissions.
* You have installed the `Helm CLI`_

.. _OpenShift CLI: https://docs.openshift.com/container-platform/3.9/cli_reference/get_started_cli.html
.. _Helm CLI: https://docs.helm.sh/using_helm/#installing-the-helm-client

With the helm chart appropriately configured, you can run the installer script via

.. code:: shell

    bash installer.sh
