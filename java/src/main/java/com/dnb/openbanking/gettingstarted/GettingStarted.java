package com.dnb.openbanking.gettingstarted;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import java.net.URI;
import org.json.JSONArray;
import org.json.JSONObject;

public class GettingStarted {

  // AWS signing V4 constants
  private static final String AWS_REGION = "eu-west-1";
  private static final String AWS_SERVICE = "execute-api";

  private static final String OPENBANKING_ENDPOINT = "https://developer-api-sandbox.dnb.no";

  private static String getApiToken(final AWS4Signer signer, final AWSCredentials awsCredentials) {
    final Request apiTokenRequest = new DefaultRequest(AWS_SERVICE);
    apiTokenRequest.setHttpMethod(HttpMethodName.GET);
    apiTokenRequest.setEndpoint(URI.create(OPENBANKING_ENDPOINT));
    apiTokenRequest.setResourcePath("/token");
    apiTokenRequest.addHeader("Accept", "application/json");
    apiTokenRequest.addHeader("Content-type", "application/json");
    apiTokenRequest.addHeader("x-api-key", Config.get("API_KEY"));
    apiTokenRequest.withParameter(
        "customerId", "{\"type\":\"SSN\", \"value\":\"29105573083\"}");

    signer.sign(apiTokenRequest, awsCredentials);

    final Response<JSONObject> apiTokenResponse
        = new AmazonHttpClient(new ClientConfiguration())
        .requestExecutionBuilder()
        .executionContext(new ExecutionContext(true))
        .request(apiTokenRequest)
        .errorResponseHandler(new ErrorResponseHandler(false))
        .execute(new ResponseHandler(false));
    final JSONArray tokenInfoArray
        = (JSONArray) (apiTokenResponse.getAwsResponse().get("tokenInfo"));
    final JSONObject tokenInfo = (JSONObject) tokenInfoArray.get(0);
    final String jwtToken = (String) tokenInfo.get("jwtToken");
    System.out.println("JWT token: " + jwtToken);
    return jwtToken;
  }

  private static void getCustomerInfo(
      final String jwtToken, final AWS4Signer signer, final AWSCredentials awsCredentials) {
     final Request customerRequest = new DefaultRequest(AWS_SERVICE);
    customerRequest.setHttpMethod(HttpMethodName.GET);
    customerRequest.setEndpoint(URI.create(OPENBANKING_ENDPOINT));
    customerRequest.setResourcePath("/customers/current");
    customerRequest.addHeader("Accept", "application/json");
    customerRequest.addHeader("Content-type", "application/json");
    customerRequest.addHeader("x-api-key", Config.get("API_KEY"));
    customerRequest.addHeader("x-dnbapi-jwt", jwtToken);

    signer.sign(customerRequest, awsCredentials);

    final Response<JSONObject> customerResponse
        = new AmazonHttpClient(new ClientConfiguration())
        .requestExecutionBuilder()
        .executionContext(new ExecutionContext(true))
        .request(customerRequest)
        .errorResponseHandler(new ErrorResponseHandler(false))
        .execute(new ResponseHandler(false));
    System.out.println(
        "Customer info: " + customerResponse.getAwsResponse().toString(4));
  }

  public static void main(final String[] args) {
    final AWSCredentials awsCredentials = new BasicAWSCredentials(Config.get("CLIENT_ID"), Config.get("CLIENT_SECRET"));
    final AWS4Signer signer = new AWS4Signer();
    signer.setRegionName(AWS_REGION);
    signer.setServiceName(AWS_SERVICE);

    final String jwtToken = getApiToken(signer, awsCredentials);
    getCustomerInfo(jwtToken, signer, awsCredentials);
  }
}
