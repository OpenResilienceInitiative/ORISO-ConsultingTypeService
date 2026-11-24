package de.caritas.cob.consultingtypeservice.api.exception.httpresponses;

/**
 * 409: a topic name is taken in the topic's tenant. Unlike other conflicts its message is meant for
 * the admin and is sent to the client (see ApiResponseEntityExceptionHandler).
 */
public class TopicNameConflictException extends ConflictException {

  public static final String REASON = "TOPIC_NAME_TAKEN";

  public TopicNameConflictException(String germanName) {
    super("A topic named \"" + germanName + "\" already exists");
  }
}
