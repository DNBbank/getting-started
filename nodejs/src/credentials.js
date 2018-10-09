const checkEnv = require('check-env');

module.exports = function loadCredentials() {
  try {
    checkEnv(['CLIENT_ID', 'CLIENT_SECRET', 'API_KEY']);
  } catch (error) {
    console.error(error.message);
    if (process.env.NODE_ENV !== 'test') {
      process.exit(1);
    }
  }

  return {
    clientId: process.env.CLIENT_ID,
    clientSecret: process.env.CLIENT_SECRET,
    apiKey: process.env.API_KEY,
  };
};
