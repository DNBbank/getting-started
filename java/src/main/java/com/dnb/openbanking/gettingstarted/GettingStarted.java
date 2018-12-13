package com.dnb.openbanking.gettingstarted;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import java.io.InputStream;
import java.net.URI;
import org.json.JSONArray;
import org.json.JSONObject;

public class GettingStarted {

  // AWS signing V4 constants
  private static final String AWS_REGION = "eu-west-1";
  private static final String AWS_SERVICE = "execute-api";

  // Open Banking constants
  private static final String OPENBANKING_ENDPOINT = "https://developer-api-sandbox.dnb.no";
  private static final String API_KEY_HEADER = "x-api-key";
  private static final String JWT_TOKEN_HEADER = "x-dnbapi-jwt";

  private static Request createRequest(final HttpMethodName httpMethodName, final String path) {
    final Request request = new DefaultRequest(AWS_SERVICE);
    request.setHttpMethod(httpMethodName);
    request.setEndpoint(URI.create(OPENBANKING_ENDPOINT));
    request.setResourcePath(path);
    request.addHeader("Accept", "application/json");
    request.addHeader("Content-type", "application/json");
    request.addHeader(API_KEY_HEADER, Config.get("API_KEY"));
    return request;
  }

  private static JSONObject signAndSendRequest(
          final AWS4Signer signer, final AWSCredentials awsCredentials, final Request request) {
    signer.sign(request, awsCredentials);
    return new AmazonHttpClient(new ClientConfiguration())
            .requestExecutionBuilder()
            .executionContext(new ExecutionContext(true))
            .request(request)
            .errorResponseHandler(new ErrorResponseHandler(false))
            .execute(new ResponseHandler(false)).getAwsResponse();
  }

  private static String getApiToken(final AWS4Signer signer, final AWSCredentials awsCredentials) {
    final Request apiTokenRequest = createRequest(HttpMethodName.GET, "/token");
    apiTokenRequest.withParameter(
        "customerId", "{\"type\":\"SSN\", \"value\":\"29105573083\"}");

    final JSONObject apiTokenResponse = signAndSendRequest(signer, awsCredentials, apiTokenRequest);
    final JSONArray tokenInfoArray = (JSONArray) (apiTokenResponse.get("tokenInfo"));
    final JSONObject tokenInfo = (JSONObject) tokenInfoArray.get(0);
    final String jwtToken = (String) tokenInfo.get("jwtToken");
    System.out.println("JWT token: " + jwtToken);
    return jwtToken;
  }

  private static void getCustomerInfo(
      final String jwtToken, final AWS4Signer signer, final AWSCredentials awsCredentials) {
     final Request customerRequest = createRequest(HttpMethodName.GET, "/customers/current");
    customerRequest.addHeader(JWT_TOKEN_HEADER, jwtToken);

    final JSONObject customerResponse = signAndSendRequest(signer, awsCredentials, customerRequest);
    System.out.println("Customer info: " + customerResponse.toString(4));
  }

  private static void postInitiatePayment(
          final String jwtToken, final AWS4Signer signer, final AWSCredentials awsCredentials) {
    final Request initiatePaymentRequest = createRequest(HttpMethodName.POST, "/payments");
    initiatePaymentRequest.addHeader(JWT_TOKEN_HEADER, jwtToken);
    final ClassLoader classLoader = GettingStarted.class.getClassLoader();
    final InputStream initiatePaymentRequestBody
        = classLoader.getResourceAsStream("InitiatePaymentRequestBody.json");
    initiatePaymentRequest.setContent(initiatePaymentRequestBody);

    final JSONObject initiatePaymentResponse
        = signAndSendRequest(signer, awsCredentials, initiatePaymentRequest);
    System.out.println("Initiate Payment response: " + initiatePaymentResponse);
  }

  public static void main(final String[] args) {
    final AWSCredentials awsCredentials
        = new BasicAWSCredentials(Config.get("CLIENT_ID"), Config.get("CLIENT_SECRET"));
    final AWS4Signer signer = new AWS4Signer();
    signer.setRegionName(AWS_REGION);
    signer.setServiceName(AWS_SERVICE);

    final String jwtToken = getApiToken(signer, awsCredentials);
    getCustomerInfo(jwtToken, signer, awsCredentials);
    postInitiatePayment(jwtToken, signer, awsCredentials);
  }
}
