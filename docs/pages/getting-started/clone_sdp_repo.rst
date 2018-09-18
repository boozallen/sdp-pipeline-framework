.. _Clone SDP Repo:
-----------------------------------
Clone The SDP Deployment Repository
-----------------------------------

Deploying the Solutions Delivery Platform with the default reference architecture is done using a bash script and a series of Helm charts for the different DevSecOps tools. These are all contained in the `SDP GitHub Repository`_, which you can clone with either of the commands below.

.. code-block:: bash

   ## cloning via ssh
   git clone git@github.boozallencsn.com:solutions-delivery-platform/sdp.git
   ## cloning via https
   git clone https://github.boozallencsn.com/solutions-delivery-platform/sdp.git

**If you haven't already**, use one of the above commands to clone the sdp repository to your computer. We will be editing the *values.yaml* file in the root of this repository to configure the SDP for your project.

.. _SDP GitHub Repository: https://github.com/boozallen/sdp-helm-chart.git