The Python implementation was made on Python 2.7

It contains 2 classes:
- AwsSigningV4: taking care of the AWS signing V4 step
- Request handler: taking care of setting up the request (headers, params encoding, ...)

And the main, making 2 calls:
- Get API token
- Get customer details

Don't forget to replace the variables `client_id`, `client_secret` and `api_key` with your credentials.

Notes: 
The implementation of the AWS signing V4 part is based on: https://docs.aws.amazon.com/general/latest/gr/sigv4-signed-request-examples.html