const { getAccessToken, getCustomerInfo, getCards } = require('..');
const loadCredentials = require('../credentials');

let accessToken;
let hasCredentials = false;

try {
  const credentials = loadCredentials();
  console.log(credentials);
  hasCredentials =
    credentials.clientId && credentials.clientSecret && credentials.apiKey;
} catch (error) {
  console.log(error);
}

function testRequiringCredentials(name, fn, timeout) {
  if (hasCredentials) {
    test(name, fn, timeout);
  } else {
    test.skip(name, () => Promise.reject(new Error('Missing API credentials')));
  }
}

beforeAll(async () => {
  if (hasCredentials) {
    // This is put here for performance reasons.
    // We don't need a separate token for each test
    accessToken = await getAccessToken('29105573083');
  }
});

testRequiringCredentials('getAccessToken should retrieve token', async () => {
  expect(typeof accessToken).toEqual('string');
  expect(accessToken.length).toBeGreaterThan(500);
});

testRequiringCredentials(
  'getCustomerInfo should retrieve customer info',
  async () => {
    const customerData = await getCustomerInfo(accessToken);

    expect(customerData.customerId).toEqual('29105573083');
  },
  12000,
);

testRequiringCredentials('getCards should retrieve list of cards', async () => {
  const cards = await getCards(accessToken);

  expect(cards).toMatchSnapshot();
});
