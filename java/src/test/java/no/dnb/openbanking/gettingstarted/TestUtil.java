package no.dnb.openbanking.gettingstarted;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class TestUtil {
  private TestUtil() {
  }

  public static JSONObject parseJSONFileFromResourceToJSONObject(String resourceFilePath) {
    InputStream is = readValueFromResourceToInputStream(resourceFilePath);
    return parseInputSteamToJSONObject(is);
  }

  public static JSONArray parseJSONFileFromResourceToJSONArray(String resourceFilePath) {
    InputStream is = readValueFromResourceToInputStream(resourceFilePath);
    return parseInputSteamToJSONArray(is);
  }

  private static JSONObject parseInputSteamToJSONObject(InputStream is) {
    JSONTokener tokener = new JSONTokener(is);
    return new JSONObject(tokener);
  }

  private static JSONArray parseInputSteamToJSONArray(InputStream is) {
    JSONTokener tokener = new JSONTokener(is);
    return new JSONArray(tokener);
  }

  private static InputStream readValueFromResourceToInputStream(String resourceFilePath) {
    return TestUtil.class.getClassLoader().getResourceAsStream(resourceFilePath);
  }
}
