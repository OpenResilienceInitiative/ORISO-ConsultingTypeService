package de.caritas.cob.consultingtypeservice.api.exception.httpresponses;

import de.caritas.cob.consultingtypeservice.api.service.LogService;

public class ConflictException extends CustomHttpStatusException {

  private static final long serialVersionUID = 4263187240217465401L;

  /**
   * Conflict exception.
   *
   * @param message an additional message
   */
  public ConflictException(String message) {
    super(message, LogService::logWarning);
  }
}
