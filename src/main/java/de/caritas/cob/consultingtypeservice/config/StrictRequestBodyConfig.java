package de.caritas.cob.consultingtypeservice.config;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import java.util.Collection;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * Makes JSON binding fail closed for the request bodies listed in {@link #FAIL_ON_UNKNOWN_FIELDS},
 * and only for those.
 *
 * <p>The obvious way to get this behaviour is {@code
 * spring.jackson.deserialization.fail-on-unknown-properties=true}. That property is global, and
 * global is the wrong scope in two directions.
 *
 * <p>Inbound, it applies to every {@code @RequestBody} in the service, so unrelated endpoints start
 * rejecting bodies they have always accepted.
 *
 * <p>Outbound, it applies to responses this service <em>reads</em>. Spring Boot hands the same
 * mapper to the auto-configured {@link org.springframework.web.client.RestTemplate}, which {@code
 * TenantServiceApiControllerFactory} passes to the generated tenant client. Another service adding
 * a field to a response — additive, backward compatible, invisible to {@code oasdiff} — would then
 * become a runtime failure here. That is not hypothetical: TenantService's published contract
 * already returns {@code theming.accent}, {@code theming.signal} and {@code theming.loginEffect},
 * none of which our vendored copy under {@code services/tenantservice.yaml} declares.
 *
 * <p>Jackson offers no per-type way to <em>tighten</em> binding. {@code JsonIgnoreProperties} with
 * {@code ignoreUnknown = true} can only relax, and {@code ignoreUnknown = false} is merely the
 * default — it does not override a disabled {@code FAIL_ON_UNKNOWN_PROPERTIES}. A {@link
 * DeserializationProblemHandler} is the one hook Jackson consults <em>before</em> that global
 * feature flag, which is what lets strictness be scoped to a type instead of to the mapper.
 *
 * <p>The default therefore stays tolerant — Jackson 3 deliberately made it so — and the types below
 * opt in.
 */
@Configuration
public class StrictRequestBodyConfig {

  /**
   * Request bodies for which an unsupported field is an error rather than something to discard.
   *
   * <p>Add a type here only if it is a body this service <em>accepts</em>. Never add a model this
   * service deserializes from another service's response: those must keep tolerating fields we do
   * not know about yet.
   */
  private static final Set<Class<?>> FAIL_ON_UNKNOWN_FIELDS =
      Set.of(ApplicationSettingsPatchDTO.class);

  @Bean
  public JacksonModule strictRequestBodyModule() {
    return new StrictRequestBodyModule();
  }

  /** Registers {@link StrictRequestBodyProblemHandler} on the application's mapper. */
  static class StrictRequestBodyModule extends JacksonModule {

    @Override
    public String getModuleName() {
      return "strictRequestBody";
    }

    @Override
    public Version version() {
      return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
      context.addHandler(new StrictRequestBodyProblemHandler());
    }
  }

  /**
   * Turns an unknown property into a binding failure for the opted-in types, and defers to the
   * mapper's own (tolerant) behaviour for everything else.
   */
  static class StrictRequestBodyProblemHandler extends DeserializationProblemHandler {

    @Override
    public boolean handleUnknownProperty(
        DeserializationContext context,
        JsonParser parser,
        ValueDeserializer<?> deserializer,
        Object beanOrClass,
        String propertyName) {

      Class<?> targetType =
          (beanOrClass instanceof Class) ? (Class<?>) beanOrClass : beanOrClass.getClass();

      if (!FAIL_ON_UNKNOWN_FIELDS.contains(targetType)) {
        // Not opted in: let the mapper decide, which means the unknown field is skipped.
        return false;
      }

      Collection<Object> knownProperties =
          deserializer == null ? null : deserializer.getKnownPropertyNames();
      throw UnrecognizedPropertyException.from(parser, beanOrClass, propertyName, knownProperties);
    }
  }
}
