# Reviewed topic-format contract correction

Issue:
https://github.com/OpenResilienceInitiative/ORISO-ConsultingTypeService/issues/96

The published contract incorrectly described two runtime `String` fields as an
integer and as the non-standard `url` format. The coordinated UserService
consumer contract is updated in the same release lane. These exact corrections
are accepted once; unrelated provider changes still fail the compatibility
gate.

GET /topic the `items/fallbackUrl` response's property type/format changed from `string`/`url` to `string`/`` for status `200`
GET /topic the `items/welcomeMessage` response's property type/format changed from `string`/`int32` to `string`/`` for status `200`
GET /topic/public the `items/fallbackUrl` response's property type/format changed from `string`/`url` to `string`/`` for status `200`
GET /topic/public the `items/welcomeMessage` response's property type/format changed from `string`/`int32` to `string`/`` for status `200`
GET /topic/{id} the `fallbackUrl` response's property type/format changed from `string`/`url` to `string`/`` for status `200`
GET /topic/{id} the `welcomeMessage` response's property type/format changed from `string`/`int32` to `string`/`` for status `200`
GET /topicadmin the `items/fallbackUrl` response's property type/format changed from `string`/`url` to `string`/`` for status `200`
GET /topicadmin the `items/welcomeMessage` response's property type/format changed from `string`/`int32` to `string`/`` for status `200`
POST /topicadmin the `fallbackUrl` response's property type/format changed from `string`/`url` to `string`/`` for status `200`
POST /topicadmin the `welcomeMessage` response's property type/format changed from `string`/`int32` to `string`/`` for status `200`
GET /topicadmin/{id} the `fallbackUrl` response's property type/format changed from `string`/`url` to `string`/`` for status `200`
GET /topicadmin/{id} the `welcomeMessage` response's property type/format changed from `string`/`int32` to `string`/`` for status `200`
PUT /topicadmin/{id} the `fallbackUrl` response's property type/format changed from `string`/`url` to `string`/`` for status `200`
PUT /topicadmin/{id} the `welcomeMessage` response's property type/format changed from `string`/`int32` to `string`/`` for status `200`
