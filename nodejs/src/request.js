const https = require('https');

module.exports = function request(params) {
  return new Promise((resolve, reject) => {
    const request_ = https.request(params, (response) => {
      if (response.statusCode < 200 || response.statusCode >= 300) {
        request_.end();
        return reject(new Error(`statusCode=${response.statusCode}`));
      }
      // cumulate data
      let body = [];
      response.on('data', (chunk) => {
        body.push(chunk);
      });
      // resolve on end
      response.on('end', () => {
        try {
          body = JSON.parse(Buffer.concat(body).toString());
        } catch (error) {
          reject(error);
        }
        resolve(body);
      });
      return response;
    });
    request_.on('error', (error) => {
      reject(error);
    });
    if (params.data) {
      request_.write(params.data);
    }
    request_.end();
  });
};
