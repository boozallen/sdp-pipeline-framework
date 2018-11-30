# Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
# This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl

FROM python:2.7

# install documentation dependencies
RUN pip install sphinx==1.6.7               \
                sphinx-autobuild==0.7.1     \
                sphinx-rtd-theme==0.4.1     \
                recommonmark==0.4.0      && \
    pip install -U git+https://github.com/sizmailov/sphinxcontrib-versioning@conditionally_run_setup_py

        
# expectation is the container gets run with 
# docker run -v $(path to repo):/app
WORKDIR /app

ENTRYPOINT cd docs && make html