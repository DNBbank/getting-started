import os
import pytest
from getting_started.main import (
    get_access_token,
    get_currency_conversion,
    get_currency_conversions,
    get_customer_info,
    get_test_customers,
)

if not os.environ.get("API_KEY"):
    pytest.skip("Missing credentials", allow_module_level=True)


@pytest.fixture
def token():
    return get_access_token("29105573083")


def test_get_access_token(token):
    assert len(token) > 500


def test_get_currency_conversion():
    currency = get_currency_conversion("EUR", "NOK")

    assert currency == {
        "baseCurrency": "EUR",
        "quoteCurrency": "NOK",
        "country": "NO",
        "updatedDate": "2019-04-25 09:00:00.0",
        "amount": 1,
        "buyRateTransfer": 9.6727,
        "sellRateTransfer": 9.6007,
        "midRate": 9.6367,
        "changeInMidRate": 0.0231,
        "previousMidRate": 9.6136,
        "buyRateCash": 10.1396,
        "sellRateCash": 9.1438,
    }


def test_get_currency_conversions():
    response = get_currency_conversions("NOK")

    assert len(response) > 0


def test_get_customer_info(token):
    customer = get_customer_info(token)

    assert customer == {
        "address": {
            "addressLine1": "Eliassenkroken 1",
            "addressLine2": "",
            "addressLine3": "",
            "postalAddressCountry": "NO",
            "postalCode": "6467",
            "postalCodeName": "Andreashavn",
        },
        "citizenship": [{"countryOfCitizenship": "NO"}],
        "countryOfBirth": None,
        "countryTax": [{"taxIdentificationNumber": "XX", "taxLiabilityCountry": "NO"}],
        "customerId": "29105573083",
        "customerType": "PERSON",
        "email": "odegard1955@example.com",
        "firstName": "Liv",
        "lastName": "Ødegård",
        "phone": "+4713857252",
    }


def test_get_test_customers():
    data = get_test_customers()

    assert len(data) > 5
    assert list(data[0].keys()) == ["ssn", "customerName"]
