import 'package:dotenv/dotenv.dart';

Map<String, String> loadCredentials() {
  load('.env');
  assert(isEveryDefined(['CLIENT_ID', 'CLIENT_SECRET', 'API_KEY']));
  return env;
}