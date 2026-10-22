package de.caritas.cob.consultingtypeservice.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/** Disposable real receiver launcher for the cross-repository public HTTP proof test. */
public final class BootstrapV2ReceiverFixture {
  private BootstrapV2ReceiverFixture() {}

  public static void main(String[] arguments) throws Exception {
    JsonNode fixture =
        new ObjectMapper()
            .readTree(Files.readString(Path.of(required("ORISO_TASK_TOKEN_FIXTURE"))));
    if (!"FIVE_ACTORS".equals(fixture.path("identityModel").asText())) {
      throw new IllegalStateException("Bootstrap receiver needs the five-actor synthetic fixture");
    }
    String issuer = fixture.path("issuer").asText();
    System.setProperty("spring.profiles.active", "testing");
    System.setProperty("server.address", "127.0.0.1");
    System.setProperty("server.port", "0");
    // Pin fixture persistence even when the parent shell carries deployment environment values.
    System.setProperty(
        "spring.datasource.url",
        "jdbc:h2:mem:bootstrap-native-cts;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MariaDB;DATABASE_TO_LOWER=TRUE");
    System.setProperty("spring.datasource.username", "sa");
    System.setProperty("spring.datasource.password", "synthetic-context-h2");
    System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
    System.setProperty(
        "spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    System.setProperty("spring.datasource.hikari.maximum-pool-size", "4");
    System.setProperty("spring.datasource.hikari.minimum-idle", "0");
    System.setProperty("spring.liquibase.enabled", "false");
    System.setProperty("settings.smtp.reconcile.url", "");
    System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", issuer);
    System.setProperty(
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
        issuer + "/protocol/openid-connect/certs");
    System.setProperty("multitenancy.enabled", "true");
    System.setProperty(
        "consulting.types.json.path",
        "src/test/resources/consulting-type-settings-tenant-specific");
    String mongo = required("ORISO_BOOTSTRAP_MONGO_URI");
    if (!mongo.startsWith("mongodb://127.0.0.1:"))
      throw new IllegalStateException("Isolated Mongo URI required");
    System.setProperty("spring.mongodb.uri", mongo);
    System.setProperty(
        "spring.autoconfigure.exclude",
        "de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration");
    System.setProperty("feature.multitenancy.with.single.domain.enabled", "true");
    System.setProperty("csrf.header.property", "csrfHeader");
    System.setProperty("csrf.cookie.property", "csrfCookie");
    System.setProperty("TASK_IDENTITY_MODEL", "FIVE_ACTORS");
    System.setProperty("TASK_IDENTITY_REQUIRED_TASKS", "CONFIG_WIZARD");
    System.setProperty("TASK_IDENTITY_AUDIENCE", "consultingtypeservice");
    System.setProperty("ORISO_TASK_CONTEXT_KEY", required("ORISO_BOOTSTRAP_SHARED_KEY"));
    // Deliberately differs from the Tenant test's v1 key; the shared key is the only common key.
    byte[] oldReceiverKey = new byte[32];
    Arrays.fill(oldReceiverKey, (byte) 2);
    System.setProperty(
        "ORISO_TENANT_CREATION_CONTEXT_KEY", Base64.getEncoder().encodeToString(oldReceiverKey));
    System.setProperty("settings.smtp.password.encryption.secret", "synthetic-bootstrap-smtp-key");
    for (JsonNode actor : fixture.path("tasks")) {
      String prefix = "IDENTITY_" + actor.path("key").asText();
      System.setProperty(prefix + "_CLIENT_ID", actor.path("clientId").asText());
      System.setProperty(prefix + "_SERVICE_SUBJECT", actor.path("subject").asText());
    }
    var controls = new PairedControls(Path.of(required("ORISO_BOOTSTRAP_CONTROL_PATH")));
    var application = new SpringApplication(ConsultingTypeServiceApplication.class);
    application.addInitializers(context -> context.getBeanFactory().addBeanPostProcessor(controls));
    try (var context = application.run(arguments)) {
      MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);
      controls.start(context);
      snapshot(mongoTemplate, "ORISO_BOOTSTRAP_RECEIVER_BEFORE");
      Files.writeString(
          Path.of(required("ORISO_BOOTSTRAP_RECEIVER_READY")),
          // A single-label internal hostname lets the real tenant filter use the tenantId header.
          "http://localhost:" + ((WebServerApplicationContext) context).getWebServer().getPort(),
          StandardCharsets.UTF_8);
      try {
        System.in.read();
      } finally {
        controls.close();
      }
      snapshot(mongoTemplate, "ORISO_BOOTSTRAP_RECEIVER_AFTER");
    }
  }

  /**
   * Counts and content hashes prove no claim, allocator or type write at the rejected HTTP seam.
   */
  private static void snapshot(MongoTemplate mongo, String output) throws Exception {
    var state = new TreeMap<String, Object>();
    for (var collection :
        Map.of(
                "claims", mongo.getCollectionName(TenantBootstrapClaim.class),
                "counter", "consulting_type_id_sequences",
                "types", mongo.getCollectionName(ConsultingTypeEntity.class))
            .entrySet()) {
      var documents =
          mongo
              .getCollection(collection.getValue())
              .find()
              .sort(new Document("_id", 1))
              .into(new ArrayList<>());
      String content = String.join("\n", documents.stream().map(Document::toJson).toList());
      state.put(
          collection.getKey(),
          Map.of(
              "count", documents.size(),
              "sha256",
                  HexFormat.of()
                      .formatHex(
                          MessageDigest.getInstance("SHA-256")
                              .digest(content.getBytes(StandardCharsets.UTF_8)))));
    }
    Files.writeString(Path.of(required(output)), new ObjectMapper().writeValueAsString(state));
  }

  /** Only the explicit disposable launcher registers these finite real-storage scheduling controls. */
  @org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods = false)
  public static final class PairedControls
      implements org.springframework.beans.factory.config.BeanPostProcessor, AutoCloseable {
    private static final long TENANT = 9158L;
    private final Path directory;
    private final ObjectMapper json = new ObjectMapper();
    private final java.util.concurrent.CountDownLatch primary = new java.util.concurrent.CountDownLatch(1);
    private final java.util.concurrent.CountDownLatch delayed = new java.util.concurrent.CountDownLatch(1);
    private final java.util.concurrent.atomic.AtomicInteger insertEntries = new java.util.concurrent.atomic.AtomicInteger();
    private final java.util.concurrent.atomic.AtomicInteger actualInsertCalls = new java.util.concurrent.atomic.AtomicInteger();
    private final java.util.concurrent.atomic.AtomicInteger conditionalWrites = new java.util.concurrent.atomic.AtomicInteger();
    private final java.util.concurrent.atomic.AtomicInteger blockedWrites = new java.util.concurrent.atomic.AtomicInteger();
    private final java.util.concurrent.atomic.AtomicInteger unconditionalWrites = new java.util.concurrent.atomic.AtomicInteger();
    private volatile boolean armed, primaryPaused, delayedPaused, primaryCompleted, delayedDuplicate;
    private volatile boolean staleActive, staleCaptured, ordinaryPreserved, closed;
    private volatile boolean awareAttempted, awareBlocked, unawareAttempted, unawareBlocked;
    private volatile boolean retiredReadsHidden;
    private volatile int staleRefusals, retiredReadChecks;
    private String activeRepository;
    private String originalAttempt;
    private Long counterAtCapture;
    private ConsultingTypeEntity staleAware, staleUnaware;
    private MongoTemplate mongo;
    private org.springframework.context.ConfigurableApplicationContext context;
    private Thread thread;
    private long sequence;

    private PairedControls(Path directory) {
      if (!Files.isDirectory(directory) || Files.isSymbolicLink(directory)) throw refused();
      this.directory = directory;
    }

    public static String checkedCommand(JsonNode command) {
      if (command == null || !command.isObject()) throw refused();
      var fields = new java.util.HashSet<String>();
      command.fieldNames().forEachRemaining(fields::add);
      if (!command.isObject() || !fields.equals(java.util.Set.of("sequence", "command"))
          || !command.path("sequence").isIntegralNumber()
          || !command.path("sequence").canConvertToLong()
          || command.path("sequence").longValue() < 1
          || command.path("sequence").longValue() > 10000
          || !command.path("command").isTextual()
          || !java.util.Set.of("ARM", "OBSERVE", "RELEASE_PRIMARY", "RELEASE_DELAYED",
              "CAPTURE_STALE", "STALE_UPDATES", "ORDINARY_CONTROL")
              .contains(command.path("command").textValue())) throw refused();
      return command.path("command").textValue();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
      if (!(bean instanceof MongoTemplate)) return bean;
      var proxy = new org.springframework.aop.framework.ProxyFactory(bean);
      proxy.setProxyTargetClass(true);
      proxy.addAdvice((org.aopalliance.intercept.MethodInterceptor) invocation -> {
        Object[] arguments = invocation.getArguments();
        String method = invocation.getMethod().getName();
        if (armed && method.equals("insert") && arguments.length == 1
            && arguments[0] instanceof ConsultingTypeEntity
            && Long.valueOf(TENANT).equals(((ConsultingTypeEntity) arguments[0]).getTenantId().longValue())) {
          int ordinal = insertEntries.incrementAndGet();
          if (ordinal > 2) {
            actualInsertCalls.incrementAndGet();
            return invocation.proceed();
          }
          if (ordinal == 1) primaryPaused = true;
          else delayedPaused = true;
          if (!(ordinal == 1 ? primary : delayed).await(20, java.util.concurrent.TimeUnit.SECONDS))
            throw refused();
          actualInsertCalls.incrementAndGet();
          try {
            Object result = invocation.proceed();
            if (ordinal == 1) primaryCompleted = true;
            return result;
          } catch (org.springframework.dao.DuplicateKeyException collision) {
            if (ordinal == 2) delayedDuplicate = true;
            throw collision;
          }
        }
        if (staleActive && Thread.currentThread() == thread && method.equals("findAndReplace")) {
          conditionalWrites.incrementAndGet();
          if ("tenantAware".equals(activeRepository)) awareAttempted = true;
          else if ("tenantUnaware".equals(activeRepository)) unawareAttempted = true;
          else throw refused();
          Object result = invocation.proceed();
          if (result == null) {
            blockedWrites.incrementAndGet();
            if ("tenantAware".equals(activeRepository)) awareBlocked = true;
            else unawareBlocked = true;
          }
          return result;
        }
        if (staleActive && Thread.currentThread() == thread && method.equals("save"))
          unconditionalWrites.incrementAndGet();
        return invocation.proceed();
      });
      return proxy.getProxy();
    }

    private void start(org.springframework.context.ConfigurableApplicationContext context) {
      this.context = context;
      mongo = context.getBean(MongoTemplate.class);
      thread = new Thread(() -> {
        while (!closed) {
          try {
            Path request = directory.resolve("request.json");
            if (!Files.exists(request)) {
              Thread.sleep(20);
              continue;
            }
            if (Files.isSymbolicLink(request) || Files.size(request) > 4096) throw refused();
            JsonNode command = json.readTree(Files.readString(request));
            String action = checkedCommand(command);
            long next = command.path("sequence").longValue();
            if (next == sequence) {
              Thread.sleep(20);
              continue;
            }
            if (next != sequence + 1) throw refused();
            execute(action);
            sequence = next;
            write("ACK");
          } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return;
          } catch (Exception failure) {
            try { write("FIXTURE_FAILURE"); } catch (Exception ignored) { /* no raw diagnostics */ }
            primary.countDown();
            delayed.countDown();
            return;
          }
        }
      }, "bounded-paired-bootstrap-control");
      thread.setDaemon(true);
      thread.start();
    }

    private void execute(String command) {
      if (command.equals("ARM")) {
        if (armed) throw refused();
        armed = true;
      } else if (command.equals("RELEASE_PRIMARY")) primary.countDown();
      else if (command.equals("RELEASE_DELAYED")) delayed.countDown();
      else if (command.equals("CAPTURE_STALE")) {
        if (!primaryCompleted || !delayedPaused) throw refused();
        var original = mongo.findOne(org.springframework.data.mongodb.core.query.Query.query(
            org.springframework.data.mongodb.core.query.Criteria.where("tenantId").is(TENANT)),
            ConsultingTypeEntity.class);
        if (original == null) throw refused();
        originalAttempt = original.getBootstrapAttempt();
        counterAtCapture = currentCounter();
        if (counterAtCapture == null) throw refused();
        var document = new Document();
        mongo.getConverter().write(original, document);
        staleAware = mongo.getConverter().read(ConsultingTypeEntity.class, document);
        staleUnaware = mongo.getConverter().read(ConsultingTypeEntity.class, document);
        staleCaptured = true;
      } else if (command.equals("STALE_UPDATES")) {
        if (!staleCaptured) throw refused();
        staleActive = true;
        de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.setCurrentTenant(TENANT);
        try {
          for (String name : java.util.List.of("tenantAware", "tenantUnaware")) {
            activeRepository = name;
            try {
              context.getBean(name,
                  de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepositoryService.class)
                  .update(name.equals("tenantAware") ? staleAware : staleUnaware);
            } catch (RuntimeException blocked) {
              staleRefusals++;
            }
          }
        } finally {
          staleActive = false;
          activeRepository = null;
          de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.clear();
        }
        retiredReads();
      } else if (command.equals("ORDINARY_CONTROL")) ordinaryControl();
    }

    private void retiredReads() {
      boolean hidden = true;
      try {
        for (Long scope : java.util.List.of(0L, TENANT)) {
          de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.setCurrentTenant(scope);
          for (String name : java.util.List.of("tenantAware", "tenantUnaware")) {
            var repository = context.getBean(name,
                de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepositoryService.class);
            hidden &= hidden(() -> repository.getConsultingTypeById(staleAware.getId()));
            retiredReadChecks++;
            hidden &= hidden(() -> repository.getConsultingTypeBySlug(staleAware.getSlug()));
            retiredReadChecks++;
            hidden &= repository.getConsultingTypeByTenantId((int) TENANT) == null;
            retiredReadChecks++;
            hidden &= !repository.hasConsultingTypesForTenant((int) TENANT);
            retiredReadChecks++;
            hidden &= repository.getListOfConsultingTypes().stream()
                .noneMatch(value -> value.getId().equals(staleAware.getId()));
            retiredReadChecks++;
          }
        }
      } finally {
        de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.clear();
      }
      retiredReadsHidden = hidden;
    }

    private boolean hidden(java.util.function.Supplier<de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType> read) {
      try {
        var value = read.get();
        return value == null || !value.getId().equals(staleAware.getId());
      } catch (de.caritas.cob.consultingtypeservice.api.exception.httpresponses.NotFoundException absent) {
        return true;
      }
    }

    private void ordinaryControl() {
      var query = org.springframework.data.mongodb.core.query.Query.query(
          org.springframework.data.mongodb.core.query.Criteria.where("tenantId").ne(TENANT));
      query.with(org.springframework.data.domain.Sort.by("_id"));
      var existing = mongo.findOne(query, ConsultingTypeEntity.class);
      if (existing == null) throw refused();
      Document before = mongo.findById(existing.getId(), Document.class,
          mongo.getCollectionName(ConsultingTypeEntity.class));
      de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.setCurrentTenant(existing.getTenantId().longValue());
      try {
        for (String name : java.util.List.of("tenantAware", "tenantUnaware")) {
          var repository = context.getBean(name,
              de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepositoryService.class);
          ConsultingTypeEntity read = (ConsultingTypeEntity) repository.getConsultingTypeById(existing.getId());
          if (repository.update(read) == null) throw refused();
        }
      } finally {
        de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.clear();
      }
      ordinaryPreserved = before.equals(mongo.findById(existing.getId(), Document.class,
          mongo.getCollectionName(ConsultingTypeEntity.class)));
    }

    private Long currentCounter() {
      var counter = mongo.findById("consulting-types", Document.class, "consulting_type_id_sequences");
      if (counter == null) return null;
      Object value = counter.get("lastId");
      if (!(value instanceof Long || value instanceof Integer)) throw refused();
      return ((Number) value).longValue();
    }

    private void write(String state) throws Exception {
      var values = new TreeMap<String, Object>();
      values.put("sequence", sequence);
      values.put("state", state);
      values.put("primaryInsertReached", primaryPaused);
      values.put("delayedInsertReached", delayedPaused);
      values.put("actualInsertCalls", actualInsertCalls.get());
      values.put("primaryInsertCompleted", primaryCompleted);
      values.put("delayedInsertDuplicate", delayedDuplicate);
      values.put("staleCaptured", staleCaptured);
      values.put("conditionalWriteAttempts", conditionalWrites.get());
      values.put("conditionalWritesBlocked", blockedWrites.get());
      values.put("unconditionalSaveAttempts", unconditionalWrites.get());
      values.put("staleUpdateRefusals", staleRefusals);
      values.put("awareConditionalWriteAttempted", awareAttempted);
      values.put("awareConditionalWriteBlocked", awareBlocked);
      values.put("unawareConditionalWriteAttempted", unawareAttempted);
      values.put("unawareConditionalWriteBlocked", unawareBlocked);
      values.put("retiredReadChecks", retiredReadChecks);
      values.put("retiredReadsHidden", retiredReadsHidden);
      values.put("ordinaryPreserved", ordinaryPreserved);
      var query = org.springframework.data.mongodb.core.query.Query.query(
          org.springframework.data.mongodb.core.query.Criteria.where("tenantId").is(TENANT));
      values.put("liveTypes", mongo.count(query.addCriteria(
          org.springframework.data.mongodb.core.query.Criteria.where("bootstrapCompensated").ne(true)),
          ConsultingTypeEntity.class));
      var terminal = org.springframework.data.mongodb.core.query.Query.query(
          org.springframework.data.mongodb.core.query.Criteria.where("tenantId").is(TENANT)
              .and("bootstrapCompensated").is(true));
      values.put("terminalTypes", mongo.count(terminal, ConsultingTypeEntity.class));
      var live = mongo.findOne(query, ConsultingTypeEntity.class);
      values.put("newAttemptDistinct", live != null && originalAttempt != null
          && !originalAttempt.equals(live.getBootstrapAttempt()));
      values.put("newResultDistinct", live != null && staleAware != null
          && !live.getId().equals(staleAware.getId()));
      Long counter = currentCounter();
      if (counterAtCapture != null && counter == null) throw refused();
      values.put("counterUnchangedSinceCapture", counterAtCapture != null && counterAtCapture.equals(counter));
      values.put("counterNeverRewound", counterAtCapture == null || counter >= counterAtCapture);
      values.put("counterAdvancedExactlyOnce", counterAtCapture != null && counter == counterAtCapture + 1);
      Path temporary = directory.resolve("response.tmp");
      Files.writeString(temporary, json.writeValueAsString(values), StandardCharsets.UTF_8);
      Files.move(temporary, directory.resolve("response.json"),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
    }

    private static IllegalStateException refused() {
      return new IllegalStateException("Native paired fixture precondition refused");
    }

    @Override
    public void close() throws Exception {
      closed = true;
      primary.countDown();
      delayed.countDown();
      if (thread != null) {
        thread.interrupt();
        thread.join(3000);
        if (thread.isAlive()) throw refused();
      }
    }
  }

  private static String required(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Bootstrap receiver fixture configuration missing: " + name);
    }
    return value;
  }
}
