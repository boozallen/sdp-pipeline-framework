Notifiers
---------

.. note:: 

    The use of notifiers is an experimental feature.

Notifiers are libraries that provide feedback after the pipeline is done executing. 

Currently, the only notifier is ``slack`` and it's configured as follows: 

.. code:: groovy

    libraries{
        slack
    }
    notifiers{
        slack
    }