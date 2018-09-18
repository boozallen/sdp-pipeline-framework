# Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
# This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl

FROM python

RUN pip install sphinx \
                sphinx-autobuild \
                sphinx-rtd-theme \
                recommonmark

WORKDIR /sdp/docs

ENTRYPOINT make livehtml