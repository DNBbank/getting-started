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

  public static Response<JSONArray> getTestCustomers() {
    final Request currenciesRequest = createRequest(HttpMethodName.GET, "/test-customers/v0");

    return buildRequest(currenciesRequest).execute(new ResponseHandlerJSONArray(false));
  }

  public static Response<JSONArray> getCurrencyConversions(String quoteCurrency) {
    final Request currenciesRequest = createRequest(HttpMethodName.GET, "/currencies/v1/convert/" + quoteCurrency);

    return buildRequest(currenciesRequest).execute(new ResponseHandlerJSONArray(false));
  }

  public static Response<JSONObject> getCurrencyConversion(String quoteCurrency, String baseCurrency) {
    final Request currenciesRequest = createRequest(HttpMethodName.GET,
        "/currencies/v1/" + baseCurrency + "/convert/" + quoteCurrency);

    return buildRequest(currenciesRequest).execute(new ResponseHandlerJSONObject(false));
  }
}
