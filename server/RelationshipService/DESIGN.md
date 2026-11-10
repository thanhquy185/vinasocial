# RelationshipService design

## Domain decisions

- A follow is a directed edge: `follower_id -> following_id`.
- Following a public account creates the edge immediately. Following a private account creates a pending follow request; accepting it creates the edge.
- “Friends” means mutual follows and is derived from two follow edges. There is no separate friendship row or friendship request.
- Close Friends is a private, owner-managed audience list. Membership has no reciprocal edge and no join request. It can be used by ContentService when deciding who may see limited-audience content.
- Blocking is a directed edge. Blocking removes follows in both directions, cancels pending follow requests in both directions, and removes close-friend membership in both directions.
- User IDs belong to UserService's database. RelationshipService stores IDs but does not create cross-service foreign keys.
- Timestamp strings use the existing `yyyy-MM-dd HH:mm:ss` convention from `TimeUtil` for this phase.

Meta's product descriptions support the distinction: Close Friends is private and controlled by its owner, without join requests or add/remove notifications ([Meta, 2019](https://about.fb.com/news/2019/10/privacy-matters-threads/)); Meta describes Instagram “friends” as followers the account follows back ([Meta, 2025](https://about.fb.com/news/2025/08/new-instagram-features-help-you-connect/amp/)).

## Delivery phases

### Phase 1 — social graph (implemented here)

Tables: `follows`, `follow_requests`, `blocks`, and `close_friends`.

API under `/api/v1/relationships`:

- `POST /users/{username}/follow`: follow a public account or create a private-account request.
- `DELETE /users/{username}/follow`: unfollow or cancel an outgoing request.
- `GET /me/followers`, `GET /me/following`, `GET /me/follow-requests`.
- `POST /follow-requests/{id}/accept`, `POST /follow-requests/{id}/reject`, `DELETE /follow-requests/{id}`.
- `GET /users/{username}/relationship`: viewer-specific follow state.
- `GET /me/close-friends`, `PUT /me/close-friends/{username}`, `DELETE /me/close-friends/{username}`.
- `POST /users/{username}/block`, `DELETE /users/{username}/block`.
- `GET /me/blocks`.

### Phase 2 — conversations and message requests (implemented here)

Tables: `conversations`, `conversation_participants`, `conversation_requests`, and `messages`. A direct conversation has a canonical `(direct_user_low_id, direct_user_high_id)` pair and a unique constraint; groups have a name, creator, participant roles, and join/leave state. Users who follow each other can start an active DM. Other first messages create a conversation request, independently of private-account follow approval. The recipient may inspect the request and its first message; acceptance adds them as a participant and promotes the conversation to the inbox. Keep `last_message_id` nullable so empty conversations are valid. Message history uses a stable ID cursor, edit and delete are author-only, and every operation checks membership. Each participant stores a `last_read_message_id` cursor so read state stays separate from message content state; `SENT`, `EDITED`, and `DELETED` remain content states. Blocking cancels pending message requests and prevents direct message reads and sends while the block is active; existing rows are retained.

API:

- `GET /api/v1/conversations/me`, `PATCH /api/v1/conversations/me/read`, `PATCH /api/v1/conversations/{id}/read`, `GET /api/v1/conversations/me/requests`, `GET /api/v1/conversations/me/sent-requests`.
- `POST /api/v1/users/{username}/messages` (starts a DM or sends a message request).
- `POST /api/v1/conversations/groups`, `POST /api/v1/conversations/{id}/participants/{username}`, `PATCH /api/v1/conversations/{id}/participants/{username}/role`, `DELETE /api/v1/conversations/{id}/participants/me`.
- `POST /api/v1/conversations/requests/{id}/accept`, `POST /api/v1/conversations/requests/{id}/reject`, `DELETE /api/v1/conversations/requests/{id}`.
- `GET /api/v1/conversations/{id}/participants`, `GET /api/v1/conversations/{id}/messages`, `POST /api/v1/conversations/{id}/messages`, `PATCH /api/v1/conversations/{id}/messages/{messageId}`, `DELETE /api/v1/conversations/{id}/messages/{messageId}`.

The message API stores the submitted payload in `ciphertext` and never decodes it. Website message senders encrypt before calling the API; message DTOs reject payloads that do not match the current E2EE envelope format. Older plaintext rows remain readable as legacy messages.

### Phase 3 — client-side encryption (implemented, prototype)

The website uses Web Crypto to encrypt each message payload with a fresh AES-256-GCM key and wraps that key for each active recipient device using RSA-OAEP-3072/SHA-256. Attachments are encrypted with AES-GCM before upload; their decryption key and metadata are placed inside the encrypted message. Nonces and the stable client message ID are authenticated as additional data. The server stores the ciphertext envelope and opaque encrypted attachment reference; private keys stay in browser IndexedDB and are marked non-extractable.

Public device keys are registered per user/device, devices can be revoked, and the browser applies local trust-on-first-use: once it has seen a device ID and key, it rejects a changed key for that ID. Session management links an authenticated login session to its E2EE device ID. The settings UI tries to revoke the linked E2EE key when a login session is revoked, but session and key revocations are separate service calls rather than one atomic operation. The server does not verify that the submitted envelope covers every active conversation participant/device, and users do not yet have a fingerprint/safety-number verification flow.

This is a functional project prototype, not a reviewed messaging protocol. It has no forward secrecy or post-compromise recovery, and a browser profile loss means that device's private key and access to messages addressed only to it are lost. Before presenting it as production-grade E2EE, evaluate an audited asynchronous protocol instead of custom crypto: Signal PQXDH/Double Ratchet for one-to-one chats and Sesame for multi-device session management ([Signal specifications](https://signal.org/docs/)); evaluate IETF MLS for groups ([RFC 9420](https://www.rfc-editor.org/rfc/rfc9420.html)).

### Phase 4 — scale and product integration

Add notification/outbox events, abuse/rate limits, cursor pagination, count projections, privacy-policy integration for messages and `BEST_FRIENDS` story visibility, retention rules, and event-driven user deletion cleanup. Reconcile the existing `ProfileStats` friend/follower counters with these projections. Treat counters as projections, not the source of truth.

## Important boundary

Phases 1–3 are implemented to the scope above. Phase 3 is a prototype and still needs protocol review, verification UX, robust device recovery, and server-side checks that an envelope includes all current conversation devices. Server storage alone does not make plaintext sent by a client into E2EE; the client encryption path and its key handling are required.
