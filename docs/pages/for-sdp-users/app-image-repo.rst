.. _Application Image Repository: 
----------------------------
Application Image Repository
----------------------------

Images that get built via SDP will be pushed to a container image repository. 

.. csv-table:: Application Image Repository Settings
   :header: "Field", "Description"

   "application_image_repository", "The container image repository where images will be pushed to." 
   "application_image_repository_credential", "The Jenkins credential ID to log into the application image repository"  


**Example Configuration**

.. code:: groovy

    application_image_repository = "docker-registry.default.svc:5000/red-hat-summit"
    application_image_repository_credential = "sdp-jenkins-docker-registry"

