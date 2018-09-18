Contribute to the Docs
======================

Local Development With Docker 
-----------------------------

A docker image has been developed for working with docs locally. This way, you don't have to 
install the python packages required, and you'll get the benefits of hot reloading of your
document changes. 

While working, if a new library page is created you'll have to restart the container to incorporate
that page. 

**Clone the pipeline-framework repository**

.. code-block:: bash

   ## cloning via ssh
   git clone git@github.com:boozallen/sdp-pipeline-framework.git
   ## cloning via https
   git clone https://github.com/boozallen/sdp-pipeline-framework.git

**Create a branch for your changes**

.. code-block:: bash

   git checkout -B <my-awesome-doc-contribution>

**Run the Sphinx documentation locally**

.. code-block:: bash

   ## from the root of pipeline-framework
   docker build -f docs/Dockerfile -t sdp-docs docs
   docker run --name sdp-docs -d -v $(pwd):/sdp -p 8000:8000 sdp-docs

**Update the documentation. Your changes will automatically appear on http://localhost:8000**

**Once complete, push your branch**

.. code-block:: bash

   git push -u origin <my-awesome-doc-contribution>

**Create a Pull Request**


**Done! Your changes will be reviewed by an SDP team member**

ReStructuredText Syntax
-----------------------

Go read some documentation_ on RST syntax.

.. _documentation: http://www.sphinx-doc.org/en/master/usage/restructuredtext/basics.html

Local Development Without Docker 
--------------------------------

You'll need python installed and some pip dependencies. 

I currently have ``Python 2.7.15`` installed. 

To install the pip dependencies, run 

.. code:: shell

   pip install sphinx sphinx-autobuild sphinx-rtd-theme recommonmark 

   # need a specific version of the versioning tool because of a sphinx breaking change
   pip install -U git+https://github.com/sizmailov/sphinxcontrib-versioning@conditionally_run_setup_py

Deploying the Docs
------------------

From the root of the ``pipeline-framework`` repository, run: 

.. code:: shell

    sphinx-versioning push --show-banner docs gh-pages .