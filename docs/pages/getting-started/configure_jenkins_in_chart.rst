.. _jenkins chart configuration:
-----------------------
Configure Jenkins Chart
-----------------------

With your pipeline configuration repository created, you can now deploy the Solutions Delivery Platform by configuring the SDP chart's ``values.yaml`` file and running the installer script.

=======================================
Make a Copy of the Values File Template
=======================================

The file "values.yaml" is how you configure the SDP to use your organization's
particular source code repositories, credentials, and various settings. This
makes values.yaml unique to each deployment of the SDP. In order to prevent your
particular values.yaml file from being overwritten or from being inadvertently
uploaded, values.yaml is not only *not* included in the repository, but is
actively ignored by Git.

Instead, a template is provided that you can copy to your own values.yaml file.
One way to create this copy is opening your terminal to the sdp repository and
running the command ``cp values.template.yaml values.yaml``. It is highly
recommended to start from this template and adding your additional settings to
that template copy. Some of the necessary settings you will need to configure
are described below.

.. _sdp repository README: https://github.com/boozallen/sdp-helm-chart/blob/master/README.rst

=============================
Configure GitHub Organization
=============================

The first thing to configure in your "values.yaml" file is the GitHub
organization(s) you would like the SDP to watch. Using your favorite text editor,
open the values.yaml file in the root of your cloned sdp repository and update
the following section:

::

    jenkins:

        githubOrganizations:
        - name:         Required. The GitHub Organization Name (case-sensitive)
          displayName:  Required. The Jenkins Job Display Name
          credentialID: Required. The Name Of The Jenkins Credential Appropriate For This Organization
          apiUrl:       Required. The GitHub API URL
          repoPattern:  Optional. Regex of Repositories to watch. Default is ".*"
        - ... (multiple can be defined)

This will create a `Jenkins GitHub Organization Job`_ for each organization you
configure with our SDP modification. This Jenkins job automatically creates a
single pipeline for every branch and pull request for every repository in the
organization. If it's not clear what to enter, read the explanation on the
example configuration further down on this page.

.. _Jenkins GitHub Organization Job: https://go.cloudbees.com/docs/cloudbees-documentation/cje-user-guide/index.html#github-branch-source

.. note::
   The credentialID fields refer to the ID of a credential object stored in Jenkins.
   Assuming you have a single GitHub account you plan to use, put ``github``
<<<<<<< HEAD
   here. Otherwise provide a unique credentialID for each GitHub account you plan
   to use. We go over storing these credentials in Jenkins further down this page.
=======
   here. Otherwise you will have to `create additional credentials`_ once Jenkins
   is running.

.. _create additional credentials: /pages/how-to/add_jenkins_credentials.html
>>>>>>> new-github-secret-name

===========================================
Configure Pipeline Configuration Repository
===========================================

Next, we need to have Jenkins import the organization config repo we created earlier. To do this, we add it to the list of external pipeline libraries to import on startup.

Add the following to the ``values.yaml`` file, creating an entry for your organization config repo:

::

    jenkins:

        pipelineLibraries:
        - name:               Required. Library ID to reference when loading
          githubApiUrl:       Required. GitHub API URL
          githubCredentialID: Required. The Name Of The Jenkins Credential That Can Access Library Repo
          org:                Required. Name of GitHub Organization Containing Library
          repo:               Required. Name of GitHub Repository
          implicit:           Optional. Whether to Load Library Implicitly. Default false.
          defaultVersion:     Optional. Default Branch of Library to Load. Default master.
        - ... (multiple can be defined)


.. note::

    Here is where you would define additional shared libraries you would like to configure in Jenkins.
    Doing so is outside the scope of this quick start guide.

=====================
Configure Credentials
=====================

Now that we've chosen our GitHub organizations and pipeline libraries, we now need
to provide Jenkins the GitHub credentials to access these resources.

::

    jenkins:

        credentials:
        - id:        Required. Unique name for the credential by which it can be referenced
          username:  Required. The username for the credential
          password:  Required. The password for the credential
        - ... (multiple can be defined)

Don't worry if you leave out any credentials here. You can always
`create additional credentials`_ after installing the SDP.

.. note::
  For Booz Allen employees, your GitHub username is lastname-firstname, all lowercase.
  `You can find instructions for creating a Personal Access Token here`_

.. _You can find instructions for creating a Personal Access Token here: https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/

.. important::

	  Have at least one credential called "github" for the SDP to use when accessing its own resources in GitHub, such as the pipeline-framework repository.

.. _create additional credentials: /pages/how-to/add_jenkins_credentials.html

=====================
Example Configuration
=====================

Putting it all together, an example of the jenkins portion for the *values.yaml* file looks like this:

::

    jenkins:
        master_docker_context_dir: resources/jenkins-master
        agent_docker_context_dir: resources/jenkins-agent
        num_agents: 4

        # GitHub Orgs to watch
        githubOrganizations:
        - name: terrana-steven
          displayName: Steven Terrana
          credentialID: github
          apiUrl: "https://github.com/api/v3"
        - name: Red-Hat-Summit
          displayName: Red Hat Summit
          credentialID: github
          apiUrl: "https://github.com/api/v3"

        # Pipeline Configuration Repository
        pipelineLibraries:
        - name: red-hat-summit
          githubApiUrl: "https://github.com/api/v3"
          githubCredentialID: github
          org: Red-Hat-Summit
          repo: pipeline-configuration

        # Credentials
        - id: github
          username: terrana-steven
          password: <Access Token>

In this example, we are creating pipelines for the GitHub repositories in Steven
Terrana's account, as well as in the "Red-Hat-Summit" organization. Both
repositories have their credentialID set to ``github``. We define that credential
in our values file and store in it the username and password for a Booz Allen
Enterprise GitHub account. Both libraries also have the Booz Allen enterprise
GitHub's API URL, since they're on that particular GitHub server. If we were using
the public GitHub site, we would choose ``https://api.github.com`` and provide
credentials for a public github account.

We also created an entry for our organization config repo in ``pipelineLibraries``.
We gave it the name ``red-hat-summit``, the same as the GitHub organization it's
in, and specified that the *pipeline-configuration* repository is where we put
our configuration files. Later on, when we want our tenant application
repositories to use this organization configuration repo, we would refer to it
with ``organization = "red-hat-summit"``.
