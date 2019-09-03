const crypto = require('crypto');

function sign(key, data, encoding) {
  return crypto
    .createHmac('sha256', key)
    .update(data, 'utf8')
    .digest(encoding);
}

exports.hash = function hash(string, encoding) {
  return crypto
    .createHash('sha256')
    .update(string, 'utf8')
    .digest(encoding);
};

function getSignatureKey(key, dateStamp, serviceName, regionName) {
  const date = sign(`AWS4${key}`, dateStamp);
  const region = sign(date, regionName);
  const service = sign(region, serviceName);
  return sign(service, 'aws4_request');
}

exports.sign = (options, clientId, clientSecret) => {
  const algorithm = 'AWS4-HMAC-SHA256';
  const amzDate = options.headers['x-amz-date'];
  const dateStamp = amzDate.substr(0, 8);
  const queryStringMatch = options.path.match(/(.*)\?/);
  let canonicalUri = options.path;
  if (queryStringMatch !== null) {
    const firstMatchIndex = 1;
    canonicalUri = queryStringMatch[firstMatchIndex];
  }
  const canonicalQuerystring = options.params;
  const canonicalHeaders = `host:${options.host}\nx-amz-date:${amzDate}\n`;
  const signedHeaders = 'host;x-amz-date';
  const payloadHash = exports.hash(options.data || '', 'hex');
  const canonicalRequest = `${options.method}\n${canonicalUri}\n${canonicalQuerystring}\n${canonicalHeaders}\n${signedHeaders}\n${payloadHash}`;
  const credentialScope = `${dateStamp}/${options.region}/${options.service}/aws4_request`;
  const stringToSign = `${algorithm}\n${amzDate}\n${credentialScope}\n${exports.hash(
    canonicalRequest,
    'hex',
  )}`;
  const signingKey = getSignatureKey(
    clientSecret,
    dateStamp,
    options.service,
    options.region,
  );
  const signature = sign(signingKey, stringToSign, 'hex');
  const credentialHeader = `Credential=${clientId}/${credentialScope}`;
  return `${algorithm} ${credentialHeader}, SignedHeaders=${signedHeaders}, Signature=${signature}`;
};
