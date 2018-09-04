package com.dnb.openbanking.gettingstarted;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;

public class ErrorResponseHandler implements HttpResponseHandler<AmazonServiceException> {

  private boolean needsConnectionLeftOpen;

  ErrorResponseHandler(final boolean needsConnectionLeftOpen) {
    this.needsConnectionLeftOpen = needsConnectionLeftOpen;
  }

  @Override
  public AmazonServiceException handle(final HttpResponse response) {
    final AmazonServiceException ase = new AmazonServiceException(response.getStatusText());
    ase.setStatusCode(response.getStatusCode());
    return ase;
  }

  @Override
  public boolean needsConnectionLeftOpen() {
    return this.needsConnectionLeftOpen;
  }
}
