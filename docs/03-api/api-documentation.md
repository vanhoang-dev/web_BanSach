# Tài liệu API

## Mục đích

Tài liệu liệt kê API thực tế được khai báo bởi controller, request mapping và security rules. Đây không phải OpenAPI specification và không thay thế việc đọc DTO/source khi thay đổi contract.

## Scope và nguồn kiểm chứng

- Controllers: [`module/*/controller`](../../Back_end/src/main/java/com/example/web_bansach/module).
- URL rules: [`Endpoints`](../../Back_end/src/main/java/com/example/web_bansach/security/config/Endpoints.java).
- Filter/method security: [`SecurityConfig`](../../Back_end/src/main/java/com/example/web_bansach/security/config/SecurityConfig.java) và `@PreAuthorize` trên controller.
- DTO/validation: các package `module/*/dto`.
- Response/error: [`ApiResponse`](../../Back_end/src/main/java/com/example/web_bansach/common/response/ApiResponse.java), [`PageResponse`](../../Back_end/src/main/java/com/example/web_bansach/common/response/PageResponse.java), [`GlobalExceptionHandler`](../../Back_end/src/main/java/com/example/web_bansach/common/exception/GlobalExceptionHandler.java).

## 1. API Overview

Backend không cấu hình global context path. Base URL do môi trường quyết định; local default là `http://localhost:8080`. Prefix thực tế không đồng nhất:

- `/tai-khoan`: authentication.
- `/api/categories`, `/api/authors`: public metadata catalog.
- `/user`: public book catalog và authenticated user operations.
- `/admin`, `/api/admin`, `/user_for_admin`: administration.
- `/api/payment`: payment, webhook và SSE.

Ký hiệu access:

- **Public**: không cần JWT trong security filter chain.
- **USER+**: `ROLE_USER` hoặc `ROLE_ADMIN`.
- **ADMIN**: `ROLE_ADMIN`.
- **Special token**: URL được permit để mở stream nhưng controller tự xác minh JWT query parameter.

## 2. Authentication Requirements

Endpoint được bảo vệ nhận access token:

```http
Authorization: Bearer <access-jwt>
```

JWT subject là email; claim `roles` được chuyển thành Spring authorities. Không có refresh-token hoặc logout endpoint. Chi tiết tại [Security Design](../05-security/security-design.md).

## 3. Authentication APIs

Nguồn: [`AuthController`](../../Back_end/src/main/java/com/example/web_bansach/module/auth/controller/AuthController.java).

- **POST · `/tai-khoan/dang-ky`**
  - Access: Public
  - Request body: `UserRequest`
  - Kết quả chính: Tạo inactive user và gửi verification code
- **POST · `/tai-khoan/xac-nhan-email`**
  - Access: Public
  - Request body: `VerifyAccountRequest`
  - Kết quả chính: Kích hoạt tài khoản khi mã hợp lệ
- **POST · `/tai-khoan/gui-lai-ma-xac-nhan`**
  - Access: Public
  - Request body: `ResendVerificationCodeRequest`
  - Kết quả chính: Gửi mã mới theo cooldown
- **POST · `/tai-khoan/dang-nhap`**
  - Access: Public
  - Request body: `LoginRequest`
  - Kết quả chính: `LoginResponse` chứa JWT, user id, email và roles
- **POST · `/tai-khoan/quen-mat-khau`**
  - Access: Public
  - Request body: `ForgotPasswordRequest`
  - Kết quả chính: Phản hồi trung tính và gửi reset link nếu user tồn tại
- **POST · `/tai-khoan/dat-lai-mat-khau`**
  - Access: Public
  - Request body: `ResetPasswordRequest`
  - Kết quả chính: Đổi mật khẩu và đánh dấu token đã dùng


Validation chính:

- Registration: username 3–50; password 8–50 có chữ hoa, chữ thường, số, ký tự đặc biệt; email hợp lệ; phone rỗng hoặc 10 chữ số bắt đầu bằng `0`; address tối đa 255.
- Verification code: đúng 6 chữ số.
- Reset password: token không rỗng; password theo rule registration; `confirmPassword` được service kiểm tra khớp.

Nguồn: [`UserRequest`](../../Back_end/src/main/java/com/example/web_bansach/module/auth/dto/request/UserRequest.java), [`VerifyAccountRequest`](../../Back_end/src/main/java/com/example/web_bansach/module/auth/dto/request/VerifyAccountRequest.java), [`ResetPasswordRequest`](../../Back_end/src/main/java/com/example/web_bansach/module/auth/dto/request/ResetPasswordRequest.java).

## 4. User APIs

- **GET · `/user/me`**
  - Access: USER+
  - Kết quả: Profile của principal hiện tại
- **PUT · `/user/update-profile`**
  - Access: USER+
  - Input: `UpdateUserRequest`
  - Kết quả: Cập nhật fullname/address/phone/email
- **POST · `/user/change-password`**
  - Access: USER+
  - Input: `ChangePasswordRequest`
  - Kết quả: Đổi mật khẩu sau khi kiểm tra password hiện tại
- **GET · `/user_for_admin/user/{id}`**
  - Access: ADMIN
  - Input: path `id`
  - Kết quả: User entity theo id
- **GET · `/user_for_admin/all`**
  - Access: ADMIN
  - Input: `page=0`, `size=10`
  - Kết quả: Page user chưa soft-delete
- **PUT · `/user_for_admin/user/{id}`**
  - Access: ADMIN
  - Input: `AdminUpdateUserRequest`
  - Kết quả: Cập nhật user
- **DELETE · `/user_for_admin/user/delete/{id}`**
  - Access: ADMIN
  - Input: path `id`
  - Kết quả: Soft-delete user


Controller admin khai báo base path có dấu `/` cuối và method path có `/` đầu; bảng ghi effective route Spring sử dụng. Nguồn: [`UserProfileController`](../../Back_end/src/main/java/com/example/web_bansach/module/user/controller/UserProfileController.java), [`UserAdminController`](../../Back_end/src/main/java/com/example/web_bansach/module/user/controller/UserAdminController.java).

## 5. Catalog APIs

### Books

- **GET · `/user/books`**
  - Access: Public
  - Parameters/body: `page=0`, `size=10`, optional `keyword`, `categoryId`, `authorId`, `sortBy=createdAt`, `sortDirection=desc`
  - Kết quả: Page sách chưa bị xóa
- **GET · `/user/books/{id}`**
  - Access: Public
  - Parameters/body: path `id`
  - Kết quả: Chi tiết sách
- **GET · `/admin/books`**
  - Access: ADMIN
  - Parameters/body: `page=0`, `size=10`
  - Kết quả: Page admin
- **GET · `/admin/books/{id}`**
  - Access: ADMIN
  - Parameters/body: path `id`
  - Kết quả: Chi tiết admin
- **POST · `/admin/books/create-book`**
  - Access: ADMIN
  - Parameters/body: multipart: validated `BookRequest` + optional `image`
  - Kết quả: Tạo sách và inventory
- **PUT · `/admin/books/update-book/{id}`**
  - Access: ADMIN
  - Parameters/body: multipart: validated `BookRequest` + optional `image`
  - Kết quả: Cập nhật sách
- **DELETE · `/admin/books/delete-book/{id}`**
  - Access: ADMIN
  - Parameters/body: path `id`
  - Kết quả: Soft-delete sách


`BookRequest`: title/ISBN/publisher bắt buộc; publication year bắt buộc; price > 0; author/category id dương; discount id tùy chọn. Nguồn: [`BookPublicController`](../../Back_end/src/main/java/com/example/web_bansach/module/book/controller/BookPublicController.java), [`BookAdminController`](../../Back_end/src/main/java/com/example/web_bansach/module/book/controller/BookAdminController.java), [`BookRequest`](../../Back_end/src/main/java/com/example/web_bansach/module/book/dto/request/BookRequest.java).

### Authors

- **GET · `/api/authors`**
  - Access: Public
  - Input: `page=0`, `size=10`
- **GET · `/api/authors/search`**
  - Access: Public
  - Input: optional `keyword`, `page=0`, `size=10`
- **GET · `/api/authors/{id}`**
  - Access: Public
  - Input: path `id`
- **GET · `/admin/authors`**
  - Access: ADMIN
  - Input: `page=0`, `size=10`
- **POST · `/admin/authors`**
  - Access: ADMIN
  - Input: `AuthorRequest`
- **PUT · `/admin/authors/{id}`**
  - Access: ADMIN
  - Input: `AuthorRequest`
- **DELETE · `/admin/authors/{id}`**
  - Access: ADMIN
  - Input: path `id`


### Categories

- **GET · `/api/categories`**
  - Access: Public
  - Input: `page=0`, `size=10`
- **GET · `/api/categories/{id}`**
  - Access: Public
  - Input: path `id`
- **GET · `/api/categories/search`**
  - Access: Public
  - Input: required `keyword`, `page=0`, `size=10`
- **GET · `/api/admin/categories`**
  - Access: ADMIN
  - Input: `page=0`, `size=10`
- **POST · `/api/admin/categories`**
  - Access: ADMIN
  - Input: `CreateCategoryRequest`
- **PUT · `/api/admin/categories/{id}`**
  - Access: ADMIN
  - Input: `UpdateCategoryRequest`
- **DELETE · `/api/admin/categories/{id}`**
  - Access: ADMIN
  - Input: path `id`
- **PUT · `/api/admin/categories/{id}/activate`**
  - Access: ADMIN
  - Input: path `id`
- **PUT · `/api/admin/categories/{id}/deactivate`**
  - Access: ADMIN
  - Input: path `id`


## 6. Cart, Order và Inventory APIs

### Cart

- **GET · `/user/cart`**
  - Access: USER+
- **POST · `/user/cart/items`**
  - Access: USER+
  - Input: `AddToCartRequest {bookId, quantity}`; cả hai >= 1
- **PUT · `/user/cart/items/{itemId}`**
  - Access: USER+
  - Input: path `itemId`; `UpdateCartItemRequest {quantity >= 1}`
- **DELETE · `/user/cart/items/{itemId}`**
  - Access: USER+
  - Input: path `itemId`
- **DELETE · `/user/cart/clear`**
  - Access: USER+


Nguồn: [`CartController`](../../Back_end/src/main/java/com/example/web_bansach/module/cart/controller/CartController.java).

### Orders

- **POST · `/user/orders`**
  - Access: USER+
  - Input: `CreateOrderRequest`
- **POST · `/user/orders/buy-now`**
  - Access: USER+
  - Input: `BuyNowOrderRequest` mở rộng `CreateOrderRequest` với `bookId`, `quantity`
- **GET · `/user/orders`**
  - Access: USER+
  - Input: `page=0`, `size=10`
- **GET · `/user/orders/{id}`**
  - Access: USER+
  - Input: Chỉ order thuộc principal
- **PUT · `/user/orders/{id}/cancel`**
  - Access: USER+
  - Input: Chỉ order thuộc principal
- **GET · `/admin/orders`**
  - Access: ADMIN
  - Input: `page=0`, `size=10`
- **GET · `/admin/orders/{id}`**
  - Access: ADMIN
  - Input: path `id`
- **PUT · `/admin/orders/{id}/status`**
  - Access: ADMIN
  - Input: `UpdateOrderStatusRequest {status}`
- **PUT · `/admin/orders/{id}/cancel`**
  - Access: ADMIN
  - Input: path `id`


`CreateOrderRequest`: receiver name bắt buộc/tối đa 255; phone khớp `(0|+84)` và 9 chữ số tiếp theo; address bắt buộc/tối đa 500; shipping method tối đa 100; voucher code tối đa 50; idempotency key tối đa 64. Client có field `shippingFee`, nhưng service tự tính `STANDARD` = 0 và `EXPRESS` = 30000; giá trị client không quyết định phí thực tế.

Nguồn: [`OrderPublicController`](../../Back_end/src/main/java/com/example/web_bansach/module/order/controller/OrderPublicController.java), [`OrderAdminController`](../../Back_end/src/main/java/com/example/web_bansach/module/order/controller/OrderAdminController.java), [`CreateOrderRequest`](../../Back_end/src/main/java/com/example/web_bansach/module/order/dto/request/CreateOrderRequest.java).

### Inventory

- **GET · `/user/inventory/book/{bookId}`**
  - Access: USER+
  - Input: path `bookId`
- **GET · `/admin/inventory`**
  - Access: ADMIN
- **PUT · `/admin/inventory/{id}/set/{quantity}`**
  - Access: ADMIN
  - Input: quantity >= 0 qua service
- **POST · `/admin/inventory/{id}/adjust/{delta}`**
  - Access: ADMIN
  - Input: delta có thể âm, kết quả không được âm
- **PUT · `/admin/inventory/{id}/reconcile/{quantity}`**
  - Access: ADMIN
  - Input: quantity >= 0


## 7. Voucher, Review và Wishlist APIs

### Vouchers

- **GET · `/user/vouchers`**
  - Access: USER+
  - Input: `page=0`, `size=10`
- **GET · `/user/vouchers/code/{code}`**
  - Access: USER+
  - Input: Voucher còn hiệu lực theo code
- **POST · `/user/vouchers/{voucherId}/claim`**
  - Access: USER+
  - Input: path `voucherId`
- **GET · `/user/vouchers/my`**
  - Access: USER+
  - Input: `page=0`, `size=20`
- **GET · `/user/vouchers/claimed`**
  - Access: USER+
  - Input: `page=0`, `size=100`
- **POST · `/admin/vouchers`**
  - Access: ADMIN
  - Input: `CreateVoucherRequest`
- **PUT · `/admin/vouchers/{voucherId}`**
  - Access: ADMIN
  - Input: `CreateVoucherRequest`
- **DELETE · `/admin/vouchers/{voucherId}`**
  - Access: ADMIN
  - Input: path id
- **GET · `/admin/vouchers/{voucherId}`**
  - Access: ADMIN
  - Input: path id
- **GET · `/admin/vouchers`**
  - Access: ADMIN
  - Input: `page=0`, `size=10`
- **GET · `/admin/vouchers/expired`**
  - Access: ADMIN
  - Input: `page=0`, `size=10`


Voucher request: code 3–50; discount 1–100%; `maxDiscount >= 0`; quantity >= 1; expiry bắt buộc.

### Reviews

Tất cả route `/user/reviews/**` là **USER+** do class-level `@PreAuthorize`, kể cả danh sách và statistics.

- **POST · `/user/reviews`**
  - Access: USER+
  - Input: `CreateReviewRequest {bookId, rating 1..5, comment <= 1000}`
- **PUT · `/user/reviews/{reviewId}`**
  - Access: USER+
  - Input: Cùng DTO; chỉ chủ review
- **DELETE · `/user/reviews/{reviewId}`**
  - Access: USER+
  - Input: Chỉ chủ review
- **GET · `/user/reviews/book/{bookId}/my-review`**
  - Access: USER+
  - Input: path book id
- **GET · `/user/reviews/book/{bookId}`**
  - Access: USER+
  - Input: `page=0`, `size=10`
- **GET · `/user/reviews/book/{bookId}/stats`**
  - Access: USER+
  - Input: average/count
- **GET · `/admin/reviews/user/{userId}`**
  - Access: ADMIN
  - Input: `page=0`, `size=10`
- **GET · `/admin/reviews/{reviewId}`**
  - Access: ADMIN
  - Input: path id
- **DELETE · `/admin/reviews/{reviewId}`**
  - Access: ADMIN
  - Input: path id


Create review còn yêu cầu user đã có order item của book trong order `COMPLETED`, và không có review trước đó. Nguồn: [`ReviewService`](../../Back_end/src/main/java/com/example/web_bansach/module/review/service/ReviewService.java).

### Wishlist

- **POST · `/user/wishlist/books/{bookId}`**
  - Access: USER+
  - Input: path id
- **DELETE · `/user/wishlist/books/{bookId}`**
  - Access: USER+
  - Input: path id
- **GET · `/user/wishlist/books/{bookId}/check`**
  - Access: USER+
  - Input: path id
- **GET · `/user/wishlist`**
  - Access: USER+
  - Input: `page=0`, `size=10`
- **GET · `/user/wishlist/count`**
  - Access: USER+
- **DELETE · `/user/wishlist/clear`**
  - Access: USER+


## 8. Dashboard API

- **GET · `/admin/dashboard`**
  - Access: ADMIN
  - Kết quả: User/book/order counts, successful revenue/sold quantity và recent orders theo DTO


Nguồn: [`AdminDashboardController`](../../Back_end/src/main/java/com/example/web_bansach/module/dashboard/controller/AdminDashboardController.java), [`AdminDashboardService`](../../Back_end/src/main/java/com/example/web_bansach/module/dashboard/service/AdminDashboardService.java).

## 9. Payment, Webhook và Realtime APIs

- **POST · `/api/payment/initiate`**
  - Access: USER+
  - Input/behavior: `PaymentRequest {orderId, amount, returnUrl, description}`
- **POST · `/api/payment/sepay-webhook`**
  - Access: Public + webhook API key
  - Input/behavior: `Authorization` header và JSON payload
- **GET · `/api/payment/status/{paymentId}`**
  - Access: USER+
  - Input/behavior: Owner hoặc ADMIN
- **GET · `/api/payment/status/order/{orderId}`**
  - Access: USER+
  - Input/behavior: Owner hoặc ADMIN
- **GET · `/api/payment/sse/order/{orderId}`**
  - Access: Special token
  - Input/behavior: Query `token`; owner hoặc ADMIN


Payment request không dùng Bean Validation; [`PaymentServiceImpl.validatePaymentRequest`](../../Back_end/src/main/java/com/example/web_bansach/module/payment/service/impl/PaymentServiceImpl.java) kiểm tra `orderId > 0`, amount > 0 và return URL không rỗng. Amount phải bằng `Order.totalAmount` và order phải thuộc principal, ở trạng thái `PENDING`.

Webhook chấp nhận transaction code từ `code`, `transactionId`, `paymentCode`, hoặc trích `SEP-?\d+` từ các trường nội dung; amount lấy từ `transferAmount` hoặc `amount`. Header phải khớp dạng `Apikey <configured-key>`. Webhook verification thất bại hiện trả HTTP 200 với `data.status=FAILED`; thiếu transaction/amount trả HTTP 400.

WebSocket/STOMP endpoint: `/ws`; application prefix `/app`; broker destinations `/topic` và `/queue`. Server phát các topic như `/topic/orders`, `/topic/users/{username}`, `/topic/payments`, `/topic/orders/{orderId}` trong [`RealtimeNotificationService`](../../Back_end/src/main/java/com/example/web_bansach/infrastructure/realtime/RealtimeNotificationService.java).

## 10. Response Structure

Application response:

```json
{
  "statusCode": 200,
  "message": "Thành công",
  "data": {},
  "timestamp": "2026-01-01T00:00:00",
  "path": null,
  "errors": null
}
```

Null fields bị bỏ khỏi JSON do `@JsonInclude(NON_NULL)`. Paginated `data`:

```json
{
  "content": [],
  "meta": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

Security 401/403 dùng schema khác:

```json
{
  "code": "AUTHENTICATION_ERROR",
  "message": "...",
  "status": 401,
  "timestamp": "...",
  "path": "/protected/path"
}
```

## 11. HTTP Status Codes và Error Mapping

- **200 · Query/update/delete; nhiều command auth**
- **201 · Một số create endpoint: book/order/review/voucher/category/author tùy controller**
- **400 · `BusinessException`, `ValidationException`, Bean Validation, invalid argument; webhook thiếu dữ liệu**
- **401 · Bad credentials hoặc chưa xác thực**
- **403 · Authenticated nhưng thiếu authority; invalid SSE token được ném `AccessDeniedException`**
- **404 · `ResourceNotFoundException`**
- **500 · Exception không được handler cụ thể xử lý**


Exact status của từng success path phụ thuộc controller; không nên suy ra mọi create đều trả 201.

## 12. Pagination & Filtering

- Page index là zero-based.
- Default phổ biến: `page=0`, `size=10`; voucher `my=20`, `claimed=100` là ngoại lệ.
- Books hỗ trợ keyword/category/author và sort field/direction.
- Authors/categories có search endpoint riêng.
- Không phát hiện global maximum page size; một số service chỉ kiểm tra page >= 0 và size > 0.

## 13. API Limitations & Unverified Findings

- Không có OpenAPI/Swagger spec hoặc generated client.
- Prefix và naming dùng cả tiếng Việt/Anh, singular/plural và `/api`/không `/api`.
- `UserAdminController` base path có trailing slash, method path có leading slash; effective mapping nên được regression-test.
- Review read APIs không public dù tên nghiệp vụ có thể gợi ý catalog public.
- Endpoint list trong `Endpoints` và method-level annotations được duy trì thủ công, có nguy cơ lệch nhau.
- Không có explicit rate limiting được phát hiện cho auth/webhook.
- Không phát hiện standardized correlation id trong response; request id chỉ nằm trong logging context.
- Runtime serialization, content negotiation và behavior với malformed payload là **UNVERIFIED** ngoài các test hiện có.
