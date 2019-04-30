import datetime
import json

from getting_started.aws_signing import AwsSigningV4

signer = AwsSigningV4(
    aws_access_key_id="access key",
    aws_secret_access_key="secret key",
    aws_host="developer-api.dnb.no",
)


def test_aws_signing_get_request(mocker):
    with mocker.patch(
        "getting_started.aws_signing.now", return_value=datetime.datetime(2018, 6, 2)
    ):
        headers = signer.create_headers(path="/tokens", method="GET")

        assert headers["Authorization"] == (
            "AWS4-HMAC-SHA256 Credential=access key/20180602/eu-west-1/execute-api/aws4_request, "
            "SignedHeaders=host;x-amz-date, "
            "Signature=1672f85f04d1375ffc1f91881d4e3ff6a583242fce8c6d92ba15544a63dd4dcb"
        )


def test_aws_signing_post_request(mocker):
    with mocker.patch(
        "getting_started.aws_signing.now", return_value=datetime.datetime(2018, 6, 2)
    ):
        headers = signer.create_headers(
            path="/tokens", method="POST", data=json.dumps({"ssn": "29105573083"})
        )

        assert headers["Authorization"] == (
            "AWS4-HMAC-SHA256 Credential=access key/20180602/eu-west-1/execute-api/aws4_request, "
            "SignedHeaders=host;x-amz-date, "
            "Signature=1f1eb16d666394ba57522b01db51e1da0f2f272a4b48aba9011e5c4bb8540cac"
        )
        assert (
            headers["x-amz-content-sha256"]
            == "b80fb83935fba3770a2436d26c84767b99f487250b6b7505a470153c47ecdcbb"
        )
