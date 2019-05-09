package no.dnb.openbanking.gettingstarted;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class GettingStartedIntegrationTest {

  private static final String AWS_REGION = "eu-west-1";
  private static final String AWS_SERVICE = "execute-api";
  private static final AWSCredentials awsCredentials = new BasicAWSCredentials(Config.get("CLIENT_ID"), Config.get("CLIENT_SECRET"));
  private static final AWS4Signer signer = new AWS4Signer();
  private static String jwtToken;

  @BeforeAll
  static void initAll() {
    signer.setRegionName(AWS_REGION);
    signer.setServiceName(AWS_SERVICE);
    jwtToken = GettingStarted.getApiToken(signer, awsCredentials);
  }

  @Test
  void testGetApiToken() {
    assertTrue(jwtToken.length() > 500);
  }

  @Test
  void testGetTestCustomersInfoAPI() {
    JSONArray expectedTestCustomersResponse = TestUtil.parseJSONFileFromResourceToJSONArray(
        "GetTestCustomers.json");
    Response<JSONArray> actualTestCustomersResponse = GettingStarted.getTestCustomers();

    assertThat(actualTestCustomersResponse.getHttpResponse().getStatusCode())
        .as("Test if status code is 200/OK").isEqualTo(200);

    JSONArray actualTestCustomersJSONResponse = actualTestCustomersResponse.getAwsResponse();

    assertThat(actualTestCustomersJSONResponse.length())
        .as("Check if objects have same amount of fields")
        .isEqualTo(expectedTestCustomersResponse.length());
    JSONAssert.assertEquals(expectedTestCustomersResponse, actualTestCustomersJSONResponse , false);
  }

  @Test
  void testGetCustomerInfoAPI() {
    JSONObject expectedCustomerDetailsResponse = TestUtil.parseJSONFileFromResourceToJSONObject(
            "GetCustomerDetails.json");
    Response<JSONObject> actualCustomerDetailsResponse = GettingStarted.getCustomerInfo(jwtToken);

    assertThat(actualCustomerDetailsResponse.getHttpResponse().getStatusCode())
            .as("Test if status code is 200/OK").isEqualTo(200);

    JSONObject actualCustomerDetailsJSONResponse = actualCustomerDetailsResponse.getAwsResponse();

    assertThat(actualCustomerDetailsJSONResponse.length())
            .as("Check if objects have same amount of fields")
            .isEqualTo(expectedCustomerDetailsResponse.length());
    JSONAssert.assertEquals(expectedCustomerDetailsResponse, actualCustomerDetailsJSONResponse, false);
  }

  @Test
  void testGetCardInfoAPI() {
    JSONArray expectedCardDetailsResponse = TestUtil.parseJSONFileFromResourceToJSONArray(
            "GetCardDetails.json");
    Response<JSONArray> actualCardDetailsResponse = GettingStarted.getCardInfo(jwtToken);

    assertThat(actualCardDetailsResponse.getHttpResponse().getStatusCode())
            .as("Test if status code is 200/OK").isEqualTo(200);

    JSONArray actualCardDetailsJSONResponse = actualCardDetailsResponse.getAwsResponse();

    assertThat(actualCardDetailsJSONResponse.length())
            .as("Check if objects have same amount of fields")
            .isEqualTo(expectedCardDetailsResponse.length());
    JSONAssert.assertEquals(expectedCardDetailsResponse, actualCardDetailsJSONResponse, false);
  }

  @Test
  void testGetCurrencyConversions() {
    JSONArray expectedCardDetailsResponse = TestUtil.parseJSONFileFromResourceToJSONArray(
        "GetCurrencyConversions.json");
    Response<JSONArray> actualCardDetailsResponse = GettingStarted.getCurrencyConversions("NOK");

    assertThat(actualCardDetailsResponse.getHttpResponse().getStatusCode())
        .as("Test if status code is 200/OK").isEqualTo(200);

    JSONArray actualCardDetailsJSONResponse = actualCardDetailsResponse.getAwsResponse();

    assertThat(actualCardDetailsJSONResponse.length())
        .as("Check if objects have same amount of fields")
        .isEqualTo(expectedCardDetailsResponse.length());
    JSONAssert.assertEquals(expectedCardDetailsResponse, actualCardDetailsJSONResponse, false);
  }

  @Test
  void testGetCurrencyConversion() {
    JSONObject expectedCustomerDetailsResponse = TestUtil.parseJSONFileFromResourceToJSONObject(
        "GetCurrencyConversion.json");
    Response<JSONObject> response = GettingStarted.getCurrencyConversion("NOK", "EUR");

    assertThat(response.getHttpResponse().getStatusCode())
        .as("Test if status code is 200/OK").isEqualTo(200);

    JSONObject json = response.getAwsResponse();

    assertThat(json.length())
        .as("Check if objects have same amount of fields")
        .isEqualTo(expectedCustomerDetailsResponse.length());
    JSONAssert.assertEquals(expectedCustomerDetailsResponse, json, false);
  }
}