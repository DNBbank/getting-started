import 'package:dnb/dnb.dart';
import 'package:test/test.dart';
import 'package:getting_started/credentials.dart';

// TODO Implement block/inblock testing once DNB has fixed their test custoemrs
// TODO Implement payment tests

void main() {
  final credentials = loadCredentials();

  final dnb = DNB(
    apiKey: credentials['API_KEY'],
    clientKey: credentials['CLIENT_ID'],
    clientSecret: credentials['CLIENT_SECRET'],
  );

  test('getToken() retrieves token', () async {
    var jwt = await dnb.getToken(customerId: '29105573083');
    assert(jwt != null);
    expect(jwt.runtimeType, String);
  });

  test('getCurrentCustomer() retrieves current customer', () async {
    final customer = await dnb.getCurrentCustomer();
    assert(customer != null);
    expect(customer['customerId'], '29105573083');
  });

  test('getAccounts() retrieves a list of accounts', () async {
    final accounts = await dnb.getAccounts();
    assert(accounts != null);
    expect(accounts[0]['accountNumber'], '12003189487');
  });

  test('getCards() retrieves a list of cards', () async {
    final cards = await dnb.getCards();
    assert(cards != null);
    expect(cards[0]['cardId'], 'TQJQ95214468J85O');
  });
}
