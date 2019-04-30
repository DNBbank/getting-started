import os
import requests
import urllib
import json
from dotenv import load_dotenv

from aws_signing import AwsSigningV4
from request_handler import RequestHandler

load_dotenv()
load_dotenv(os.path.join(os.path.dirname(__name__), "..", ".env"))


client_id = os.environ.get("CLIENT_ID")
client_secret = os.environ.get("CLIENT_SECRET")
api_key = os.environ.get("API_KEY")

aws_signer = AwsSigningV4(
    aws_access_key_id=client_id,
    aws_secret_access_key=client_secret,
    aws_host="developer-api-testmode.dnb.no",
    aws_region="eu-west-1",
    aws_service="execute-api",
)

request_handler = RequestHandler(
    endpoint="https://developer-api-testmode.dnb.no", 
    api_key=api_key,
    aws_signer=aws_signer
)

def getAccessToken(ssn): 
    response = request_handler.request(
        path="/tokens",
        method='POST',
        data={ 'ssn': ssn }
    )
    return response.json()["jwtToken"]

def getCustomerInfo(api_token):
    response = request_handler.request(
        path="/customers/current", 
        api_token=api_token
    )
    return response.json()


def main():
    api_token = getAccessToken(ssn="29105573083")
    print("\nAPI token: " + api_token)

    customer = getCustomerInfo(api_token)
    print('\nCustomer info: ' + json.dumps(customer, indent=4, sort_keys=True))



if __name__ == "__main__":
    main()
