package de.caritas.cob.consultingtypeservice.api.exception.httpresponses;

import de.caritas.cob.consultingtypeservice.api.service.LogService;

/** 503 - a dependency this request needs could not be asked, so the request is not decided. */
public class ServiceUnavailableException extends CustomHttpStatusException {

  private static final long serialVersionUID = 6840273316842711529L;

  /**
   * Service unavailable exception.
   *
   * @param message an additional message, never a credential
   * @param cause the failure of the dependency
   */
  public ServiceUnavailableException(String message, Exception cause) {
    super(message, cause, LogService::logWarning);
  }
}
