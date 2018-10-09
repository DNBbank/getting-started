module.exports = {
    "extends": "airbnb-base",
    "rules": {
        "no-console": 0
    },
    "overrides": [
        {
            "files": "*.tests.js",
            "env": {
                "jest": true
            }
        }
    ]
};
