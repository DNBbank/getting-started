# Dart example for DNB Open Banking APIs

## Usage

See [the main readme][] of the repo for a description on how to
retrieve client id, client secret and api key.

To configure the credentials use environment variables. You can
put them in a file called `.env` in this directory or the main directory 
of this repository with the following variables set.

```
CLIENT_ID=
CLIENT_SECRET=
API_KEY=
```

Before you can run the example you need to install dependencies

````
pub get
````

After adding the credentials you can run the example with

```
dart lib/main.dart
```

## Running the tests
In order to run the tests the env file above have to be configured as
the tests integrate with the live sandbox.

After adding the credentials you can run the tests with

```
pub run test tests/integration_tests.dart
```

[the main readme]: ../README.md