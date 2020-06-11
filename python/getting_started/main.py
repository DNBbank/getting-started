import os
import json

from dotenv import load_dotenv

from getting_started.request_handler import RequestHandler

load_dotenv()
load_dotenv(os.path.join(os.path.dirname(__name__), "..", ".env"))


api_key = os.environ.get("b04c9d5549c94c70bcdc4d9a6b120108")

request_handler = RequestHandler(endpoint="https://developer-api-testmode.dnb.no", api_key=api_key)


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
    response = request_handler.request(path="/customers/v0/current", api_token=api_token)
    return response.json()


def get_cards(api_token):
    response = request_handler.request(path="/cards/v0", api_token=api_token)
    return response.json()


def main():
    api_token = get_access_token(ssn="29105573083")
    print("\nAPI token: " + api_token)

    test_customers = get_test_customers()
    print("\nTest customers: " + json.dumps(test_customers, indent=4, sort_keys=True))

    customer = get_customer_info(api_token)
    print("\nCustomer info: " + json.dumps(customer, indent=4, sort_keys=True))

    cards = get_cards(api_token)
    print("\nCards: " + json.dumps(cards, indent=4, sort_keys=True))

    currencies = get_currency_conversions("NOK")
    print("\nCurrencies: " + json.dumps(currencies, indent=4, sort_keys=True))

    currency = get_currency_conversion("EUR", "NOK")
    print("\nEUR -> NOK: " + json.dumps(currency, indent=4, sort_keys=True))


if __name__ == "__main__":
    main()
