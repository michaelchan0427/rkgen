  
	THIS IS A WIP!



RKGen 
=====

RKGen generates Objective-c objets from JSON, and it also generates mapper
classes  that can be used with [RestKit](https://github.com/RestKit/RestKit).

Introduction  
------------

Just briefly how this little project of mine comes to life. I recently started
on iOS programming and wanting to create an app tightly integrated to a
commercial software product via RESTful APIs. To ease the integration, the most
obvious choice for the job seems to be
[RestKit](https://github.com/RestKit/RestKit). I worked on it and managed to
integrate to the first API, but the problem was that these APIs return rather
complex responses and it was a REAL boring and daunting task to create the
objects and mappings required.  What make it worse is that I am integrating to
15 APIs...

I figured I need a tool that would allow me to generate what's required and it
*SHOULD* be available somewhere on the internet. Unfortunately, despite
RestKit's large user base, there isn't anything  like that... and this is how
this project came to life.


How it works  
------------

Basically, RKGen parses JSON from either a file or HTTP response using
[ezMorph](http://ezmorph.sourceforge.net/), converts JSON to Java Ojbects and
then renders Objective-C headers and methods files with [Velocity
Templates](http://velocity.apache.org/).

Currently RKGen uses the following rules to generate Objective-c classes:

### Root object name

A "root object name" must be provided, this is because JSON does not require a
name for the "root", for example, consider the following JSON for a user
record:


	{
		"address":{
			"streetAddress": "21 2nd Street",
			"city":"New York"
		},
		"phoneNumber":
		[
			{
				"type":"home",
				"number":"212 555-1234"
			}
		]
	}


RKGen knows that there are two objects, "address" and "phoneNumber" in the
JSON file, but there is no way for RKGen to determine the name of the object
which holds the relationship to address and phoneNumber. By providing RKGen
with a "root object name" of UserRecord, it can generate a UserRecord class
with relationship to an Address class and a PhoneNumber class.

