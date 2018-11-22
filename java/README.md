# Java example for DNB Open Banking APIs

## Usage

See [the main readme][] of the repo for a description on how to
retrieve client id, client secret and api key.

To configure the credentials use environment variables. You can
put them in a file called `.env` in this directory with the
following variables set.

```
CLIENT_ID=
CLIENT_SECRET=
API_KEY=
```

After adding the credentials you can run the example with

```
mvn exec:java -Dexec.mainClass="com.dnb.openbanking.gettingstarted.GettingStarted"
```


[the main readme]: ../README.md
