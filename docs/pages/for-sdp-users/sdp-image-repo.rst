.. _SDP Pipeline Images: 
-----------------------------
SDP Pipeline Image Repository
-----------------------------

You must specify the image repository where SDP will pull the pipeline container 
images from.  If you deployed SDP onto OpenShift, this is most likely the integrated
container registry. 


.. csv-table:: SDP Pipeline Image Repository Settings
   :header: "Field", "Description"

   "sdp_image_repository", "The container image repository where SDP pipeline images will be pulled from" 
   "sdp_image_repository_credential", "The Jenkins credential ID to log into the sdp image repository"  


**Example Configuration**

.. code:: groovy

    sdp_image_repository = "https://docker-registry.default.svc:5000"
    sdp_image_repository_credential = "sdp-jenkins-docker-registry"
