# DNB Open Banking Postman collection

Important: Our collection defines an Authorization (AWS Signing) step on the collection-level.
Please make sure your Postman is up to date (at least >= 6.6.1) to support this feature, otherwise the colection will not be setup properly when you import it.

Import this collection in postman and define the following global variable keys: `ApiKey`.

First, run the `API: Get API token` request to get the JWT token, which will then be automatically set in your global variables. This is required to run all the other requests.

# DNB PSD2 Postman collection

This postman collection can be used to make requests to our PSD2 APIs in both sandbox and production environments. Note that you will need a client certificate to gain access. For more information about the onboarding process, please visit: https://developer.dnb.no/getting-started/psd2

Information about how to use the postman collection with our APIs can be found at: https://developer.dnb.no/documentation/psd2. This documentation includes guides on how to setup postman with your certificate, some examples to get you started, along with information about how the APIs can be used.    
