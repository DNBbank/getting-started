package com.dnb.openbanking.gettingstarted;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.util.IOUtils;
import org.json.JSONObject;

class ResponseHandlerJSONObject implements HttpResponseHandler<JSONObject> {

  private boolean needsConnectionLeftOpen;

  ResponseHandlerJSONObject(final boolean needsConnectionLeftOpen) {
    this.needsConnectionLeftOpen = needsConnectionLeftOpen;
  }

  @Override
  public JSONObject handle(final HttpResponse response) throws Exception {
    return new JSONObject(IOUtils.toString(response.getContent()));
  }

  @Override
  public boolean needsConnectionLeftOpen() {
    return needsConnectionLeftOpen;
  }
}
