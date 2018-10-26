// Load config from .env file in this directory
const dotenv = require('dotenv');
const { join } = require('path');

const querystring = require('querystring');

const asv4 = require('./asv4');
const loadCredentials = require('./credentials');
const request = require('./request');

dotenv.config({ path: join(__dirname, '..', '.env') });
dotenv.config({ path: join(__dirname, '..', '..', '.env') });

const { clientId, clientSecret, apiKey } = loadCredentials();

// AWS signing v4 constants
const awsRegion = 'eu-west-1';
const awsService = 'execute-api';

const openbankingEndpoint = 'developer-api-sandbox.dnb.no';

function createAmzDate() {
  return new Date().toISOString().replace(/[:-]|\.\d{3}/g, '');
}

function createRequest(path, queryString = '', jwtToken = '') {
  const opts = {
    host: openbankingEndpoint,
    headers: {
      Host: openbankingEndpoint,
      Accept: 'application/json',
      'Content-type': 'application/json',
      'x-api-key': apiKey,
      'x-amz-date': createAmzDate(),
    },
    path,
    params: queryString,
    service: awsService,
    region: awsRegion,
  };
  if (queryString !== '') {
    opts.path += `?${queryString}`;
  }
  if (jwtToken !== '') {
    opts.headers['x-dnbapi-jwt'] = jwtToken;
  }
  opts.headers.Authorization = asv4.sign(opts, clientId, clientSecret);
  return opts;
}

async function getAccessToken(ssn) {
  const apiTokenParams = {
    customerId: JSON.stringify({ type: 'SSN', value: ssn }),
  };
  const data = await request(createRequest('/token', querystring.stringify(apiTokenParams)));
  return data.tokenInfo[0].jwtToken;
}

async function getCustomerInfo(jwtToken) {
  return request(createRequest('/customers/current', '', jwtToken));
}

async function getAccounts(jwtToken) {
  const { accounts } = await request(createRequest('/accounts', '', jwtToken));
  return accounts;
}

async function getCards(jwtToken) {
  const data = await request(createRequest('/cards', '', jwtToken));
  return data;
}

async function main() {
  const dashes = '-------------------------------';
  const accessToken = await getAccessToken('29105573083');

  const customerInfo = await getCustomerInfo(accessToken);
  console.log(`${dashes} Customer Info ${dashes}`);
  console.log(JSON.stringify(customerInfo, null, 2));
  console.log('\n');

  const accounts = await getAccounts(accessToken);
  console.log(`${dashes} Accounts ${dashes}`);
  console.log(JSON.stringify(accounts, null, 2));
  console.log('\n');

  const cards = await getCards(accessToken);
  console.log(`${dashes} Cards ${dashes}`);
  console.log(JSON.stringify(cards, null, 2));
  console.log('\n');
}

module.exports = {
  getAccessToken,
  getCustomerInfo,
  getAccounts,
  getCards,
  main,
};

if (require.main === module) {
  main().catch((error) => {
    console.error(error);
    process.exit(1);
  });
}
