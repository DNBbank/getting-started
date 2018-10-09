const https = require('https');

module.exports = function request(params) {
  return new Promise((resolve, reject) => {
    const req = https.request(params, (res) => {
      if (res.statusCode < 200 || res.statusCode >= 300) {
        req.end();
        return reject(new Error(`statusCode=${res.statusCode}`));
      }
      // cumulate data
      let body = [];
      res.on('data', (chunk) => { body.push(chunk); });
      // resolve on end
      res.on('end', () => {
        try {
          body = JSON.parse(Buffer.concat(body).toString());
        } catch (e) {
          reject(e);
        }
        resolve(body);
      });
      return res;
    });
    req.on('error', (err) => { reject(err); });
    req.end();
  });
};
