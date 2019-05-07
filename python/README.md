# Python example for DNB Open Banking APIs

## Usage

See [the main readme][] of the repo for a description on how to
retrieve client id, client secret and api key.

To configure the credentials use environment variables. You can
put them in a file called `.env` in this directory or the main directory 
following variables set.

```
CLIENT_ID=
CLIENT_SECRET=
API_KEY=
```

After adding the credentials you can run the example with

```shell
pipenv install # install dependencies
pipenv run python -m getting_started.main
```

This requires pipenv, which can be installed with pip install pipenv.

## Code structure

The code has three main parts, the main.py, request_handler.py and aws_signing.py. The
AWS signing is an implementation of the signing process which is described
in [the main readme][]. The main part is in main.py, which handles the
process of getting an api token and and calling our APIs as described in the
[the main readme][].

[the main readme]: ../README.md
