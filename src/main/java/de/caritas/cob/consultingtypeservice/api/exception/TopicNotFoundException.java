package de.caritas.cob.consultingtypeservice.api.exception;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.NotFoundException;

/**
 * 404: no topic with this id in the caller's reach. A topic of another tenant is indistinguishable
 * from an unknown one. Rendered by the {@link NotFoundException} handler, never by the generic 500
 * fallback.
 */
public class TopicNotFoundException extends NotFoundException {

  public TopicNotFoundException(String exception) {
    super(exception);
  }
}
