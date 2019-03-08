import os
from dotenv import load_dotenv
from aws import UrlHeaderGenerator
from aws import AwsSigningV4
import requests
import json

# Paths:
_loc_branch = "/locations/branches/"
_loc_atm = "/locations/atms/"

# Load keys:
load_dotenv()
_client_id = os.environ.get("CLIENT_ID")
_client_secret = os.environ.get("CLIENT_SECRET")
_api_key = os.environ.get("API_KEY")


def print_response(response):
    print(json.dumps(response.json(), indent=4, sort_keys=False))


class PaymentDto:
    def __init__(self, kid: [str, None],
                 debit_account_number: str,
                 credit_account_number: str,
                 amount: float,
                 requested_execution_date: str,
                 country: [str, None],
                 currency: [str, None],
                 immediate_payment: [bool, None]):

        self.kid = kid
        self.debitAccountNumber = debit_account_number
        self.creditAccountNumber = credit_account_number
        self.amount = amount,
        self.requestedExecutionDate = requested_execution_date
        self.country = country
        self.currency = currency
        self.immediatePayment = immediate_payment


class BaseClient:
    def __init__(self):
        if _api_key is None:
            raise Exception("Missing .env. Information in README at top-level directory.")
        aws_signing_v4 = AwsSigningV4(
            aws_access_key_id=_client_id,
            aws_secret_access_key=_client_secret,
            aws_host="developer-api-sandbox.dnb.no",
            aws_region="eu-west-1",
            aws_service="execute-api",
        )
        self.url_header_generator = UrlHeaderGenerator(
            endpoint="https://developer-api-sandbox.dnb.no",
            aws_signing_v4=aws_signing_v4,
        )
        self.api_token = None

    def get_token(self):
        return None

    def get_url_headers(self, path, query_parameters, method, get_token=True):
        if get_token and self.api_token is None:
            self.api_token = self.get_token()
        if query_parameters is None:
            query_parameters = {}
        return self.url_header_generator.generate(
            path=path, params=query_parameters, method=method, api_key=_api_key, api_token=self.api_token
        )

    def get(self, path, query_parameters=None, get_token=True):
        url, headers = self.get_url_headers(path, query_parameters, "GET", get_token=get_token)
        return requests.get(url, headers=headers)

    def put(self, path, query_parameters=None):
        request_url, headers = self.get_url_headers(path, query_parameters, "PUT")
        return requests.put(request_url, headers=headers)

    def post(self, path, data, query_parameters=None):
        request_url, headers = self.get_url_headers(path, query_parameters, "POST")
        return requests.post(request_url, data, headers=headers)

    def delete(self, path, query_parameters=None):
        request_url, headers = self.get_url_headers(path, query_parameters, "DELETE")
        return requests.delete(request_url, headers=headers)


class GeneralClient(BaseClient):

    # Currency:

    def get_currency_list(self, from_currency: str):
        if len(from_currency) != 3:
            raise AttributeError("currency must be string of length 3")
        from_currency = from_currency.upper()

        return self.get("/currencies/" + from_currency)

    def get_currency_rate(self, from_currency: str, to_currency: str):
        path = "/currencies/{}/convert/{}".format(from_currency, to_currency)
        return self.get(path)

    # Branches:

    def get_branches_list(self):
        return self.get(_loc_branch)

    def get_atm_list(self):
        return self.get(_loc_atm)

    def get_closest_branch(self, latitude: [str, float], longitude: [str, float]):
        return self.__get_closest__(_loc_branch, latitude, longitude)

    def get_closest_atm(self, latitude: [str, float], longitude: [str, float]):
        return self.__get_closest__(_loc_atm, latitude, longitude)

    def __get_closest__(self, path, latitude, longitude):
        latitude = str(latitude)
        longitude = str(longitude)
        return self.get(path + "coordinates", {"latitude": latitude, "longitude": longitude})

    def get_branch_details(self, branch_id: [int, str]):
        return self.get(_loc_branch + str(branch_id))

    def get_closest_branch_address(self, address: str):
        print("NOT IMPLEMENTED")
        return ""

    # Test customers:

    def get_test_customers(self):
        return self.get("/testCustomers/")


class PersonClient(BaseClient):
    def __init__(self, ssn):
        # Create API objects
        super().__init__()
        self.api_token_params = {"customerId": '{"type":"SSN", "value":"%s"}' % ssn}
        self.api_token = None

    def get_token(self):
        response = self.get("/token", self.api_token_params, get_token=False)
        return response.json()["tokenInfo"][0]["jwtToken"]

    def get_accounts(self):
        return self.get("/accounts/", {})

    def get_account_details(self, account_number: str):
        return self.get("/accounts/" + account_number, {})

    def get_account_balance(self, account_number: str):
        return self.get("/accounts/" + account_number + "/balance", {})

    def get_cards(self):
        return self.get("/cards/", {})

    def get_card_details(self, card_id: str):
        return self.get("/cards/" + card_id, {})

    def get_card_balance(self, card_id: str):
        return self.get("/cards/" + card_id + "/balance", {})

    def get_customer_details(self):
        return self.get("/customers/current", {})

    def get_due_payments(self, account_number: str):
        return self.get("/payments/" + account_number + "/due", {})

    def get_due_payments_by_id(self, account_number: str, payment_id: str):
        return self.get("/payments/" + account_number + "/due/" + payment_id, {})

    def get_transactions(self, account_number: str):
        return self.get("/transactions/" + account_number + "/", {})

    def put_block_card(self, card_id):
        return self.put("/cards/" + card_id + "/block", {})

    def put_unblock_card(self, card_id):
        return self.put("/cards/" + card_id + "/unblock", {})

    # NOT WORKING? However APIs are static sooooooo...
    def post_initiate_payment(self, payment: PaymentDto):
        return self.post("/payments/", json.dumps(payment.__dict__))

    # NOT TESTED
    def delete_payment(self, account_number: str, payment_id: str):
        return self.delete("/payments/" + account_number + "/pending-payments/" + payment_id, {})
