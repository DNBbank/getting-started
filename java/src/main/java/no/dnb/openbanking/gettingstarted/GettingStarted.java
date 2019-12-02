package no.dnb.openbanking.gettingstarted;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.AmazonHttpClient.RequestExecutionBuilder;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.Charset;

public class GettingStarted {
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

  public static String getApiToken() {
    final Request apiTokenRequest = createRequest(HttpMethodName.POST, "/tokens/v0");
    String content = "{\"ssn\": \"29105573083\"}";
    apiTokenRequest.setContent(new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8"))));

    final JSONObject apiTokenResponse = buildRequest(apiTokenRequest).execute(new ResponseHandlerJSONObject(false))
        .getAwsResponse();
    return (String) (apiTokenResponse.get("jwtToken"));
  }

  public static Response<JSONArray> getTestCustomers() {
    final Request customerRequest = createRequest(HttpMethodName.GET, "/test-customers/v0");

    return buildRequest(customerRequest).execute(new ResponseHandlerJSONArray(false));
  }

  public static Response<JSONObject> getCustomerInfo(final String jwtToken) {
    final Request customerRequest = createRequest(HttpMethodName.GET, "/customers/v0/current");
    customerRequest.addHeader(JWT_TOKEN_HEADER, jwtToken);

    return buildRequest(customerRequest).execute(new ResponseHandlerJSONObject(false));
  }

  public static Response<JSONArray> getCurrencyConversions(String quoteCurrency) {
    final Request customerRequest = createRequest(HttpMethodName.GET, "/currencies/v1/convert/" + quoteCurrency);

    return buildRequest(customerRequest).execute(new ResponseHandlerJSONArray(false));
  }

  public static Response<JSONObject> getCurrencyConversion(String quoteCurrency, String baseCurrency) {
    final Request customerRequest = createRequest(HttpMethodName.GET,
        "/currencies/v1/" + baseCurrency + "/convert/" + quoteCurrency);

    return buildRequest(customerRequest).execute(new ResponseHandlerJSONObject(false));
  }

  public static Response<JSONArray> getCardInfo(final String jwtToken) {
    final Request cardRequest = createRequest(HttpMethodName.GET, "/cards/v0");
    cardRequest.addHeader(JWT_TOKEN_HEADER, jwtToken);

    return buildRequest(cardRequest).execute(new ResponseHandlerJSONArray(false));
  }

  public static void main(final String[] args) {
    final Response<JSONArray> testCustomers = getTestCustomers();
    System.out.println("Test customers: " + testCustomers.getAwsResponse().toString(4));

    final String jwtToken = getApiToken();
    System.out.println("JWT token: " + jwtToken);

    final Response<JSONArray> currenciesResponse = getCurrencyConversions("NOK");
    System.out.println("Currencies: " + currenciesResponse.getAwsResponse().toString(4));

    final Response<JSONObject> currencyResponse = getCurrencyConversion("NOK", "EUR");
    System.out.println("Currency: " + currencyResponse.getAwsResponse().toString(4));

    final Response<JSONObject> customerResponse = getCustomerInfo(jwtToken);
    System.out.println("Customer info: " + customerResponse.getAwsResponse().toString(4));

    final Response<JSONArray> cardResponse = getCardInfo(jwtToken);
    System.out.println("Card info: " + cardResponse.getAwsResponse().toString(4));
  }
}
