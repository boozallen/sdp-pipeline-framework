-----------------
GitHub Enterprise
-----------------

====================
Collect SCM Metadata
====================

The library utilizes a constructor to collect some information during intialization.

.. important:: 

    Reminder: Code in a library step not encapsulated in a method will be executed when
    the library is loaded

The follow environment variables are collected at the start of the build:

.. csv-table:: GitHub Enterprise Environment Variables
   :header: "Environment Variable", "Meaning" 
    
    "GIT_URL", "The remote github url for the repository"
    "ORG_NAME", "The GitHub Organization or User owning the repository"
    "REPO_NAME", "The name of the repository"
    "GIT_SHA", "The commit id of the current pipeline run" 
    "GIT_BUILD_CAUSE", "One of ``commit``, ``merge``, or ``pr``"

======================================
Interact with Remote GitHub Repository
======================================

This functionality is typically going to be used by other libraries rather than
directly by a user or Jenkinsfile.


.. code:: groovy  

    /*
        @Params: 
            url:  remote github url (https)
            cred: jenkins credential ID to access repository 
    */
    withGit(String url, String cred){
        /*
            Working directory is now the remote GitHub 
            repository ${url} and can be interacted with 
            via a git object. 
        */

        // stage a file
        git add: "some_file.txt" 

        // stage a set of files
        git add: [ "some_file.txt", "another_file.groovy" ]

        // commit changes with message
        git commit: "my commit message" 

        // push changes
        git push 
    }

**Example**

.. code:: groovy 

    withGit url: "https://github.company.com/my-org/my-cool-repo.git", cred: "cool-repo", {
        // now inside a node block with the repository as the working directory
        sh "echo hey >> some_file.text"
        git add: "some_file.text"
        git commit: "I said hey in the file"
        git push
    }