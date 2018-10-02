import requests
import urllib
import json
import datetime
import hashlib
import hmac


class AwsSigningV4(object):
    __ALGORITHM = "AWS4-HMAC-SHA256"

    def __init__(
        self,
        aws_access_key_id,
        aws_secret_access_key,
        aws_host,
        aws_region,
        aws_service,
    ):
        self.__aws_access_key_id = aws_access_key_id
        self.__aws_secret_access_key = aws_secret_access_key
        self.__aws_host = aws_host
        self.__aws_region = aws_region
        self.__aws_service = aws_service

    def __sign(self, key, msg):
        return hmac.new(key, msg.encode("utf-8"), hashlib.sha256).digest()

    def __get_signature_key(self, key, date_stamp, region_name, service_name):
        k_date = self.__sign(("AWS4" + key).encode("utf-8"), date_stamp)
        k_region = self.__sign(k_date, region_name)
        k_service = self.__sign(k_region, service_name)
        k_signing = self.__sign(k_service, "aws4_request")
        return k_signing

    def headers_for_get_method(self, path, request_parameters):
        # Create a date for headers and the credential string
        now = datetime.datetime.utcnow()
        amz_date = now.strftime("%Y%m%dT%H%M%SZ")
        # Date w/o time, used in credential scope
        date_stamp = now.strftime("%Y%m%d")
        canonical_uri = path
        canonical_querystring = request_parameters
        canonical_headers = "host:" + self.__aws_host + "\n"
        canonical_headers += "x-amz-date:" + amz_date + "\n"
        signed_headers = "host;x-amz-date"
        payload_hash = hashlib.sha256("".encode("utf-8")).hexdigest()
        canonical_request = "GET" + "\n"
        canonical_request += canonical_uri + "\n"
        canonical_request += canonical_querystring + "\n"
        canonical_request += canonical_headers + "\n"
        canonical_request += signed_headers + "\n"
        canonical_request += payload_hash

        credential_scope = date_stamp + "/"
        credential_scope += self.__aws_region + "/"
        credential_scope += self.__aws_service + "/" + "aws4_request"
        string_to_sign = self.__ALGORITHM + "\n"
        string_to_sign += amz_date + "\n"
        string_to_sign += credential_scope + "\n"
        string_to_sign += hashlib.sha256(canonical_request.encode("utf-8")).hexdigest()

        signing_key = self.__get_signature_key(
            key=self.__aws_secret_access_key,
            date_stamp=date_stamp,
            region_name=self.__aws_region,
            service_name=self.__aws_service,
        )

        signature = hmac.new(
            signing_key, string_to_sign.encode("utf-8"), hashlib.sha256
        ).hexdigest()

        authorization_header = self.__ALGORITHM + " "
        credential_header = "Credential=" + self.__aws_access_key_id + "/"
        credential_header += credential_scope
        authorization_header += credential_header + ", "
        authorization_header += "SignedHeaders=" + signed_headers + ", "
        authorization_header += "Signature=" + signature

        headers = {"x-amz-date": amz_date, "Authorization": authorization_header}

        return headers


class RequestHandler(object):
    def __init__(self, endpoint, aws_signing_v4):
        self.__endpoint = endpoint
        self.__aws_signing_v4 = aws_signing_v4

    def __to_canonical_querystring(self, params):
        canonical_querystring = ""
        # parameters have to be sorted alphabetically for the signing part
        for param_key, param_value in sorted(params.iteritems()):
            if canonical_querystring != "":
                canonical_querystring += "&"
            canonical_querystring += param_key + "=" + urllib.quote(param_value)
        return canonical_querystring

    def get_request(self, path, params, api_token=None):
        canonical_querystring = self.__to_canonical_querystring(params)
        headers = self.__aws_signing_v4.headers_for_get_method(
            path, canonical_querystring
        )

        # 'host' header is added automatically by the Python 'requests' library.
        headers["Accept"] = "application/json"
        headers["Content-type"] = "application/json"
        headers["x-api-key"] = api_key

        # All endpoints require the API token, except the API token endpoint.
        if api_token:
            headers["x-dnbapi-jwt"] = api_token

        request_url = self.__endpoint + path + "?" + canonical_querystring
        return requests.get(request_url, headers=headers)


if __name__ == "__main__":
    # Developer's credentials
    client_id = ""
    client_secret = ""
    api_key = ""

    # AWS signing v4 constants
    aws_host = "developer-api-sandbox.dnb.no"
    aws_region = "eu-west-1"
    aws_service = "execute-api"

    openbanking_endpoint = "https://developer-api-sandbox.dnb.no"

    aws_signing_v4 = AwsSigningV4(
        aws_access_key_id=client_id,
        aws_secret_access_key=client_secret,
        aws_host=aws_host,
        aws_region=aws_region,
        aws_service=aws_service,
    )
    request_handler = RequestHandler(
        endpoint=openbanking_endpoint, aws_signing_v4=aws_signing_v4
    )

    # Get API Token
    api_token_params = {"customerId": '{"type":"SSN", "value":"29105573083"}'}
    api_token_path = "/api/token"
    api_token_response = request_handler.get_request(
        path=api_token_path, params=api_token_params
    )
    api_token = api_token_response.json()["tokenInfo"][0]["jwtToken"]
    print("api_token: " + api_token)

    # Get customer details
    customer_params = {}
    customer_path = "/customers/current"
    customer_response = request_handler.get_request(
        path=customer_path, params=customer_params, api_token=api_token
    )
    customer_response_json = json.loads(customer_response.text)
    print(json.dumps(customer_response_json, indent=4, sort_keys=True))
