const { sign } = require('../asv4');

test('asv4.sign should sign based on request options', () => {
  const signature = sign(
    {
      host: 'openbankingEndpoint',
      headers: {
        Host: 'openbankingEndpoint',
        Accept: 'application/json',
        'Content-type': 'application/json',
        'x-api-key': 'apiKey',
        'x-amz-date': '20181009T105508Z',
      },
      path: 'accounts',
      params: '',
      method: 'GET',
      service: 'execute-api',
      region: 'eu-west-1',
    },
    'clientId',
    'clientSecret',
  );

  expect(signature).toEqual(
    'AWS4-HMAC-SHA256 Credential=clientId/20181009/eu-west-1/execute-api/aws4_request, SignedHeaders=host;x-amz-date, Signature=c3b9d641da4946041d56e584d4da13a92746e5e5b40dcb12e20bbcde2a5c1ecd',
  );
});

test('asv4.sign should sign based on request options for post with body', () => {
  const signature = sign(
    {
      host: 'openbankingEndpoint',
      headers: {
        Host: 'openbankingEndpoint',
        Accept: 'application/json',
        'Content-type': 'application/json',
        'x-api-key': 'apiKey',
        'x-amz-date': '20181009T105508Z',
      },
      path: 'accounts',
      params: '',
      method: 'POST',
      data: JSON.stringify({ somedata: 1 }),
      service: 'execute-api',
      region: 'eu-west-1',
    },
    'clientId',
    'clientSecret',
  );

  expect(signature).toEqual(
    'AWS4-HMAC-SHA256 Credential=clientId/20181009/eu-west-1/execute-api/aws4_request, SignedHeaders=host;x-amz-date, Signature=68a4604f296eced8522561f20cb7ce6e807d1beebfff4960f7e3ba3a27c9d0fa',
  );
});
