package com.dnb.openbanking.gettingstarted;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.util.IOUtils;
import java.io.IOException;

public class ErrorResponseHandler implements HttpResponseHandler<AmazonServiceException> {

  private boolean needsConnectionLeftOpen;

  ErrorResponseHandler(final boolean needsConnectionLeftOpen) {
    this.needsConnectionLeftOpen = needsConnectionLeftOpen;
  }

  @Override
  public AmazonServiceException handle(final HttpResponse response) {
    final AmazonServiceException ase = new AmazonServiceException(response.getStatusText());
    ase.setStatusCode(response.getStatusCode());
    try {
      final String content = IOUtils.toString(response.getContent()).trim();
      ase.setRawResponseContent(content);
    } catch (IOException exception) {
      System.err.println("Exception thrown while reading the response's content: " + exception);
    }

    return ase;
  }

  @Override
  public boolean needsConnectionLeftOpen() {
    return this.needsConnectionLeftOpen;
  }
}
