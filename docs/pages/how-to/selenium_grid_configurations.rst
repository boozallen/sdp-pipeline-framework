
------------------------------
How To Configure Selenium Grid
------------------------------

Selenium Grid is a web browser testing tool that allows a series of automated test to run in parallel across multiple browsers and versions.

From any Pod/Container in the OpenShift cluster you are able to access Selenium Grid via the Service (selenium-hub.sdp.svc).

Installing Libraries
####################
Before writing any test the correct libraries must be installed. For more information installing libraries access the official Selenium Grid documentation https://www.seleniumhq.org/docs/03_webdriver.jsp#introducing-webdriver


The Webdriver API must be setup to access Selenium Grid Remotely.
Below are two example stings for configuring the driver.  When using the SDP Cluster the Openshift Service address for accessing Selenium Hub is:
**selenium-hub.sdp.svc:4444**

Remote WebDriver
################
In both examples replace <BROWSER> with the type of browser being used.

**Python**::

	driver = webdriver.Remote(command_executor="http://selenium-hub.sdp.svc:4444/wd/hub",webdriver.DesiredCapabilities.<BROWSER>.copy())

**Java**::

	driver = new RemoteWebDriver(new URL("http://selenium-hub.sdp.svc:4444/wd/hub”),
                 DesiredCapabilities.<browser>());

For More examples for creating a Remote WebDriver in other languages (*c#, ruby, javascript, and php*) visit the official Selenium Grid documentation.  
https://www.seleniumhq.org/docs/04_webdriver_advanced.jsp#remotewebdriver


Python Selenium Grid Minimal Example.  In the example below the script connects into the Selenium Hub. From there the Hub makes a request to the Chrome Node running a series of simple test. If all test succeed the driver prints the title of the website loaded (Google.com) then releases the driver object. 

::


	$ cat > SGTest.py <<EOF 
	#Simple test written in Python for Selenium Grid

	from selenium import webdriver
				 
	driver = webdriver.Remote(
	   command_executor="http://selenium-hub.sdp.svc:4444/wd/hub",
	   desired_capabilities={
	            "browserName": "chrome",  
	      	    "version": "67.0.3396.87",             
	            "video": "True",
            "platform": "LINUX",
            "platformName": "LINUX",
	   })

	print (driver.session_id)

	try:
	    driver.implicitly_wait(30)
	    driver.set_window_position(0,0)
	    driver.set_window_size(1920,1080)
	    driver.get("http://www.google.com")
	    print driver.title
	finally:
	    driver.quit()
	EOF

	$ python SGTest.py 


Adding a new Node
#################
If needed adding a new node requires creating a new OpenShift Pod/Deployment. A good place to start is by using the Selenium Grid NodeBase image. https://github.com/SeleniumHQ/docker-selenium/tree/master/NodeBase.  

When configuring the nodes make sure to set the Pods environment variables are set too
::

 HUB_HOST: selenium-hub.sdp.svc  
 HUB_PORT_PARAM: 4444
 
If the pod is attempting to run as the root user add these two additional environment variables to the pod
::

 USER: seluser
 HOME: /home/seluser

Then add the following code to the top of the entry_point.sh file. 

::

	if ! whoami &> /dev/null; then
	  if [ -w /etc/passwd ]; then
	    echo "${USER_NAME:-default}:x:$(id -u):0:${USER_NAME:-default} user:${HOME}:/sbin/nologin" >> /etc/passwd
	  fi
	fi
