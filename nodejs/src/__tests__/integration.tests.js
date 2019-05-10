const {
  getTestCustomers,
  getAccessToken,
  getCustomerInfo,
  getCards,
  getCurrencyConversion,
  getCurrencyConversions,
} = require('..');
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
  'getTestCustomers should retrieve a list of test customers',
  async () => {
    expect(await getTestCustomers()).toMatchSnapshot();
  },
);

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

testRequiringCredentials(
  'getCurrencyConversions should currency info',
  async () => {
    const cards = await getCurrencyConversions('NOK');

    expect(cards).toMatchSnapshot();
  },
);

testRequiringCredentials(
  'getCurrencyConversion should currency info',
  async () => {
    const cards = await getCurrencyConversion('EUR', 'NOK');

    expect(cards).toMatchSnapshot();
  },
);
