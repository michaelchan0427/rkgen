RKGen
=====

RKGen generates Objective-c objets from JSON, and it also generates mapper classes 
that can be used with [RestKit](https://github.com/RestKit/RestKit).

Introduction
------------

Just briefly how this little project of mine comes to life. I recently started on iOS
programming and wanting to create an app tightly integrated to a commercial software product
via RESTful APIs. To ease the integration, the most obvious choice for the job seems to be 
[RestKit](https://github.com/RestKit/RestKit).
I worked on it and managed to integrate to the first API, but the problem was that these APIs return rather complex
responses and it was a REAL boring and daunting task to create the objects and mappings required. 
What make it worse is that I am integrating to 15 APIs...

I figured I need a tool that would allow me to generate what's required and it *SHOULD* be available
somewhere on the internet. Unfortunately, despite RestKit's large user base, there isn't anything 
like that... and this is how this project came to life.


