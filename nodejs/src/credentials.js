module.exports = function loadCredentials() {
  if (
    typeof process.env.API_KEY === 'undefined' &&
    process.env.NODE_ENV !== 'test'
  ) {
    console.error('Environment variable API_KEY must be set!');
    process.exit(1);
  }

  return {
    apiKey: process.env.API_KEY,
  };
};
