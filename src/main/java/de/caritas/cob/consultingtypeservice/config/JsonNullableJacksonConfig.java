package de.caritas.cob.consultingtypeservice.config;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

/**
 * The generated DTOs use JsonNullable, whose module only supports Jackson 2. Without this, the
 * Jackson 3 mapper writes {"present":true} instead of the value.
 */
@Configuration
public class JsonNullableJacksonConfig {

  @Bean
  @SuppressWarnings({"rawtypes", "unchecked"})
  public JacksonModule jsonNullableModule() {
    return new SimpleModule("json-nullable")
        .addSerializer((Class) JsonNullable.class, new JsonNullableValueWriter());
  }

  static class JsonNullableValueWriter extends ValueSerializer<JsonNullable<?>> {
    @Override
    public void serialize(
        JsonNullable<?> value, JsonGenerator generator, SerializationContext context) {
      if (value.isPresent() && value.get() != null) {
        context.writeValue(generator, value.get());
      } else {
        generator.writeNull();
      }
    }
  }
}
