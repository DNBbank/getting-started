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
    currency = get_currency_conversion("NOK", "EUR")

    assert currency == {
        "baseCurrency": "EUR",
        "quoteCurrency": "NOK",
        "country": "EU",
        "updatedDate": "2018-11-06 09:00:00.0",
        "unit": 1,
        "buyRateTransfer": 9.5047,
        "sellRateTransfer": 9.5767,
        "midRate": 9.5407,
        "changeInRate": 1.17,
        "previousRateTransfer": 9.529,
        "buyRateCash": 9.0478,
        "sellRateCash": 10.04,
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
