# Getting started examples for DNB Open Banking

In this repo you will find code sorted by languages to get you started with DNB's
Open Banking APIs. The aim of this repo is to provide code that can just be used once
the developer's credentials are setup in each file. You will find a readme file in
each subfolder that will add complementary information about the language-specific code.

## tldr;

1. Register an application at [developer.dnb.no][].
2. Configure credentials in `.env` file for running code examples. If you want
to run postman please follow the [postman readme][].
3. Run one of the examples and check out the code to see what is happening. Make sure that you have attached all API's at [developer.dnb.no][] for the examples to run correctly.

### Getting client-id, client-secret and api-key

In order to call the api, you will need client-id, client-secret and api-key. It
can be obtained from [developer.dnb.no][]. Register and login to create an example
app and you will get the credentials needed. Each of the examples has documentation
on how to configure it with the credentials.

*Never store your API key, Client ID and Client Secret available publicly!*

### Configuring the credentials

If you put the credentials in a `.env` file in the root directory it will work
for all the examples except postman. It is also possible to put the `.env` file
in the directory of the example you want to run. See [.env.example][] for a template
for the `.env` file.

### Running the examples

Each example contains a readme with documentation on how to run them. There is also
script that is helpful for running the examples:

```shell
./run <example>  # e.g. ./run nodejs
```

## Steps to your first API call

### 1. Get the access token

Every request towards our APIs that requires end user authentication requires an access token.
To obtain this token send a post request to `/tokens` with the SSN in the payload. Example SSNs
can be fetched from the test-customers endpoint which only requires a API-key from the portal.

#### AWS signing

You need to use [AWS Signing V4][] to sign all requests towards the token endpoint. Every piece of
code in this repo includes this signing step but the detailed process to do so will not
be described here. We provide an implementation of the AWS Singing V4 step to make developers'
start easier, but if you would like to understand the implementation, please have a look at
AWS docs and particularly the [code example][aws-signing-example].

### 2. Get customer details

This request will retrieve information about a dummy customer from our "Customers" endpoint.
This is an example of a how to do a GET request towards our APIs, you will easily adapt the
 code for all other endpoints using the GET method.

[developer.dnb.no]: https://developer.dnb.no
[AWS Signing V4]: https://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html
[aws-signing-example]: https://docs.aws.amazon.com/general/latest/gr/sigv4-signed-request-examples.html
[postman readme]: ./postman/README.md

-------------------

All the examples are licensed under MIT license.
