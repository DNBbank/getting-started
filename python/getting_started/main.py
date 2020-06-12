import os
import json

from dotenv import load_dotenv

from .request_handler import RequestHandler

load_dotenv()
load_dotenv(os.path.join(os.path.dirname(__name__), "..", ".env"))


api_key = os.environ.get("API_KEY")

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


def main():
    test_customers = get_test_customers()
    print("\nTest customers: " + json.dumps(test_customers, indent=4, sort_keys=True))

    currencies = get_currency_conversions("NOK")
    print("\nCurrencies: " + json.dumps(currencies, indent=4, sort_keys=True))

    currency = get_currency_conversion("EUR", "NOK")
    print("\nEUR -> NOK: " + json.dumps(currency, indent=4, sort_keys=True))


if __name__ == "__main__":
    main()
