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

### Getting API key

In order to call the api, you will need API key. It
can be obtained from [developer.dnb.no][]. Register and login to create an example
app and you will get the credentials needed. Each of the examples has documentation
on how to configure it with the credentials.

_Never store your API key available publicly!_

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

All the examples are licensed under MIT license.
