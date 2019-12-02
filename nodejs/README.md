# Node example for DNB Open Banking APIs

## Usage

See [the main readme][] of the repo for a description on how to
retrieve api key.

To configure the credentials use environment variables. You can
put them in a file called `.env` in this directory or the main directory 
of this repository with the following variables set.

```
API_KEY=
```

After adding the credentials you can run the example with

```
node index.js
```

## Running the tests
In order to run the tests the env file above have to be configured as
the tests integrate with the live sandbox.

[the main readme]: ../README.md
