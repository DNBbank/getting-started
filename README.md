# About this repository
In this repo you will find code sorted by languages to get you started with DNB's Open Banking APIs.
The aim of this repo is to provide code that can just be used once the developer's credentials are setup in each file.
You will find a README file in each subfolder that will add complementary information about the language-specific code.

# Steps to your first API call
## 1. Generate the AWS signing V4 headers
You need to use [AWS Signing V4](https://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html) to sign all requests towards our APIs.
Every piece of code in this repo includes this signing step but the detailed process to do so will not be discribed here.
We provide an implementation of the AWS Singing V4 step to make developers' start easier, but if you would like to understand the implementation, please have a look at AWS docs and particularly the [code example](https://docs.aws.amazon.com/general/latest/gr/sigv4-signed-request-examples.html).

## 2. Get the API token
Every request towards our APIs requires to pass an API token recently generated.
The only API which does not require this token is the "API Token" endpoint, which generates the token.
So all the code examples will make a first request towards the "API token" endpoint to generate that token and then include it in the following requests.

## 3. [GET] Get customer details
This request will retrieve information about a dummy customer from our "Customers" endpoint.
This is an example of a how to do a GET request towards our APIs, you will easily adapt the code for all other endpoints using the GET method.

## 4. [POST] Initiate a payment
TODO
