##Welcome to Monster Finder Application


Monster Finder is an application that can use camera to capture user's face to recognize user's identification for security authentication in mobile device.

###Modules:

Our application have 2 modules

	* [finder-mobile]: it's an Apache Cordova application, for now it already provides Android support. But with Cordorva's implementation, it's very easy to add other platform (list IOS, BlackBarry) support.
	
	* [finderRI]: it's the service layer of our application. We provide such features.
	
		* REST service for the mobile client
		
		* In-Memory database to store client's information
		
		* OpenCV implementation to detect user's face and recognize user's identification 
		
###Technologies and architecture

We used the following technologies and architecture:

	* HTML5	
	* REST Service	
	* Memory Database
	* Machine Learning for Face detection and recognition

Our code is designed to
---------------------------
	
	introduce a new architecture and development process for Enterprise Mobile Application, 
keeping all of resource in remote server so that the application upgrade automatically.

It was written by
----------------------
	 Java and HTML5
 
It uses following open source or proprietary software
----------------------------------------------------------
	Apache Cordova
	JQuery Mobile
	HSQL	
	OpenCV

It's cool because
---------------------
	It is a machine learning application
	It can support multiple mobile platform	
	It can support different screen-resolution automatically
	It can demonstrate how to upgrade the system without IT help



		 
	  
