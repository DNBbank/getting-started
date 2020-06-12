// Load config from .env file in this directory
const dotenv = require('dotenv');
const { join } = require('path');

const loadCredentials = require('./credentials');
const request = require('./request');

dotenv.config({ path: join(__dirname, '..', '.env') });
dotenv.config({ path: join(__dirname, '..', '..', '.env') });

const { apiKey } = loadCredentials();

const openbankingEndpoint = 'developer-api-testmode.dnb.no';

function createAmzDate() {
  return new Date().toISOString().replace(/[:-]|\.\d{3}/g, '');
}

function createRequest({ path, method = 'GET', data, queryString = '' }) {
  const options = {
    host: openbankingEndpoint,
    headers: {
      Host: openbankingEndpoint,
      Accept: 'application/json',
      'Content-type': 'application/json',
      'x-api-key': apiKey,
      'x-amz-date': createAmzDate(),
    },
    path,
    method,
    params: queryString,
  };
  if (queryString !== '') {
    options.path += `?${queryString}`;
  }
  if (data) {
    options.data = JSON.stringify(data);
  }
  return options;
}

async function getTestCustomers() {
  return request(
    createRequest({
      path: '/test-customers/v0',
      method: 'GET',
    }),
  );
}

async function getCurrencyConversions(quoteCurrency) {
  return request(
    createRequest({
      path: `/currencies/v1/convert/${quoteCurrency}`,
    }),
  );
}

async function getCurrencyConversion(baseCurrency, quoteCurrency) {
  return request(
    createRequest({
      path: `/currencies/v1/${baseCurrency}/convert/${quoteCurrency}`,
    }),
  );
}

async function main() {
  const dashes = '-------------------------------';

  const testCustomers = await getTestCustomers();
  console.log(`${dashes} Test Customers ${dashes}`);
  console.log(JSON.stringify(testCustomers, null, 2));
  console.log('\n');

  const currencies = await getCurrencyConversions('NOK');
  console.log(`${dashes} NOK conversions ${dashes}`);
  console.log(JSON.stringify(currencies, null, 2));
  console.log('\n');

  const currency = await getCurrencyConversion('EUR', 'NOK');
  console.log(`${dashes} EUR -> NOK ${dashes}`);
  console.log(JSON.stringify(currency, null, 2));
  console.log('\n');
}

module.exports = {
  getTestCustomers,
  getCurrencyConversions,
  getCurrencyConversion,
  main,
};

if (require.main === module) {
  main().catch((error) => {
    console.error(error);
    process.exit(1);
  });
}
