package no.dnb.openbanking.gettingstarted;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.AmazonHttpClient.RequestExecutionBuilder;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

public class GettingStarted {

  // AWS signing V4 constants
  private static final String AWS_REGION = "eu-west-1";
  private static final String AWS_SERVICE = "execute-api";

  // Open Banking constants
  private static final String OPENBANKING_ENDPOINT = "https://developer-api-testmode.dnb.no";
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

  private static RequestExecutionBuilder buildRequest(final Request request) {
    try {
      return new AmazonHttpClient(new ClientConfiguration()).requestExecutionBuilder()
          .executionContext(new ExecutionContext(true)).request(request)
          .errorResponseHandler(new ErrorResponseHandler(false));
    } catch (AmazonServiceException exception) {
      System.out.println("Unexpected status code in response: " + exception.getStatusCode());
      System.out.println("Content: " + exception.getRawResponseContent());
      throw new RuntimeException("Failed request. Aborting.");
    }
  }

  private static RequestExecutionBuilder signAndBuildRequest(final AWS4Signer signer,
      final AWSCredentials awsCredentials, final Request request) {
    signer.sign(request, awsCredentials);
    return buildRequest(request);
  }

  public static String getApiToken(final AWS4Signer signer, final AWSCredentials awsCredentials) {
    final Request apiTokenRequest = createRequest(HttpMethodName.POST, "/tokens");
    String content = "{\"ssn\": \"29105573083\"}";
    apiTokenRequest.setContent(new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8"))));

    final JSONObject apiTokenResponse = signAndBuildRequest(signer, awsCredentials, apiTokenRequest)
        .execute(new ResponseHandlerJSONObject(false)).getAwsResponse();
    return (String) (apiTokenResponse.get("jwtToken"));
  }

  public static Response<JSONObject> getCustomerInfo(final String jwtToken, final AWS4Signer signer,
      final AWSCredentials awsCredentials) {
    final Request customerRequest = createRequest(HttpMethodName.GET, "/customers/current");
    customerRequest.addHeader(JWT_TOKEN_HEADER, jwtToken);

    return signAndBuildRequest(signer, awsCredentials, customerRequest).execute(new ResponseHandlerJSONObject(false));
  }

  public static Response<JSONArray> getCurrencyConversions(String quoteCurrency) {
    final Request customerRequest = createRequest(HttpMethodName.GET, "/currencies/" + quoteCurrency);

    return buildRequest(customerRequest).execute(new ResponseHandlerJSONArray(false));
  }

  public static Response<JSONObject> getCurrencyConversion(String quoteCurrency, String baseCurrency) {
    final Request customerRequest = createRequest(HttpMethodName.GET,
        "/currencies/" + quoteCurrency + "/convert/" + baseCurrency);

    return buildRequest(customerRequest).execute(new ResponseHandlerJSONObject(false));
  }

  public static Response<JSONArray> getCardInfo(final String jwtToken, final AWS4Signer signer,
      final AWSCredentials awsCredentials) {
    final Request cardRequest = createRequest(HttpMethodName.GET, "/cards");
    cardRequest.addHeader(JWT_TOKEN_HEADER, jwtToken);

    return signAndBuildRequest(signer, awsCredentials, cardRequest).execute(new ResponseHandlerJSONArray(false));
  }

  public static void main(final String[] args) {
    final AWSCredentials awsCredentials = new BasicAWSCredentials(Config.get("CLIENT_ID"), Config.get("CLIENT_SECRET"));
    final AWS4Signer signer = new AWS4Signer();
    signer.setRegionName(AWS_REGION);
    signer.setServiceName(AWS_SERVICE);

    final String jwtToken = getApiToken(signer, awsCredentials);
    System.out.println("JWT token: " + jwtToken);

    final Response<JSONArray> currenciesResponse = getCurrencyConversions("NOK");
    System.out.println("Currencies: " + currenciesResponse.getAwsResponse().toString(4));

    final Response<JSONObject> currencyResponse = getCurrencyConversion("NOK", "EUR");
    System.out.println("Currency: " + currencyResponse.getAwsResponse().toString(4));

    final Response<JSONObject> customerResponse = getCustomerInfo(jwtToken, signer, awsCredentials);
    System.out.println("Customer info: " + customerResponse.getAwsResponse().toString(4));

    final Response<JSONArray> cardResponse = getCardInfo(jwtToken, signer, awsCredentials);
    System.out.println("Card info: " + cardResponse.getAwsResponse().toString(4));
  }
}
