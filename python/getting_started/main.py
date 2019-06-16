import os
import json

from dotenv import load_dotenv

from .aws_signing import AwsSigningV4
from .request_handler import RequestHandler

load_dotenv()
load_dotenv(os.path.join(os.path.dirname(__name__), "..", ".env"))


client_id = os.environ.get("CLIENT_ID")
client_secret = os.environ.get("CLIENT_SECRET")
api_key = os.environ.get("API_KEY")

aws_signer = AwsSigningV4(
    aws_access_key_id=client_id,
    aws_secret_access_key=client_secret,
    aws_host="developer-api-testmode.dnb.no",
)

request_handler = RequestHandler(
    endpoint="https://developer-api-testmode.dnb.no", api_key=api_key, aws_signer=aws_signer
)


def get_currency_conversions(quoteCurrency):
    response = request_handler.request(path=f"/currencies/v1/convert/{quoteCurrency}")
    return response.json()


def get_currency_conversion(baseCurrency, quoteCurrency):
    response = request_handler.request(
        path=f"/currencies/v1/{baseCurrency}/convert/{quoteCurrency}"
    )
    return response.json()


def get_test_customers():
    response = request_handler.request(path="/test-customers/v0")
    return response.json()


def get_access_token(ssn):
    response = request_handler.request(path="/tokens/v0", method="POST", data={"ssn": ssn})
    return response.json()["jwtToken"]


def get_customer_info(api_token):
    response = request_handler.request(path="/customers/v0/current/", api_token=api_token)
    return response.json()


def main():
    api_token = get_access_token(ssn="29105573083")
    print("\nAPI token: " + api_token)

    test_customers = get_test_customers()
    print("\nTest customers: " + json.dumps(test_customers, indent=4, sort_keys=True))

    customer = get_customer_info(api_token)
    print("\nCustomer info: " + json.dumps(customer, indent=4, sort_keys=True))

    currencies = get_currency_conversions("NOK")
    print("\nCurrencies: " + json.dumps(currencies, indent=4, sort_keys=True))

    currency = get_currency_conversion("EUR", "NOK")
    print("\nEUR -> NOK: " + json.dumps(currency, indent=4, sort_keys=True))


if __name__ == "__main__":
    main()
