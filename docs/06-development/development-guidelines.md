# Development Guidelines

## Mục đích

Tài liệu phân biệt rõ các convention quan sát được trong repository với quy ước đề xuất cho hoạt động phát triển. Mọi nội dung mang nhãn **PROPOSED** chưa phải chính sách đã được xác nhận của team.

## Scope và nguồn kiểm chứng

- Backend: [`Back_end/src/main`](../../Back_end/src/main), [`Back_end/src/test`](../../Back_end/src/test), [`pom.xml`](../../Back_end/pom.xml).
- Frontend: [`front_end/src`](../../front_end/src), [`front_end/package.json`](../../front_end/package.json).
- CI hiện có: [`.github/workflows`](../../.github/workflows).
- Architecture và contract: [System Architecture](../01-architecture/system-architecture.md), [API Documentation](../03-api/api-documentation.md).

## 1. Development Overview

Repository hiện là monorepo với ba vùng trách nhiệm:

- `Back_end/`: Java/Spring Boot application.
- `front_end/`: React/TypeScript SPA.
- `infrastructure/`: Docker, provisioning, Kubernetes và monitoring.

Không phát hiện tài liệu team chính thức cho branching, commit format, pull-request template hoặc branch protection. Các phần tương ứng bên dưới vì vậy là đề xuất.

## 2. Repository Organization

**Hiện trạng đã xác minh**
```text
Back_end/src/main/java/com/example/web_bansach/
├── common/          shared response, exception, cache, config, logging
├── infrastructure/ external adapters và persistence support
├── module/          package theo domain
└── security/        JWT/filter/principal/security handlers

front_end/src/
├── app/             bootstrap, provider, routes
├── components/      shared UI/layout
├── features/        package theo feature
├── services/        API/storage/websocket clients
├── stores/          client state
├── hooks/           shared hooks
└── types/           shared TypeScript types
```

> **Đề xuất (PROPOSED)**
- Feature mới phải đặt trong domain/feature gần nhất; chỉ đưa vào `common` khi có ít nhất hai consumer và không chứa business rule riêng của một domain.
- Không tạo package `util` tổng hợp cho logic nghiệp vụ.
- Integration mới nên có interface nhỏ ở boundary phù hợp và implementation trong `infrastructure` khi pattern hiện tại cho phép.
- Thay đổi infrastructure phải tách khỏi business change khi có thể để review ownership rõ ràng.

## 3. Backend Package Conventions

**Hiện trạng đã xác minh**
- Controller: `module.<domain>.controller`.
- Service interface/concrete: `service`, `service.impl`; một số module dùng concrete service trực tiếp.
- Persistence: `repository`, `entity`.
- HTTP contract: `dto.request`, `dto.response`.
- Mapping: `mapper`.
- Shared exception/response/security/integration tách khỏi domain.

Project chưa áp dụng đồng nhất một pattern service: book/cart/category/author/wishlist tách interface/implementation; order/payment/voucher/review/inventory có cấu trúc khác. Không nên mô tả một convention chưa tồn tại là bắt buộc.

> **Đề xuất (PROPOSED)**
- Khi sửa module hiện có, theo cấu trúc của chính module đó trừ khi có refactor được review riêng.
- Controller không truy cập repository trực tiếp.
- Service không trả JPA entity ra API mới; dùng response DTO và mapper.
- Cross-domain dependency phải đi qua service khi cần bảo vệ business invariant; truy cập repository chéo phải được nêu trong PR vì làm tăng coupling.
- Interface chỉ nên được tạo khi có boundary/alternative implementation/test seam thực sự, không tạo máy móc.

## 4. Naming Conventions

**Hiện trạng đã xác minh**
- Java class: PascalCase; method/field: camelCase; constant: UPPER_SNAKE_CASE.
- Entity thường dùng singular class và plural table; ngoại lệ `Users`, `Roles`.
- Controller được đặt theo audience (`Public`, `User`, `Admin`) nhưng tên class không tự quyết định access; security config mới là nguồn quyền thực tế.
- Endpoint hiện dùng cả tiếng Việt và tiếng Anh.

> **Đề xuất (PROPOSED)**
- Tên method/service mới dùng tiếng Anh nhất quán; không đổi endpoint/class legacy chỉ để thỏa style trong cùng một feature PR.
- DTO đặt hậu tố `Request`/`Response`; không dùng entity làm request body.
- Repository lock method dùng hậu tố `ForUpdate` và phải có `@Lock(PESSIMISTIC_WRITE)` rõ ràng.
- Event name dùng UPPER_SNAKE_CASE, ví dụ `ORDER_CREATED`.
- Nếu chuẩn hóa route prefix, thực hiện qua versioned API/migration plan, không silently rename endpoint.

## 5. Coding Standards

**Hiện trạng đã xác minh**
- Java 17; Spring Boot 4.0.1; Lombok được dùng cho DTO/entity/logging.
- Constructor injection phổ biến nhưng một số class còn field injection `@Autowired`.
- Transaction đặt ở service.
- `BigDecimal` dùng cho tiền.
- Time dùng `LocalDate`, `LocalDateTime`.

> **Đề xuất (PROPOSED)**
- Ưu tiên constructor injection cho code mới.
- Không dùng `double` cho business money; chỉ parse boundary rồi chuyển `BigDecimal`.
- Không log password, token, API key, raw verification code hoặc webhook authorization.
- Không bắt `Exception` chỉ để che lỗi; nếu rethrow, giữ cause và để centralized handler xử lý.
- Comment giải thích invariant/lý do, không lặp lại câu lệnh code.
- Tách constant cho status/event khi domain chưa có enum; ưu tiên enum cho state domain trong thay đổi có migration tương thích.
- Mọi external call phải có timeout/retry policy được review; không giả định broker/cache luôn sẵn sàng.

## 6. DTO & Validation Guidelines

**Hiện trạng đã xác minh**
- Jakarta Validation nằm trên request DTO và controller dùng `@Valid` ở phần lớn JSON/multipart request.
- Business validation nằm tại service/validation service.
- Payment request là ngoại lệ: không có Bean Validation và được kiểm tra thủ công trong service.

> **Đề xuất (PROPOSED)**
- Dùng Bean Validation cho shape/local constraints; dùng service cho rule cần database/current state.
- Không tin user id, amount, role hoặc ownership do client gửi; lấy identity từ `Authentication` và tính amount phía server.
- Validate cả create và update; update DTO nên biểu đạt rõ field optional/required.
- Error message phải nhất quán tiếng Việt hoặc contract đã chọn, không trộn encoding lỗi.
- Khi thêm field DTO, cập nhật [API Documentation](../03-api/api-documentation.md) và test validation.

## 7. Exception Handling Guidelines

**Hiện trạng đã xác minh**
- `ResourceNotFoundException` → 404.
- `BusinessException`, `ValidationException`, invalid argument → 400.
- Bad credentials/unauthorized → 401.
- Forbidden/access denied → 403.
- Unknown exception → generic 500; detail được log server-side.
- Security filter errors dùng `ErrorResponse`, application dùng `ApiResponse`.

> **Đề xuất (PROPOSED)**
- Không trả stack trace, SQL detail hoặc external-service credential cho client.
- Không dùng 500 cho lỗi nghiệp vụ dự kiến.
- Exception message mới phải hữu ích nhưng không tiết lộ resource/identity nhạy cảm.
- Nếu chuẩn hóa hai error schema, coi đó là API contract change và cập nhật frontend/test/docs cùng PR.
- Controller không tự bọc mọi exception nếu global handler đã xử lý đúng.

## 8. Logging Guidelines

**Hiện trạng đã xác minh**
- SLF4J/Lombok `@Slf4j`.
- `RequestIdFilter` đưa request id vào MDC; console pattern chứa `service` và `requestId`.
- `LogMaskingUtil.maskEmail` được dùng ở các flow nhạy cảm chính.
- Root INFO, Spring/Hibernate SQL WARN theo configuration.

> **Đề xuất (PROPOSED)**
- Log lifecycle bằng stable identifiers: orderId, paymentId, userId; tránh PII khi không cần.
- Mask email/phone; tuyệt đối không log JWT, password, verification/reset token, API key hoặc callback Authorization.
- INFO cho business lifecycle thành công; WARN cho expected rejection; ERROR cho unexpected/system failure.
- Không log cùng exception ở nhiều layer nếu không bổ sung context.
- Realtime/email retry log phải có correlation identifiers nhưng không chứa message body nhạy cảm.

## 9. Transaction Guidelines

**Hiện trạng đã xác minh**
- Query service thường dùng `@Transactional(readOnly = true)`.
- Business command dùng `@Transactional`; checkout/cancel/payment có `rollbackFor = Exception.class` ở các flow chính.
- Pessimistic locks nằm trong repository method `ForUpdate`.
- Email publisher hỗ trợ publish after commit.

> **Đề xuất (PROPOSED)**
- Transaction boundary đặt ở public service orchestration method, không ở controller.
- Lock query chỉ được gọi trong active transaction.
- Giữ lock section ngắn; tránh external network call trong lúc giữ DB lock nếu có thể.
- Khi thay đổi inventory/order/payment/voucher, bổ sung concurrent/idempotency test tương ứng.
- Event phải phản ánh committed state; với guarantee cao hơn cần quyết định outbox riêng, không giả định current realtime publish là atomic với DB.
- Nêu rõ rollback expectation khi catch exception bên trong transaction.

## 10. Git Branching Strategy — PROPOSED

Không có bằng chứng team đang dùng GitFlow hoặc trunk-based development. Đề xuất tối thiểu:

- Nhánh bảo vệ chính: tên nhánh thực tế cần Tech Lead xác nhận.
- Nhánh công việc ngắn hạn: `feature/<scope>`, `fix/<scope>`, `docs/<scope>`, `chore/<scope>`.
- Rebase hoặc merge policy do team chọn; không rewrite history của nhánh chia sẻ nếu chưa thống nhất.
- Mỗi PR tập trung một mục tiêu; tách documentation-only, infrastructure và business logic khi không phụ thuộc nhau.
- Hotfix/release branching chỉ thêm khi quy trình phát hành thực sự cần.

## 11. Commit Message Guidelines — PROPOSED

Repository chưa chứng minh team bắt buộc Conventional Commits. Đề xuất format dễ đọc:

```text
<type>(<scope>): <mô tả ngắn ở thể mệnh lệnh>
```

Type gợi ý: `feat`, `fix`, `docs`, `test`, `refactor`, `chore`, `build`, `ci`. Ví dụ: `docs(payment): document webhook state handling`.

- Không đưa secret, issue content nhạy cảm hoặc generated noise vào commit.
- Commit nên build/review được và không trộn formatting toàn repository với behavior change.
- Breaking API/schema change phải được ghi rõ trong body/footer theo policy team xác nhận.

## 12. Pull Request Process — PROPOSED

1. Mô tả problem, scope và behavior trước/sau.
2. Liệt kê module/endpoint/schema/config bị tác động.
3. Ghi test đã chạy và test chưa chạy; không ghi “pass” nếu không có kết quả.
4. Với API change: cập nhật DTO, controller test, frontend consumer và docs.
5. Với schema change: cần migration/review riêng; không dựa vào `ddl-auto=update` như migration strategy.
6. Với security/payment/inventory: yêu cầu reviewer domain/Tech Lead.
7. Với UI: kèm ảnh/video khi thay đổi hiển thị.
8. Giải quyết review comment hoặc ghi rationale nếu không áp dụng.

Branch protection, required checks và reviewer policy là **UNVERIFIED**; tài liệu này không cấu hình chúng.

## 13. Code Review Checklist — PROPOSED

**Correctness**
- Business rule có nguồn rõ ràng và test happy/error path.
- Ownership/RBAC không dựa vào tên controller.
- Tiền dùng `BigDecimal`; status transition hợp lệ.
- Inventory/voucher/order/payment giữ invariant và không tạo quantity âm.
- Idempotency/concurrency được xem xét cho command có thể retry.

**API và data**
- Không silently đổi route/request/response.
- DTO validation và service validation đủ nhưng không lặp vô ích.
- Entity/DDL/migration đồng bộ; FK/index/unique không được suy đoán.
- Pagination có boundary hợp lý.

**Security và operations**
- Không log/commit secret hoặc PII.
- Public endpoint thực sự cần public.
- External input/webhook được authenticate và validate.
- Timeout, retry, failure mode và observability được cân nhắc.
- Cache eviction đúng với write path.

**Maintainability**
- Dependency mới cần thiết và đúng layer.
- Không mở rộng `common` bằng business logic một domain.
- Documentation/cross-links cập nhật và không trùng lặp.

## 14. Testing Expectations

**Hiện trạng đã xác minh**
Backend có unit tests cho auth, cart, book, order, payment, review, user, voucher, dashboard; controller tests cho auth/cart/payment; một `BookstoreFlowIntegrationTest`; Redis serialization test. Test profile dùng H2, cache none và tắt RabbitMQ listeners. Frontend có Vitest/Testing Library tests.

Không có bằng chứng JaCoCo threshold, Testcontainers, REST Assured hoặc mandatory CI gate trong dependency/config đã audit.

> **Đề xuất (PROPOSED)**
- Service unit test: business branches và exception.
- Controller test: mapping, validation, status, response shape và authorization.
- Repository/integration test: custom JPQL, lock/unique behavior và transaction rollback.
- Workflow integration test: cart→order→payment và cancel→inventory restore.
- Security test: public/USER/ADMIN matrix, ownership, expired/invalid token.
- Frontend test: auth guard, API error state và critical commerce interaction.
- External adapter test dùng mock/stub; không gọi service thật từ unit test.
- Mọi bug fix nên có regression test khi khả thi.

## 15. Definition of Done — PROPOSED

Một thay đổi hoàn tất khi:

- Scope và acceptance criteria được đáp ứng.
- Code compile/typecheck và test liên quan đã chạy; kết quả được ghi chính xác.
- Không chứa secret, debug log hoặc generated artifact không cần thiết.
- Validation, authorization, transaction, concurrency và error handling đã được review phù hợp risk.
- API/database/config change có backward-compatibility hoặc migration note.
- Tài liệu liên quan được cập nhật, link hợp lệ.
- Reviewer phù hợp đã approve theo policy team.
- Deployment/rollback impact được ghi nếu thay đổi có tác động runtime.

## 16. Documentation Update Rules — PROPOSED

- **Component/module/integration · [System Architecture](../01-architecture/system-architecture.md)**
- **Entity/table/index/transaction · [Database Design](../02-database/database-design.md)**
- **Controller/DTO/validation/status · [API Documentation](../03-api/api-documentation.md)**
- **Cart/order/inventory/voucher/payment · [Order & Payment Workflow](../04-business/order-payment-workflow.md)**
- **JWT/RBAC/CORS/webhook/realtime security · [Security Design](../05-security/security-design.md)**
- **Team engineering process/convention · Tài liệu này**


- Source code vẫn là source of truth; docs update phải cùng PR với contract/behavior change.
- Không sao chép endpoint catalog vào nhiều file; workflow chỉ link sang API doc.
- Mọi claim runtime chưa test phải ghi `UNVERIFIED`.
- Mermaid/link phải được kiểm tra trước merge.
- Không đưa secret/credential hoặc environment value thật vào tài liệu.

## 17. Unverified Findings cần Tech Lead xác nhận

- Branch chính, branching/merge/rebase strategy.
- Commit convention và issue tracker integration.
- Required CI checks, coverage threshold và branch protection.
- Code owner/reviewer cho security, payment, database và infrastructure.
- Release/versioning/backward-compatibility policy.
- Migration ownership và database change process.
- SLA, observability, incident response và rollback process.
