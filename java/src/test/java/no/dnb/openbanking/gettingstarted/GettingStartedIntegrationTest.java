package no.dnb.openbanking.gettingstarted;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Set;

public class GettingStartedIntegrationTest {

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
  void testGetCurrencyConversions() {
    Response<JSONArray> actualCardDetailsResponse = GettingStarted.getCurrencyConversions("NOK");

    assertThat(actualCardDetailsResponse.getHttpResponse().getStatusCode())
        .as("Test if status code is 200/OK").isEqualTo(200);

    JSONArray actualCardDetailsJSONResponse = actualCardDetailsResponse.getAwsResponse();

    assertThat(actualCardDetailsJSONResponse.length())
        .as("Expecting 46 different currencies")
        .isEqualTo(46);

    JSONObject jsonObject;
    Set<String> keySet;
    for (int i = 0 ; i < actualCardDetailsJSONResponse.length() ; i++) {
      jsonObject = actualCardDetailsJSONResponse.getJSONObject(i);
      keySet = jsonObject.keySet();
      assertThat(keySet.size()).as("Check that object contains correct amount of parameters").isEqualTo(12);
      assertThat(keySet.contains("quoteCurrency")).as("That object contains quoteCurrency").isTrue();
      assertThat(keySet.contains("country")).as("That object contains country").isTrue();
      assertThat(keySet.contains("amount")).as("That object contains amount").isTrue();
      assertThat(keySet.contains("buyRateTransfer")).as("That object contains buyRateTransfer").isTrue();
      assertThat(keySet.contains("midRate")).as("That object contains midRate").isTrue();
      assertThat(keySet.contains("sellRateTransfer")).as("That object contains sellRateTransfer").isTrue();
      assertThat(keySet.contains("sellRateCash")).as("That object contains sellRateCash").isTrue();
      assertThat(keySet.contains("changeInMidRate")).as("That object contains changeInMidRate").isTrue();
      assertThat(keySet.contains("buyRateCash")).as("That object contains buyRateCash").isTrue();
      assertThat(keySet.contains("updatedDate")).as("That object contains updatedDate").isTrue();
      assertThat(keySet.contains("previousMidRate")).as("That object contains previousMidRate").isTrue();
      assertThat(keySet.contains("baseCurrency")).as("That object contains baseCurrency").isTrue();
    }
  }

  @Test
  void testGetCurrencyConversion() {
    Response<JSONObject> response = GettingStarted.getCurrencyConversion("NOK", "EUR");

    assertThat(response.getHttpResponse().getStatusCode())
        .as("Test if status code is 200/OK").isEqualTo(200);

    JSONObject json = response.getAwsResponse();

    assertThat(json.length()).as("Check that object contains correct amount of parameters").isEqualTo(12);

    Set<String> keySet;
    keySet = json.keySet();
    assertThat(keySet.contains("quoteCurrency")).as("That object contains quoteCurrency").isTrue();
    assertThat(keySet.contains("country")).as("That object contains country").isTrue();
    assertThat(keySet.contains("amount")).as("That object contains amount").isTrue();
    assertThat(keySet.contains("buyRateTransfer")).as("That object contains buyRateTransfer").isTrue();
    assertThat(keySet.contains("midRate")).as("That object contains midRate").isTrue();
    assertThat(keySet.contains("sellRateTransfer")).as("That object contains sellRateTransfer").isTrue();
    assertThat(keySet.contains("sellRateCash")).as("That object contains sellRateCash").isTrue();
    assertThat(keySet.contains("changeInMidRate")).as("That object contains changeInMidRate").isTrue();
    assertThat(keySet.contains("buyRateCash")).as("That object contains buyRateCash").isTrue();
    assertThat(keySet.contains("updatedDate")).as("That object contains updatedDate").isTrue();
    assertThat(keySet.contains("previousMidRate")).as("That object contains previousMidRate").isTrue();
    assertThat(keySet.contains("baseCurrency")).as("That object contains baseCurrency").isTrue();
  }
}
