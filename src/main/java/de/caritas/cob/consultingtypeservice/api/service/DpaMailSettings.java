package de.caritas.cob.consultingtypeservice.api.service;

import lombok.Value;

@Value
public class DpaMailSettings {
  String host;
  Integer port;
  boolean secure;
  String username;
  String password;
  String from;
}
