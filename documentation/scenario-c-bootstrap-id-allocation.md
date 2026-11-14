# Scenario C bootstrap ID allocation

Tenant bootstrap and both ordinary automatic creators reserve IDs through
`ConsultingTypeIdAllocator`. One fixed Mongo document in
`consulting_type_id_sequences` holds the global high-water mark. Initialization
uses `$max` over existing consulting-type IDs and immutable claim result IDs.
The reservation is a conditional, atomic `findAndModify` increment, with no
upsert on the increment query. Concurrent initialization retries only a duplicate
insert of that same counter. The supported range is 0 through Integer.MAX_VALUE;
exhaustion fails without wrapping or creating another counter.

An allocation whose response is lost, a stop before claim insertion, or a losing
same-tenant claim can leave a gap. Gaps are retained, never guessed or reused.
The counter is durable and must not be deleted or reset in production. These are
single-document atomic operations, not a multi-document transaction. Immutable
claims and exact actor/payload/result ownership checks retain their existing
meaning; retries use only the original claimed result ID.

Both repository CREATE paths use insert. A concurrent explicit/manual ID
collision is refused rather than saving over foreign data. The ordinary create
service retains its existing failure response when no creation result exists
(500); bootstrap foreign-result and changed-owner/payload conflicts remain 409.
Legitimate UPDATE paths continue using save. A privileged manual write directly
into a reserved ID is still foreign data and makes that claim fail closed; it is
never rebound or overwritten by bootstrap.

The allocation guarantee requires all automatic creators to use this shared
allocator. A mixed rollout with older max-plus-one creators is not proved safe.
Already-colliding historical claims are not automatically rewritten. Production
cutover and any repair of pre-existing damaged claims require separate evidence
and an owner decision; this local source repair changes no deployed data.
