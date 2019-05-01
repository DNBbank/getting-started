import 'package:dotenv/dotenv.dart';
import 'dart:io';

Map<String, String> loadCredentials() {
  if (File('../.env').existsSync()) {
    load('../.env');
  }
  assert(isEveryDefined(['CLIENT_ID', 'CLIENT_SECRET', 'API_KEY']));
  return env;
}
