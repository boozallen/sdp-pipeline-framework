FROM python

RUN pip install sphinx \
                sphinx-autobuild \
                sphinx-rtd-theme \
                recommonmark

WORKDIR /sdp/docs

ENTRYPOINT make livehtml