const {
  getAccessToken, getCustomerInfo, getAccounts, getCards,
} = require('../');

let accessToken;

beforeAll(async () => {
  // This is put here for performance reasons.
  // We don't need a separate token for each test
  accessToken = await getAccessToken('29105573083');
});

test('getAccessToken should retrieve token', async () => {
  expect(typeof accessToken).toEqual('string');
  expect(accessToken.length).toBeGreaterThan(500);
});

test('getCustomerInfo should retrieve customer info', async () => {
  const customerData = await getCustomerInfo(accessToken);

  expect(customerData.customerId).toEqual('29105573083');
}, 12000);

test('getAccounts should retrieve list of accounts', async () => {
  const accounts = await getAccounts(accessToken);

  expect(accounts).toMatchSnapshot();
}, 12000);

test('getCards should retrieve list of cards', async () => {
  const cards = await getCards(accessToken);

  expect(cards).toMatchSnapshot();
});
