.. _Keywords: 
---------------------
Jenkinsfile Variables
---------------------

Our goal is to make the pipeline templates as readable as possible. 

To help with this, we've added a portion of the configuration file that can be 
used to define variables for use in your Jenkinsfile. 

We currently use this to define the branch name regular expressions to help with 
the ``github-enterprise`` library. 

Use the ``keywords`` block to add variables to your Jenkinsfile. 

.. code:: groovy 

    keywords{
        master  =  /^[Mm]aster$/
        develop =  /^[Dd]evelop(ment|er|)$/ 
        hotfix  =  /^[Hh]ot[Ff]ix-/ 
        release =  /^[Rr]elease-(\d+.)*\d$/
    }

then, from within your Jenkinsfile (assuming you've loaded the github-enterprise library) you could say: 

.. code:: groovy 

    on_pull_request from: develop, to: master, { 

    } 

where the ``develop`` and ``master`` keywords are regular expressions defined in the configuration file and
imported into the Jenkinsfile as variables. 
