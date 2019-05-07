package no.dnb.openbanking.gettingstarted;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class Config {
  private final static HashMap<String, String> config = new HashMap<>();

  static {
    config.putAll(loadEnvFile(".env"));
    config.putAll(loadEnvFile("../.env"));
  }

  static String get(String key) {
    return config.get(key);
  }

  private static Map<String, String> loadEnvFile(String path) {
    Map<String, String> variables = new HashMap<>();
    FileInputStream input = null;
    try {
      input = new FileInputStream(path);
      Properties properties = new Properties();
      properties.load(input);
      for (final String name : properties.stringPropertyNames()) {
        variables.put(name, properties.getProperty(name));
      }
    } catch (IOException ignored) {
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException ignored) {
        }
      }
    }
    return variables;
  }
}
