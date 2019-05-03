import 'package:dnb/dnb.dart';
import 'package:getting_started/credentials.dart';

void main() async {
  // Load credentials
  final credentials = loadCredentials();

  // Initialise client
  final client = DNB(
    clientKey: credentials['CLIENT_ID'],
    clientSecret: credentials['CLIENT_SECRET'],
    apiKey: credentials['API_KEY']
  );

  final dashes = List.filled(40, "-").join();

  // Get token
  final token = await client.getToken(customerId: '29105573083');
  print('$dashes Token $dashes\n $token');

  // Get accounts
  final accounts = await client.getAccounts();
  print('$dashes Accounts $dashes \n $accounts');

  // Get cards
  final cards = await client.getCards();
  print('$dashes Cards $dashes \n $cards');

  // Get all currency rates from NOK
  final currencies = await client.getCurrencyRateList(fromCurrency: 'NOK');
  print('$dashes NOK conversions $dashes \n $currencies');

  // Get currency rate from NOK to EUR
  final currency = await client.getCurrencyRate(fromCurrency: 'NOK', toCurrency: 'EUR');
  print('$dashes NOK -> EUR $dashes \n $currency');
}