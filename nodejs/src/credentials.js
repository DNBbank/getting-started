const checkEnv = require('check-env');

module.exports = function loadCredentials() {
  try {
    checkEnv(['API_KEY']);
  } catch (error) {
    console.error(error.message);
    if (process.env.NODE_ENV !== 'test') {
      process.exit(1);
    }
  }

  return {
    apiKey: process.env.API_KEY,
  };
};
