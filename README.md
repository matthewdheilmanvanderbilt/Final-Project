# Available Bike Donations (ABD) API
## About The Project
This project is a collection of API's to maintain a list of bicycles that were donated and available to be received by individuals seeking free bicycles.

## Legal Stuff
The ownership of this code base is exclusively held by Matthew Heilman.  If you  view, modify, evaluate, contribute, change, offer suggestions, etc, you are giving up all rights to ownership of this code and the intellectual property.  Do not proceed in looking at this code unless you agree with that statement.  
## Technology
### Architecture
The ABD API is built on a serverless architecture.  The goal was to create an solution in which no infrastructure needed to be managed (provisions, scaled, maintained, etc).

![alt text](https://github.com/matthewdheilmanvanderbilt/Final-Project/blob/master/ArchitectureScreenShot.png?raw=true)

###Stack
* Database - AWS DynamoDB
* API's - AWS Lambda's written in Java
* API Gateway - AWS API Gateway
* Log Aggregator - AWS CloudWatch
* Packaging/Deployment - Serverless Framework
* Unit Test Framework - JUnit
* Integration Test Framework - Postman / Newman 


# Setting up Development Environment
## Clone Repo
`git clone https://github.com/vu-5278-su21/track-2-exercise-java-streams-lambdas-matthewdheilmanvanderbilt`

## Install the following packages/applications:
* [NodeJS](https://nodejs.org/en/) (LTS recommended)
* [NPM](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)
* [Serverless Framework](https://www.serverless.com/framework/docs/providers/aws/guide/installation/)
* [Java](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/windows-7-install.html) (JDK v10 or higher)
* [Maven](https://maven.apache.org/install.html)

## Create and Use AWS API Keys
* https://www.serverless.com/framework/docs/providers/aws/guide/credentials/

## Build and Deploy the App
1. Build jar `mvn clean install`
1. Deploy the application to AWS `sls deploy`

# Application Details
## APIs

## Database Schema
* Primary Key - UUID generated during the insertion of the record
* Sort Key (Range) - A binary geohash.


#### Out of Scope / Tech Debt / Future Plans
* The GeoHash search is using "starts with" searching.  This works for the MVP because only US addresses will be accepted.  However, this functionality will break when crossing the equator or the prime meridian.  Future global "nearby" searching will need to correct this issue by using a better GeoHash proximity/radius search.
* The GeoHash is currently using a binary interleaved string.  This should be converted to base32 to reduce the size and (possibly) increase the readability of the GeoHash.
* The jar file is rather large and Java does not appear to be the preferred language for Lambda's.  It may be causing the cold start that is apparent.  Possibly convert the API's to js/node or python.
* There is almost no exception handling for unexpected user input
* No testing has been done to determine if this suite is subject penetration.  At least OWASP Top 10 should be test/handled in the next iteration.
* Open API - no API Keys or other authorization mechanism employed.  Will use AWS Cognito in a future version

## Acknowledgements & References
* Amazon DynamoDB and Serverless - The Ultimate Guide: https://www.serverless.com/dynamodb
* Nice API Documentation Spec: https://gist.github.com/iros/3426278
* Lon/Lat API Service:  http://julien.gunnm.org/geek/programming/2015/09/13/how-to-get-geocoding-information-in-java-without-google-maps-api/
* Lon/Lat API: https://nominatim.openstreetmap.org/
* Postman Learning https://learning.postman.com/docs/designing-and-developing-your-api/monitoring-your-api/monitoring-apis-websites/#running-an-api-test-suite

## Class Items
* The DynamoDB Data Access Layer is a singleton and uses the adapter pattern
* The GeoHash class is a singleton
* DRY and other best practice were taken from Uncle Bob's book
* Functional Programming/ Lambda's (not AWS Lambda's) were used to simplify code and make it more readable.

**Get All Bikes**
----
  Fetch all bikes
  
-  **URL**

   /bikes

-  **Method:**
  
   GET

  
-  **URL Params**

   none
   
-  **Query String Params**

   none

-  **Data Params**

   none

-  **Success Response:**

    - **Code:** 200 <br />
    - **Content:** `  `[{"id":"82d30b71-5198-4259-8ba7-0c834721a71f","description":"Schwinn Banana Seat","gender":"U","frameSize":"15-16","condition":"good","address":" Downingtown, PA 19335","longitude":-75.7032742,"latitude":40.0064958,"location":"1001101011"},{"id":"1bdb5cde-e02c-4155-a474-80261923e3e9","description":"Schwinn Banana Seat","gender":"U","frameSize":"15-16","condition":"good","address":"West Chester, PA 19380","longitude":-75.6059638,"latitude":39.9597213,"location":"100110101100010010100111011100001011100101001110111011111001"},{"id":"12af59ae-1abb-4a21-b3ac-a9e5ac133580","description":"Schwinn Banana Seat","gender":"U","frameSize":"15-16","condition":"good","address":"The White House, Washington DC","longitude":-77.03655315,"latitude":38.897699700000004,"location":"100110100110101100100111000111010101111011010000001111000100"}]``
 
 
-  **Error Response:**

    - **Code:** 500 INTERNAL SERVER ERROR <br />
    - **Content:** `{"Error getting all bikes: {input}}`


-  **Sample Call:**

   curl -X GET https://ny8ztrtpv8.execute-api.us-east-1.amazonaws.com/dev/bikes
  
**Get Individual Bike**
----
  Fetch one bike by ID
  
-  **URL**

   /bikes

-  **Method:**
  
   GET
  
-  **URL Params**

   **Required:**

   id=[string] - The ID of the bike

-  **Query String Params**

   none

-  **Data Params**

   none

-  **Success Response:**

    -  **Code:** 200
    **Content:** `  `[{"id":"82d30b71-5198-4259-8ba7-0c834721a71f","description":"Schwinn Banana Seat","gender":"U","frameSize":"15-16","condition":"good","address":" Downingtown, PA 19335","longitude":-75.7032742,"latitude":40.0064958,"location":"1001101011"}]``
 
 
-  **Error Response:**
    -  **Code:** 404 NOT FOUND 
    -  **Content:** `{"Bike with id: '" + bikeId + "' not found."}`<br><br>

    -  **Code:** 500 INTERNAL SERVER ERROR 
    -  **Content:** `{"Error getting bike: {input}}`


-  **Sample Call:**

   curl -X GET https://ny8ztrtpv8.execute-api.us-east-1.amazonaws.com/dev/bikes/82d30b71-5198-4259-8ba7-0c834721a71f
  
**Get Nearby Bikes**
----
  Fetch all bikes that are near a specified address using a GeoHash proximity search.
  
-  **URL**

   /bikes

-  **Method:**
  
   GET
  
-  **URL Params**

   **Required:**

   postalCode=[string] - The ID of the bike

-  **Query String Params**

   **Required:**

   range=[int] - a value from 0 to 12 that indicates the degree of length of the geohash to use when searching for nearby bikes.  The number specified
   is multiplied by 5 to get the number of bits to use int he GeoHash
  
    * 0 = Use full Geohash (in reality, not using any GeoHash searching)
    * 1 = Use 1st 5 bits of specified GeoHash
    * 2 = Use 1st 10 bits of specified GeoHash
    * ...
    * 12 = Use 1st 60 bits of the specified GeoHash 
  
  
*  **Data Params**

   none
   
   
-  **Success Response:**

    -  **Code:** 200 <br />
    -  **Content:** `  `[{"id":"82d30b71-5198-4259-8ba7-0c834721a71f","description":"Schwinn Banana Seat","gender":"U","frameSize":"15-16","condition":"good","address":" Downingtown, PA 19335","longitude":-75.7032742,"latitude":40.0064958,"location":"1001101011"}]``
 
 
-  **Error Response:**

    -  **Code:** 500 INTERNAL SERVER ERROR <br />
    -  **Content:** `{"Error listing bikes nearby: ", {input}}`;


-  **Sample Call:**

   curl -x GET https://ny8ztrtpv8.execute-api.us-east-1.amazonaws.com/dev/bikes/nearby/Downingtown%20PA%2019335?range=5
  
**Create Bike**
----
  Creates a new bike entry
  
-  **URL**

   /bikes

-  **Method:**
  
   POST
  
-  **URL Params**

   none

-  **Query String Params**

   none
  
-  **Data Params**

 **Required:**

   * description=[string]
   * gender=[char] U=Unisex, M=Male, F=Female
   * frameSize=[string] In inches
   * condition=[string]
   * address=[string]
   
   
-  **Success Response:**

    -  **Code:** 200 <br />
    -  **Content:**   `{"id":"ebfbd338-bc84-44de-803a-8eb1173a8fde","description":"Schwinn Banana Seat","gender":"U","frameSize":"15-16","condition":"good","address":"West Chester, PA 19380","longitude":-75.6059638,"latitude":39.9597213,"location":"100110101100010010100111011100001011100101001110111011111001"}`
 
 
-  **Error Response:**

    -  **Code:** 500 INTERNAL SERVER ERROR <br />
    -  **Content:** `{"Error saving bike: ", {input}}`;


-  **Sample Call:**

   curl -X POST https://ny8ztrtpv8.execute-api.us-east-1.amazonaws.com/dev/bikes -d '{"description": "Schwinn Banana Seat", "gender": "U", "frameSize": "15-16", "condition": "good", "address": "West Chester, PA 19380"}'
  
**Delete Individual Bike**
----
  Delete one bike by ID
  
-  **URL**

   /bikes

-  **Method:**
  
   DELETE
  
-  **URL Params**

   **Required:**

   id=[string] - The ID of the bike

-  **Query String Params**

   none

-  **Data Params**

   none

-  **Success Response:**

    -  **Code:** 204 NO CONTENT <br />
    -  **Content:** 
 
 
-  **Error Response:**

    -  **Code:** 404 NOT FOUND 
    -  **Content:** `{"Bike with id: '" + bikeId + "' not found."}`<br /><br />

    -  **Code:** 500 INTERNAL SERVER ERROR <br />
    -  **Content:** `{"Error deleting bike: {input}}`


-  **Sample Call:**

   curl -X DELETE https://ny8ztrtpv8.execute-api.us-east-1.amazonaws.com/dev/bikes/0d909546-0b04-4a7b-a17c-2952e8408d15

