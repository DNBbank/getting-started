package com.dnb.openbanking.gettingstarted;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.util.IOUtils;
import org.json.JSONArray;

class ResponseHandlerJSONArray implements HttpResponseHandler<JSONArray> {

  private boolean needsConnectionLeftOpen;

  ResponseHandlerJSONArray(final boolean needsConnectionLeftOpen) {
    this.needsConnectionLeftOpen = needsConnectionLeftOpen;
  }

  @Override
  public JSONArray handle(final HttpResponse response) throws Exception {
    return new JSONArray(IOUtils.toString(response.getContent()));
  }

  @Override
  public boolean needsConnectionLeftOpen() {
    return needsConnectionLeftOpen;
  }
}
