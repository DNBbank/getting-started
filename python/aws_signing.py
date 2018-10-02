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
