const {
  getTestCustomers,
  getCurrencyConversion,
  getCurrencyConversions,
} = require('..');
const loadCredentials = require('../credentials');

let hasCredentials = false;

try {
  const credentials = loadCredentials();
  console.log(credentials);
  hasCredentials = credentials.apiKey !== undefined;
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

testRequiringCredentials(
  'getTestCustomers should retrieve a list of test customers',
  async () => {
    expect(await getTestCustomers()).toMatchSnapshot();
  },
);

testRequiringCredentials(
  'getCurrencyConversions should currency info',
  async () => {
    const currencies = await getCurrencyConversions('NOK');

    expect(currencies).toMatchSnapshot();
  },
);

testRequiringCredentials(
  'getCurrencyConversion should currency info',
  async () => {
    const currencies = await getCurrencyConversion('EUR', 'NOK');

    expect(currencies).toMatchSnapshot();
  },
);
