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


Features
--------

1. Generates Objective-C classes from JSON file or HTTP response.
1. Generates [RestKit](https://github.com/RestKit/RestKit) compatible mapper methods mapping JSON to the generated classes.
1. Templated code generation approach.
1. Configurable property type mapping.
1. Configurable restricted keywords for property name in generated classes.



How it works  
------------

Basically, RKGen parses JSON from either a file or HTTP response using
[ezMorph](http://ezmorph.sourceforge.net/), converts JSON to Java Ojbects and
then renders Objective-C headers and methods files with [Velocity
Templates](http://velocity.apache.org/).

RKGen concepts
--------------

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


RKGen knows that there are two objects, "address" and an array of "phoneNumber" in the
JSON file, but there is no way for RKGen to determine the name of the object
which holds the relationship to address and phoneNumber. By providing RKGen
with a "root object name" of UserRecord, it can generate a UserRecord class
with relationship to an Address class and a PhoneNumber class.

### What does RKGen generate?

RKGen generates four types of files, each having its own template:

* headertemplate.vm - Template for object header files (".h")
* implementationtemplate.vm - Template for object method files (".m")
* mapperHearderTemplate.vm - Template for header of the mapping containing the mapping of JSON objects to Objective-C objects.
* mapperImplementationTemplate.vm - Template for the method files including the actual implementation of the mappings.

In the generated Objective-c code, each of the JSON object will have its own .h and .m files, and one mapping class, named according to the root object name.

It is very important to understand [Velocity syntax](http://velocity.apache.org/) should you wish to change the templates, which are available under the bin/templates directory. 


### Class / attribute naming rule

RKGen uses a simple approach to generate Objective-c classes, it simply reads the object name, capitalise the first letter and remove 's' if it is the last letter. (I know this is not optimal, also see the Know Issues section...) e.g.:

	{
		"team":{
			"name": "The A-Team",
			"country":"United States"
		},
		"members":
		[
			{
				"name":"Foo",
				"email":"foo@bar.com"
			},
			{
				"name":"Bar",
				"email":"Bar@foo.com"
			}
		]
	}

In the example above, if the root object name is TeamRecord, then RKGen will generate the following classes:

* TeamRecord
* Member
* Team
* TeamRecordMapper

And if a class prefix is provided, e.g. RK, then the generated class name becomes:

* RKTeamRecord
* RKMember
* RKTeam
* RKTeamRecordMapper

However at the time of writing, I just realized that there is a limitation to the approach, if we have an address object, then the generated class would be Addres. This will be addressed in the next release.

### Restricted keywords

Just like other programming languages, a number of keywords are reserved and cannot be used as Class / property name. RKGen simply utilise the restrictedKeywords file under the bin/conf directory to determine the words to avoid. Again, at the time of writing, I found another bug, the restrictions is only applied to property name but not class name...

### Type mapping

RKGen uses the following rules to map json object properties to Objective-C type. We will refer to the example below to explain these rules:

	{
		"team":{
			"creationTime": "2012-08-25T11:04:07+01:00"
			"name": "The A-Team",
			"country":"United States"
			"teamMatchRecords":{
				"win": "200",
				"lost": "100",
				"draw": "2"
			}
		},
		"members":
		[
			{
				"name":"Foo",
				"email":"foo@bar.com"
			},
			{
				"name":"Bar",
				"email":"Bar@foo.com"
			}
		]
	}

1. **One-to-many** - If the relationship is one-to-many, i.e. the property "members" above, RKGen reads the "array" key in the attributetypes.properties file to populate the Objective-C property type, it is NSMutableArray by default.

1. **User configured** - RKGen concatenates the object name with the property name as a key to perform lookup in the attributetypes.properties file to find a mapping, e.g. if we have team.creationTime=NSDate in the properties file, then in the generated Objective-C class for Team, the creationTime property would be of type NSDate. The rule to create the key is the same for nested objects, e.g. teamMatchRecords.win=NSInteger would map the property to NSInteger type.

1. **Default** - After the lookup mentioned above, if there isn't a mapping for the property in the attributetypes.properties file, then it would use the key "default" in the properties file. The file distributed with the binary has the key default maps to NSString.

### Mapper generation

Personally I think mapper generation is the most important feature in RKGen. After generating all the Objective-C classes from the JSON, it'd be pretty useless without explicit mappings of objects and properties from the original JSON to the classes. RKGen generates Objective-C singleton class containing all the mapping required, and of course, the mapping only works with RestKit. 


Known issues
------------

1. Singulars and Plurals Class names might not be generated in an optimal way, currently RKGen only capitalises the first letter and removes the trailing 's', so addresses will become Addresse and Address will become Addres. This will not be fixed since this opens up a whole can of worms... 

1. Date format is currently hardcoded in the mapper file.

Contribute
----------

Just let me know if you wish to contribute. 


Unrelated
---------

First time I use Git and release opensource code on GitHub so Comments & Recommendations welcome!!




