# Python example for DNB Open Banking APIs

## Requirements

* Python 2.7
* pipenv - pip install pipenv
* requests - pip install requests

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
pipenv --python path/to/python/2.7/exe
pipenv run python getting_started.py
```

## Code structure

The code has two main parts, the getting_started.py and aws_signing.py. The
AWS signing is an implementation of the signing process which is described
in [the main readme][]. The main part is in getting_started.py, which handles the
process of getting an api token and and calling our APIs as described in the
[the main readme][].

[the main readme]: (/../../README.md)
