.. _configure_openshift_library:
---------------------------------
Configuring the OpenShift Library
---------------------------------

Your cluster permissions are how you achieve multitenancy with SDP.

To prepare a multitenant configuration of OpenShift for SDP we need to set up the following:

* A project per application environment per isolated tenant
* An installation of Tiller per tenant
* An OpenShift project to store the image streams for archived container images
* RBAC configuration for these projects.
* A Helm configuration repository to store the chart(s) for a tenant (or group of tenants)

===================================
Preparing The OpenShift Environment
===================================

We have automated the provisioning of application environments, helm, and a project to archive container images with the appropriate multitenant RBAC configuration via a script in the SDP deployment repository - ``/resources/helm/provision_app_envs.sh``.

To see the available options run:

.. code::

    bash provision_app_envs.sh -h

For our example, we will create a set of demo application environments (``dev`` and ``prod``), and store the images in a project called ``demo``

.. code::

    bash provision_app_envs.sh -p demo -e dev -e prod -i demo

This will create the following projects:

.. csv-table:: Provisioned OpenShift Infrastructure
   :header: "Project", "Description"

   "demo-dev", "The Development application environment"
   "demo-prod", "The Production application environment"
   "demo-tiller", "The tiller namespace"
   "demo", "The project where we will configure SDP to push container images"

.. note:: If you modify the command above to create projects with different names, be sure to continue using those names as you follow this guide. For example if you enter ``bash provision_app_envs.sh -p foo -e dev -e prod -i bar``, use "foo-tiller" instead of "demo-tiller" and use "bar" for your application_image_repository.

Now, we can begin to configure this in the organizational *pipeline_config.groovy* file. We will need to add the application environments and configure the OpenShift library.

.. code::

    sdp_image_repository = "https://docker-registry.default.svc:5000"
    sdp_image_repository_credential = "sdp-jenkins-docker-registry"

    application_image_repository = "docker-registry.default.svc:5000/demo"
    application_image_repository_credential = "sdp-jenkins-docker-registry"

    application_environments{
        dev{
            short_name = "dev"
            long_name = "Development"
        }
        prod{
            short_name = "prod"
            long_name = "Production"
        }
    }

    libraries{
      github_enterprise
      sonarqube
      docker
      openshift{
        url = <your openshift url> // for example: https://master.oscp.microcaas.net:8443
        tiller_namespace = "demo-tiller"
        tiller_credential = "demo-tiller"
      }
      owasp_zap{
        merge = true
      }
      a11y{
        merge = true
      }
    }

**Create a Jenkins Credential with ID ``demo-tiller``**

For the OpenShift library to authenticate and perform a deployment via Helm, it uses a Jenkins credential.

Follow :ref:`this guide<add credentials to jenkins>` to create a username/password credential in Jenkins with the username: ``system:serviceaccount:demo-tiller:tiller``

To get the password, you'll have to get a service account token from the tiller service account:

.. code::

    oc sa get-token tiller -n demo-tiller

copy the token and paste it into Jenkins.

.. important:: make sure you set the credential ID to demo-tiller, as referenced in your configuration file.

==========================================
Creating the Helm Configuration Repository
==========================================

To achieve disaster recovery, auditability, rollbacks, and infrastructure as code, the OpenShift library deploys
applications using Helm from a chart stored in a GitHub repository.

Create a GitHub repository. This repository can be under any GitHub account or organization, but it makes sense to create the repository inside the GitHub organization created in the first step, or in the same organization as your applications if you opted to use a pre-existing one. Name it whatever you like, but ``helm-configuration-repository`` makes sense. It can be public or private, so long as the GitHub account Jenkins is using can read *and* write.

Add this repository to your configuration file, which should now look like:

.. code::

    sdp_image_repository = "https://docker-registry.default.svc:5000"
    sdp_image_repository_credential = "sdp-jenkins-docker-registry"

    application_image_repository = "docker-registry.default.svc:5000/demo"
    application_image_repository_credential = "sdp-jenkins-docker-registry"

    application_environments{
        dev{
            short_name = "dev"
            long_name = "Development"
        }
        prod{
            short_name = "prod"
            long_name = "Production"
        }
    }

    libraries{
      github_enterprise
      sonarqube
      docker
      openshift{
        url = <your openshift url> // for example: https://master.oscp.microcaas.net:8443
        tiller_namespace = "demo-tiller"
        tiller_credential = "demo-tiller"
        helm_configuration_repository = <url to your helm repo> // for example: "https://github.com/Example-Org/helm-configuration.git"
        helm_configuration_repository_credential = <Jenkins credential ID to access repo> // probably "github"
      }
      owasp_zap{
        merge = true
      }
      a11y{
        merge = true
      }
    }

==========================
Initialize Helm Repository
==========================

Jenkins will clone this repository and push updates to it to perform deployments and record which versions of container images are deployed to each application environment. You can initialize a helm chart with the ``helm create`` command.

Assuming you've created an empty GitHub repository for your helm chart, you can run:

.. code:: shell

    helm create <repo_name>
    cd <repo_name>
    git remote add origin <helm repo url>
    git add --all
    git commit -m "initializing chart repo"
    git push -u origin master


Once that's done you should

1. Delete the yaml files that were automatically created when ``helm create`` was called. These are example helm templates, and we don't need them.
2. Delete the contents of templates/_helpers.tpl and templates/NOTES.txt. We want to keep those files, but provide our own content.
3. Update *Chart.yaml* to properly describe your new chart.

 For more information on Helm charts, check out the |Helm_documentation|.

 .. |Helm_documentation| raw:: html

     <a href="https://docs.helm.sh/developing_charts/" target="_blank">Helm documentation</a>

====================
SDP Helm Conventions
====================

SDP pushes and pulls to this chart repository to keep it up to date with the image tags of deployed containers.

For each application repository that SDP will be building, add this to the values.yaml file under an ``image_shas`` key.

For example, if there was a repository called ``sample-app``, your ``values.yaml`` would include:

::

    image_shas:
        sample_app:

.. warning:: Because YAML key's cannot contain hyphens, any hyphens in repository names should be converted to underscores.

Your template would then be able to specify the image for a deployment via:

::

    image: docker-registry.default.svc:5000/demo/sample-app:{{ .Values.image_shas.sample_app }}

At this point, you should take some time to finish fleshing out your Helm chart to reflect how you wish to deploy your app.

====================================
Create a Values File Per Environment
====================================

In addition to the *values.yaml* file created when ``helm create`` was run, you should make a *values.<APP_ENV>.yaml* file for each application environment you created at the top of this page. Be sure to substitute *<APP_ENV>* with the ``short_name`` of the application environment. For example, if you created a *dev* and *prod* environment, you might create those files with the command:

.. code::
  cp values.yaml values.dev.yaml
  cp values.yaml values.prod.yaml

The purpose of these separate files is so that you can provide your separate configurations (database URLs, names, etc.) for different environments. Now, whenever you use the ``deploy_to dev`` step in your pipeline, it will deploy a helm chart using *values.dev.yaml*.

The SDP will automatically update the image sha value discussed earlier, but you should now modify the different values.yaml files with environment-specifc variables.

==================================================
Example Helm Configuration With Forked SDP-Website
==================================================

If you forked the SDP-Website repo earlier to follow along with this guide these are the changes that you would need to make after running the ``helm create`` command.

::

    helm_configuration_repository
    ├── templates/
    │   ├── _helpers.tpl
    │   ├── frontend.yaml
    │   └── NOTES.txt
    ├── .helmignore
    ├── Chart.yaml
    ├── README.md (optional)
    ├── values.dev.yaml
    ├── values.prod.yaml
    └── values.yaml

Where the following adjustments have been made to files created with the ``helm create`` command:

~~~~~~~~~~~~
_helpers.tpl
~~~~~~~~~~~~

::

    {{/* Determines Namespace Based on Ephemerality */}}
    {{- define "determine_namespace" }}
    {{- if .Values.is_ephemeral }}
    namespace: {{ .Release.Name }}
    {{- else }}
    namespace: {{ .Values.namespace }}
    {{- end }}
    {{- end }}

~~~~~~~~~~~~
NOTES.txt
~~~~~~~~~~~~

::

    Finished installing SDP-Website

This file contains text that is displayed after the chart is successfully installed.

~~~~~~~~~~~~
Chart.yaml
~~~~~~~~~~~~

``name: .`` changed to ``name: helm-configuration-repository``

The following files were created manually:

~~~~~~~~~~~~
frontend.yaml
~~~~~~~~~~~~
::

    kind: List
    apiVersion: v1
    metadata:
      name: frontend
    items:

    - kind: DeploymentConfig
      apiVersion:  apps.openshift.io/v1
      metadata:
        name: frontend
        namespace: {{ .Values.namespace }}
        labels:
          app: frontend
          name: frontend
      spec:
        replicas: 1
        selector:
          app: frontend
          deploymentconfig: frontend
        triggers:
          - type: ConfigChange
        template:
          metadata:
            labels:
              app: frontend
              deploymentconfig: frontend
          spec:
            containers:
            - image: docker-registry.default.svc:5000/demo/sdp-website:{{ .Values.image_shas.sdp_website }}
              name: frontend
              volumeMounts:
                  - mountPath: /var/cache/nginx
                    name: nginx-cache
            ports:
            - name: web
              protocol: TCP
              port: 8080
              targetPort: 8080
              nodePort: 0
            volumes:
            - name: nginx-cache
              emptyDir: {}

    - kind: Service
      apiVersion: v1
      metadata:
        name: frontend
        namespace: {{ .Values.namespace }}
      spec:
        ports:
        - name: web
          protocol: TCP
          port: 8080
          targetPort: 8080
          nodePort: 0
        selector:
          app: frontend
        type: ClusterIP
        sessionAffinity: None

    - kind: Route
      apiVersion: "route.openshift.io/v1"
      metadata:
        name: frontend
        namespace: {{ .Values.namespace }}
      spec:
        host: "frontend-{{ .Release.Name }}.apps.oscp.example.net"
        to:
          kind: Service
          name: frontend

.. note::
    ``- image: docker-registry.default.svc:5000/demo/sdp-website:{{ .Values.image_shas.sdp_website }}`` should reflect the repository site in the organizational *pipeline_config.groovy* file for your ``application_image_repository`` variable.
    E.g. if you had created your application environments using ``bash provision_app_envs.sh -p foo -e dev -e prod -i bar`` then your ``application_image_repository`` in the organizational *pipeline_config.groovy* file would be look like
    ``application_image_repository = "docker-registry.default.svc:5000/bar"`` so your image value in the above yaml file would look like ``- image: docker-registry.default.svc:5000/bar/sdp-website:{{ .Values.image_shas.sdp_website }}``. Note the switch from
    */demo/sdp-website* to */bar/sdp-website*.

~~~~~~~~~~~~
values.dev.yaml
~~~~~~~~~~~~
::

    namespace: demo-dev # if your tiller server is foo-tiller this is probably foo-dev
    image_shas:
        sdp_website:

~~~~~~~~~~~~
values.prod.yaml
~~~~~~~~~~~~
::

    namespace: demo-prod # if your tiller server is foo-tiller this is probably foo-dev
    image_shas:
        sdp_website:
