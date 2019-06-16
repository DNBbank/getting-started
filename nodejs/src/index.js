// Load config from .env file in this directory
const dotenv = require('dotenv');
const { join } = require('path');

const asv4 = require('./asv4');
const loadCredentials = require('./credentials');
const request = require('./request');

dotenv.config({ path: join(__dirname, '..', '.env') });
dotenv.config({ path: join(__dirname, '..', '..', '.env') });

const { clientId, clientSecret, apiKey } = loadCredentials();

// AWS signing v4 constants
const awsRegion = 'eu-west-1';
const awsService = 'execute-api';

const openbankingEndpoint = 'developer-api-testmode.dnb.no';

function createAmzDate() {
  return new Date().toISOString().replace(/[:-]|\.\d{3}/g, '');
}

function createRequest({
  path,
  method = 'GET',
  data,
  queryString = '',
  jwtToken = '',
}) {
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
    service: awsService,
    region: awsRegion,
  };
  if (queryString !== '') {
    options.path += `?${queryString}`;
  }
  if (jwtToken !== '') {
    options.headers['x-dnbapi-jwt'] = jwtToken;
  }
  if (data) {
    options.data = JSON.stringify(data);
    options.headers['x-amz-content-sha256'] = asv4.hash(options.data, 'hex');
  }
  if (path.includes('token')) {
    options.headers.Authorization = asv4.sign(options, clientId, clientSecret);
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

async function getAccessToken(ssn) {
  const data = await request(
    createRequest({
      path: '/tokens/v0',
      method: 'POST',
      data: { ssn },
    }),
  );
  return data.jwtToken;
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

async function getCustomerInfo(jwtToken) {
  return request(createRequest({ path: '/customers/v0/current', jwtToken }));
}

async function getCards(jwtToken) {
  const data = await request(createRequest({ path: '/cards/v0', jwtToken }));
  return data;
}

async function main() {
  const dashes = '-------------------------------';
  const accessToken = await getAccessToken('29105573083');

  const testCustomers = await getTestCustomers();
  console.log(`${dashes} Test Customers ${dashes}`);
  console.log(JSON.stringify(testCustomers, null, 2));
  console.log('\n');

  const customerInfo = await getCustomerInfo(accessToken);
  console.log(`${dashes} Customer Info ${dashes}`);
  console.log(JSON.stringify(customerInfo, null, 2));
  console.log('\n');

  const cards = await getCards(accessToken);
  console.log(`${dashes} Cards ${dashes}`);
  console.log(JSON.stringify(cards, null, 2));
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
  getAccessToken,
  getCurrencyConversions,
  getCurrencyConversion,
  getCustomerInfo,
  getCards,
  main,
};

if (require.main === module) {
  main().catch((error) => {
    console.error(error);
    process.exit(1);
  });
}
