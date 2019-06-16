# DNB Open Banking Postman collection

Important: Our collection defines an Authorization (AWS Signing) step on the collection-level.
Please make sure your Postman is up to date (at least >= 6.6.1) to support this feature, otherwise the colection will not be setup properly when you import it.

Import this collection in postman and define the following global variable keys: `SecretKey` (Client Secret), `AccessKey` (Client ID) and `ApiKey`.

First, run the `API: Get API token` request to get the JWT token, which will then be automatically set in your global variables. This is required to run all the other requests.
