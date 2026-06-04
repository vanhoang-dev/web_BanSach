# Web Bán Sách - Backend API

## 1. Giới thiệu dự án

**Web Bán Sách** là hệ thống backend REST API cho một website thương mại điện tử bán sách. Dự án được xây dựng bằng **Java Spring Boot**, sử dụng **MySQL** làm cơ sở dữ liệu chính, hỗ trợ xác thực bằng **JWT**, phân quyền người dùng theo vai trò **USER** và **ADMIN**, đồng thời tích hợp các dịch vụ bên ngoài như **Cloudinary** để lưu trữ hình ảnh sách và **SePay** để xử lý thanh toán.

Backend cung cấp API cho cả phía người dùng và quản trị viên. Người dùng có thể đăng ký, đăng nhập, xem sách, tìm kiếm sách, thêm sách vào giỏ hàng, đặt hàng, thanh toán, đánh giá sách, lưu sách yêu thích và sử dụng voucher. Quản trị viên có thể quản lý sách, danh mục, tác giả, người dùng, đơn hàng, tồn kho, voucher, đánh giá và theo dõi thanh toán.

Dự án được thiết kế theo kiến trúc nhiều tầng, tách biệt rõ giữa Controller, Service, Repository, Entity, DTO và Security, giúp hệ thống dễ bảo trì, mở rộng và kiểm thử.

---

## 2. Mục tiêu dự án

Dự án hướng đến việc xây dựng một hệ thống backend hoàn chỉnh cho website bán sách, bao gồm các nghiệp vụ chính của một nền tảng thương mại điện tử cơ bản.

Các mục tiêu chính:

* Xây dựng hệ thống REST API cho website bán sách.
* Hỗ trợ đăng ký, đăng nhập và xác thực người dùng bằng JWT.
* Phân quyền chức năng giữa USER và ADMIN.
* Cho phép người dùng xem, tìm kiếm và mua sách.
* Cho phép người dùng quản lý giỏ hàng, đặt hàng và thanh toán.
* Cho phép quản trị viên quản lý dữ liệu sách, danh mục, tác giả, người dùng, đơn hàng, voucher và tồn kho.
* Tích hợp Cloudinary để upload và lưu trữ hình ảnh sách.
* Tích hợp SePay để hỗ trợ thanh toán.
* Chuẩn hóa response, exception handling và validation.
* Hỗ trợ kiểm thử backend trước khi deploy.

---

## 3. Công nghệ sử dụng

| Thành phần     | Công nghệ                                 |
| -------------- | ----------------------------------------- |
| Ngôn ngữ       | Java 17                                   |
| Framework      | Spring Boot                               |
| Web/API        | Spring Web / Spring MVC                   |
| ORM            | Spring Data JPA / Hibernate               |
| Database       | MySQL                                     |
| Security       | Spring Security                           |
| Authentication | JWT                                       |
| Validation     | Spring Validation                         |
| Upload file    | Cloudinary                                |
| Payment        | SePay                                     |
| Email          | Spring Mail                               |
| Realtime       | WebSocket                                 |
| Build tool     | Maven                                     |
| Container      | Docker, Docker Compose                    |
| Testing        | JUnit, Mockito, Spring Boot Test, MockMvc |

---

## 4. Kiến trúc tổng quan

Dự án được tổ chức theo mô hình nhiều tầng:

```text
Controller → Service → Repository → Database
```

Trong đó:

* **Controller**: tiếp nhận request từ client, gọi service xử lý nghiệp vụ và trả response.
* **Service**: chứa logic nghiệp vụ chính của hệ thống.
* **Repository**: thao tác với database thông qua Spring Data JPA.
* **Entity**: ánh xạ các bảng trong cơ sở dữ liệu.
* **DTO**: định nghĩa dữ liệu request và response.
* **Security**: xử lý xác thực, phân quyền và JWT.
* **Infrastructure**: tích hợp các dịch vụ ngoài như Cloudinary, SePay, Email và WebSocket.
* **Common**: chứa response dùng chung, exception, config, constant và utility.

---

## 5. Cấu trúc thư mục chính

```text
src/main/java/com/example/web_bansach
├── common
│   ├── config
│   ├── constant
│   ├── exception
│   ├── response
│   └── util
│
├── infrastructure
│   ├── cloudinary
│   ├── external
│   ├── file
│   ├── payment
│   ├── persistence
│   └── realtime
│
├── module
│   ├── auth
│   ├── user
│   ├── book
│   ├── category
│   ├── author
│   ├── cart
│   ├── order
│   ├── payment
│   ├── inventory
│   ├── pricing
│   ├── review
│   ├── voucher
│   └── wishlist
│
└── security
    ├── config
    ├── handler
    ├── jwt
    ├── principal
    └── service
```

---

## 6. Phân quyền hệ thống

Hệ thống có 3 nhóm truy cập chính:

| Nhóm   | Mô tả                                                                                                       |
| ------ | ----------------------------------------------------------------------------------------------------------- |
| Public | Khách chưa đăng nhập, có thể xem thông tin công khai và đăng ký/đăng nhập                                   |
| USER   | Người dùng đã đăng nhập, có thể mua sách, quản lý giỏ hàng, đặt hàng, thanh toán, đánh giá và dùng wishlist |
| ADMIN  | Quản trị viên, có quyền quản lý dữ liệu hệ thống như sách, người dùng, đơn hàng, tồn kho, voucher và review |

---

# 7. Chức năng chính của hệ thống

## 7.1. Chức năng Public - Khách chưa đăng nhập

Khách chưa đăng nhập là người truy cập website nhưng chưa có tài khoản hoặc chưa đăng nhập. Nhóm người dùng này có thể xem dữ liệu công khai và thực hiện các thao tác không yêu cầu xác thực.

Các chức năng chính:

* Đăng ký tài khoản mới.
* Đăng nhập vào hệ thống.
* Refresh token nếu hệ thống hỗ trợ.
* Gửi yêu cầu quên mật khẩu.
* Đặt lại mật khẩu.
* Xem danh sách sách đang được bán.
* Xem chi tiết một cuốn sách.
* Tìm kiếm sách theo từ khóa.
* Xem danh sách danh mục sách.
* Xem chi tiết danh mục sách.
* Tìm kiếm danh mục.
* Xem danh sách tác giả.
* Xem chi tiết tác giả.
* Tìm kiếm tác giả.
* Xem đánh giá của một cuốn sách.
* Xem thống kê đánh giá của một cuốn sách.

Một số API liên quan:

```text
POST /tai-khoan/dang-ky
POST /tai-khoan/dang-nhap
POST /tai-khoan/refresh-token
POST /tai-khoan/quen-mat-khau
POST /tai-khoan/dat-lai-mat-khau

GET /user/books
GET /user/books/{id}

GET /api/categories
GET /api/categories/{id}
GET /api/categories/search

GET /api/authors
GET /api/authors/{id}
GET /api/authors/search

GET /user/reviews/book/{bookId}
GET /user/reviews/book/{bookId}/stats
```

---

## 7.2. Chức năng USER - Người dùng đã đăng nhập

USER là người dùng đã có tài khoản và đăng nhập vào hệ thống. Người dùng có thể mua sách, quản lý thông tin cá nhân, giỏ hàng, đơn hàng, thanh toán, wishlist, review và voucher.

---

### 7.2.1. Quản lý tài khoản cá nhân

Người dùng có thể quản lý thông tin cá nhân của mình.

Chức năng chính:

* Xem thông tin tài khoản cá nhân.
* Cập nhật hồ sơ cá nhân.
* Đổi mật khẩu.
* Đăng xuất khỏi hệ thống.

API liên quan:

```text
GET /user/me
PUT /user/update-profile
POST /user/change-password
POST /tai-khoan/dang-xuat
```

---

### 7.2.2. Xem và tìm kiếm sách

Người dùng có thể duyệt danh sách sách và xem thông tin chi tiết của từng cuốn sách.

Chức năng chính:

* Xem danh sách sách.
* Xem chi tiết sách.
* Tìm kiếm sách theo tên hoặc từ khóa.
* Lọc sách theo danh mục, tác giả hoặc thông tin liên quan.
* Xem danh sách sách có phân trang.

API liên quan:

```text
GET /user/books
GET /user/books/{id}
```

---

### 7.2.3. Giỏ hàng

Người dùng có thể thêm sách vào giỏ hàng trước khi đặt hàng.

Chức năng chính:

* Xem giỏ hàng hiện tại.
* Thêm sách vào giỏ hàng.
* Cập nhật số lượng sách trong giỏ hàng.
* Xóa một sách khỏi giỏ hàng.
* Xóa toàn bộ giỏ hàng.
* Tính tổng tiền trong giỏ hàng.
* Kiểm tra số lượng tồn kho trước khi đặt hàng.

API liên quan:

```text
GET /user/cart
POST /user/cart/items
PUT /user/cart/items/{itemId}
DELETE /user/cart/items/{itemId}
DELETE /user/cart/clear
```

---

### 7.2.4. Đặt hàng

Người dùng có thể tạo đơn hàng từ giỏ hàng và theo dõi trạng thái xử lý đơn hàng.

Chức năng chính:

* Tạo đơn hàng từ giỏ hàng.
* Xem danh sách đơn hàng của bản thân.
* Xem chi tiết một đơn hàng.
* Hủy đơn hàng nếu đơn hàng còn ở trạng thái cho phép hủy.
* Theo dõi trạng thái xử lý đơn hàng.

API liên quan:

```text
POST /user/orders
GET /user/orders
GET /user/orders/{id}
PUT /user/orders/{id}/cancel
```

---

### 7.2.5. Thanh toán

Người dùng có thể thanh toán đơn hàng thông qua cổng thanh toán được tích hợp.

Chức năng chính:

* Khởi tạo thanh toán cho đơn hàng.
* Nhận thông tin thanh toán hoặc mã QR thanh toán.
* Kiểm tra trạng thái thanh toán.
* Nhận kết quả thanh toán sau khi hệ thống xử lý callback/webhook.

API liên quan:

```text
POST /api/payment/initiate
GET /api/payment/status/{paymentId}
```

---

### 7.2.6. Wishlist - Danh sách yêu thích

Người dùng có thể lưu lại các cuốn sách yêu thích để xem hoặc mua sau.

Chức năng chính:

* Thêm sách vào danh sách yêu thích.
* Xóa sách khỏi danh sách yêu thích.
* Kiểm tra một cuốn sách đã có trong wishlist hay chưa.
* Xem toàn bộ danh sách yêu thích.
* Đếm số lượng sách trong wishlist.
* Xóa toàn bộ wishlist.

API liên quan:

```text
POST /user/wishlist/books/{bookId}
DELETE /user/wishlist/books/{bookId}
GET /user/wishlist/books/{bookId}/check
GET /user/wishlist
GET /user/wishlist/count
DELETE /user/wishlist/clear
```

---

### 7.2.7. Review - Đánh giá sách

Người dùng có thể đánh giá sách, cập nhật đánh giá của bản thân và xem đánh giá của các người dùng khác.

Chức năng chính:

* Tạo đánh giá cho sách.
* Cập nhật đánh giá của bản thân.
* Xóa đánh giá của bản thân.
* Xem đánh giá của mình trên một cuốn sách.
* Xem danh sách đánh giá của một cuốn sách.
* Xem thống kê đánh giá của một cuốn sách.

API liên quan:

```text
POST /user/reviews
PUT /user/reviews/{reviewId}
DELETE /user/reviews/{reviewId}
GET /user/reviews/book/{bookId}/my-review
GET /user/reviews/book/{bookId}
GET /user/reviews/book/{bookId}/stats
```

---

### 7.2.8. Voucher - Mã giảm giá

Người dùng có thể xem và sử dụng mã giảm giá còn hiệu lực trong hệ thống.

Chức năng chính:

* Xem danh sách voucher khả dụng.
* Tìm voucher theo mã.
* Áp dụng voucher khi đặt hàng nếu thỏa điều kiện.

API liên quan:

```text
GET /user/vouchers
GET /user/vouchers/code/{code}
```

---

### 7.2.9. Kiểm tra tồn kho

Người dùng có thể xem số lượng tồn kho của một cuốn sách trước khi thêm vào giỏ hàng hoặc đặt hàng.

Chức năng chính:

* Xem số lượng tồn kho của một cuốn sách.
* Kiểm tra sách còn hàng hoặc hết hàng.

API liên quan:

```text
GET /user/inventory/book/{bookId}
```

---

## 7.3. Chức năng ADMIN - Quản trị viên

ADMIN là người quản trị hệ thống, có quyền quản lý dữ liệu vận hành của website bán sách.

---

### 7.3.1. Quản lý người dùng

Admin có thể quản lý tài khoản người dùng trong hệ thống.

Chức năng chính:

* Xem danh sách người dùng.
* Xem chi tiết một người dùng.
* Cập nhật thông tin người dùng.
* Xóa người dùng khỏi hệ thống.
* Quản lý thông tin liên quan đến tài khoản người dùng.

API liên quan:

```text
GET /user_for_admin/all
GET /user_for_admin/user/{id}
PUT /user_for_admin/user/{id}
DELETE /user_for_admin/user/delete/{id}
```

---

### 7.3.2. Quản lý sách

Admin có thể quản lý toàn bộ dữ liệu sách được bán trên website.

Chức năng chính:

* Xem danh sách sách trong trang quản trị.
* Xem chi tiết một cuốn sách.
* Thêm sách mới.
* Cập nhật thông tin sách.
* Xóa sách.
* Upload ảnh sách.
* Quản lý giá bán, số lượng, danh mục, tác giả và mô tả sách.

API liên quan:

```text
GET /admin/books
GET /admin/books/{id}
POST /admin/books/create-book
PUT /admin/books/update-book/{id}
DELETE /admin/books/delete-book/{id}
```

---

### 7.3.3. Quản lý danh mục sách

Admin có thể quản lý danh mục dùng để phân loại sách.

Chức năng chính:

* Xem danh sách danh mục.
* Xem chi tiết danh mục.
* Thêm danh mục mới.
* Cập nhật danh mục.
* Xóa danh mục.
* Kích hoạt danh mục.
* Vô hiệu hóa danh mục.

API liên quan:

```text
GET /api/admin/categories
POST /api/admin/categories
PUT /api/admin/categories/{id}
DELETE /api/admin/categories/{id}
PUT /api/admin/categories/{id}/activate
PUT /api/admin/categories/{id}/deactivate
```

---

### 7.3.4. Quản lý tác giả

Admin có thể quản lý thông tin tác giả của các đầu sách.

Chức năng chính:

* Xem danh sách tác giả.
* Xem chi tiết tác giả.
* Thêm tác giả mới.
* Cập nhật thông tin tác giả.
* Xóa tác giả.

API liên quan:

```text
GET /admin/authors
POST /admin/authors
PUT /admin/authors/{id}
DELETE /admin/authors/{id}
```

---

### 7.3.5. Quản lý đơn hàng

Admin có thể theo dõi và xử lý đơn hàng của toàn bộ người dùng trong hệ thống.

Chức năng chính:

* Xem danh sách tất cả đơn hàng.
* Xem chi tiết đơn hàng.
* Cập nhật trạng thái đơn hàng.
* Hủy đơn hàng.
* Theo dõi quá trình xử lý đơn hàng.

API liên quan:

```text
GET /admin/orders
GET /admin/orders/{id}
PUT /admin/orders/{id}/status
PUT /admin/orders/{id}/cancel
```

---

### 7.3.6. Quản lý thanh toán

Admin có thể theo dõi trạng thái thanh toán và xử lý các tình huống liên quan đến giao dịch.

Chức năng chính:

* Kiểm tra trạng thái thanh toán.
* Theo dõi kết quả thanh toán từ SePay.
* Xử lý webhook thanh toán.
* Hoàn tiền nếu hệ thống hoặc gateway hỗ trợ.
* Kiểm tra các giao dịch lỗi hoặc giao dịch chưa hoàn tất.

API liên quan:

```text
POST /api/payment/sepay-webhook
GET /api/payment/status/{paymentId}
POST /api/payment/refund/{paymentId}
```

---

### 7.3.7. Quản lý kho hàng

Admin có thể quản lý số lượng tồn kho của từng cuốn sách.

Chức năng chính:

* Xem danh sách tồn kho.
* Xem tồn kho theo từng sách.
* Set số lượng tồn kho.
* Điều chỉnh tăng hoặc giảm tồn kho.
* Đối soát lại số lượng tồn kho.
* Kiểm soát tình trạng còn hàng hoặc hết hàng.

API liên quan:

```text
GET /admin/inventory
PUT /admin/inventory/{id}/set/{quantity}
POST /admin/inventory/{id}/adjust/{delta}
PUT /admin/inventory/{id}/reconcile/{quantity}
```

---

### 7.3.8. Quản lý đánh giá

Admin có thể theo dõi và xử lý các đánh giá của người dùng.

Chức năng chính:

* Xem danh sách đánh giá theo người dùng.
* Xem chi tiết đánh giá.
* Xóa đánh giá không phù hợp.
* Quản lý nội dung đánh giá trên hệ thống.

API liên quan:

```text
GET /admin/reviews/user/{userId}
GET /admin/reviews/{reviewId}
DELETE /admin/reviews/{reviewId}
```

---

### 7.3.9. Quản lý voucher

Admin có thể tạo và quản lý các mã giảm giá trong hệ thống.

Chức năng chính:

* Xem danh sách voucher.
* Xem chi tiết voucher.
* Tạo voucher mới.
* Cập nhật voucher.
* Xóa voucher.
* Xem danh sách voucher đã hết hạn.
* Quản lý thời gian áp dụng và điều kiện sử dụng voucher.

API liên quan:

```text
GET /admin/vouchers
GET /admin/vouchers/{voucherId}
GET /admin/vouchers/expired
POST /admin/vouchers
PUT /admin/vouchers/{voucherId}
DELETE /admin/vouchers/{voucherId}
```

---

## 7.4. Tóm tắt phân quyền chức năng

| Nhóm chức năng      | Public               | USER                     | ADMIN           |
| ------------------- | -------------------- | ------------------------ | --------------- |
| Đăng ký / Đăng nhập | Có                   | Có                       | Có              |
| Xem danh sách sách  | Có                   | Có                       | Có              |
| Xem chi tiết sách   | Có                   | Có                       | Có              |
| Tìm kiếm sách       | Có                   | Có                       | Có              |
| Quản lý giỏ hàng    | Không                | Có                       | Không           |
| Đặt hàng            | Không                | Có                       | Không           |
| Thanh toán          | Không                | Có                       | Theo dõi/Xử lý  |
| Wishlist            | Không                | Có                       | Không           |
| Review sách         | Xem                  | Tạo/Sửa/Xóa của bản thân | Quản lý/Xóa     |
| Quản lý sách        | Không                | Không                    | Có              |
| Quản lý danh mục    | Không                | Không                    | Có              |
| Quản lý tác giả     | Không                | Không                    | Có              |
| Quản lý người dùng  | Không                | Không                    | Có              |
| Quản lý đơn hàng    | Không                | Xem/Hủy đơn của bản thân | Quản lý toàn bộ |
| Quản lý tồn kho     | Xem cơ bản           | Xem tồn kho sách         | Có              |
| Quản lý voucher     | Xem voucher khả dụng | Xem/Sử dụng              | Có              |

---

## 8. Response và xử lý lỗi

Dự án có lớp response dùng chung để chuẩn hóa dữ liệu trả về cho frontend.

Một số thành phần response:

```text
ApiResponse
PageResponse
PaginationMeta
```

Dự án cũng có hệ thống exception riêng:

```text
BusinessException
ResourceNotFoundException
UnauthorizedException
ForbiddenException
ValidationException
JwtAuthenticationException
GlobalExceptionHandler
```

Mục tiêu của phần xử lý lỗi là:

* Trả response thống nhất cho frontend.
* Không trả lỗi hệ thống trực tiếp cho client.
* Không làm lộ stack trace.
* Phân biệt rõ lỗi validate, lỗi không tìm thấy dữ liệu, lỗi chưa đăng nhập và lỗi không đủ quyền.
* Giúp frontend dễ hiển thị thông báo lỗi cho người dùng.

---

## 9. Tích hợp dịch vụ ngoài

## 9.1. Cloudinary

Cloudinary được sử dụng để upload và lưu trữ hình ảnh sách.

Các cấu hình cần có:

```properties
cloudinary.cloud-name=
cloudinary.api-key=
cloudinary.api-secret=
```

---

## 9.2. SePay

SePay được sử dụng để hỗ trợ thanh toán đơn hàng.

Các cấu hình cần có:

```properties
sepay.bank-code=
sepay.account-number=
sepay.account-name=
sepay.webhook-api-key=
sepay.return-url=
sepay.api-key=
sepay.status-api-url=
sepay.refund-api-url=
```

---

## 9.3. Email

Dự án có cấu hình Spring Mail để phục vụ các chức năng như quên mật khẩu, gửi thông báo hoặc xác nhận nếu được triển khai.

---

## 9.4. WebSocket

Dự án có cấu hình WebSocket để hỗ trợ realtime notification, ví dụ như thông báo trạng thái đơn hàng hoặc thông báo hệ thống.

---

## 10. Cấu hình môi trường

File cấu hình chính:

```text
src/main/resources/application.properties
```

Một số biến môi trường quan trọng:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/DUAN_WEBBANSACH?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
spring.datasource.username=root
spring.datasource.password=123456

app.jwt.secret=your-secret-key-change-this-in-production
app.jwt.expiration=1800000
app.jwt.refresh-token-secret=your-refresh-token-secret-change-this-in-production
app.jwt.refresh-token-expiration=2592000000

cloudinary.cloud-name=
cloudinary.api-key=
cloudinary.api-secret=

sepay.bank-code=MB
sepay.account-number=
sepay.account-name=
sepay.webhook-api-key=
sepay.return-url=http://localhost:3000/payment-result
```

Lưu ý quan trọng:

```text
Không nên commit secret thật, API key, database password hoặc JWT secret lên GitHub.
```

---

## 11. Cách chạy dự án local

## 11.1. Yêu cầu môi trường

Cần cài đặt:

```text
Java 17
Maven
MySQL
```

---

## 11.2. Tạo database

Tạo database trong MySQL:

```sql
CREATE DATABASE DUAN_WEBBANSACH;
```

Sau đó kiểm tra lại cấu hình database trong:

```text
src/main/resources/application.properties
```

---

## 11.3. Chạy bằng Maven

```bash
mvn clean install
mvn spring-boot:run
```

Hoặc nếu dùng Maven Wrapper trên Windows:

```bat
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

Backend mặc định chạy ở:

```text
http://localhost:8080
```

---

## 12. Chạy bằng Docker

Dự án có sẵn:

```text
Dockerfile
docker-compose.yml
```

Build file JAR trước:

```bash
mvn clean package
```

Sau đó chạy Docker Compose:

```bash
docker compose up --build
```

Docker Compose sẽ khởi tạo:

```text
MySQL container
Spring Boot app container
```

---

## 13. Testing

Dự án cần được kiểm thử backend trước khi deploy để đảm bảo các chức năng hoạt động ổn định, đúng nghiệp vụ và không phát sinh lỗi nghiêm trọng.

Các loại test nên bổ sung:

```text
Unit Test
Service Test
Repository Test
Controller Test
Integration Test
Security Test
API Smoke Test
Regression Test
```

Công cụ testing phù hợp:

| Loại test        | Công cụ                          |
| ---------------- | -------------------------------- |
| Unit Test        | JUnit 5, Mockito                 |
| Controller Test  | MockMvc                          |
| Repository Test  | DataJpaTest                      |
| Integration Test | Spring Boot Test, Testcontainers |
| API Test         | Postman, Newman, REST Assured    |
| Security Test    | Spring Security Test             |
| Coverage         | JaCoCo                           |

Các module nên ưu tiên test trước khi deploy:

```text
Authentication
Authorization / Role
Book
Cart
Order
Payment
Admin
Validation
Database Transaction
```

Một số test case quan trọng:

```text
Đăng ký tài khoản thành công
Đăng ký trùng email
Đăng nhập đúng tài khoản
Đăng nhập sai mật khẩu
Gọi API cần token nhưng không gửi token
USER truy cập API ADMIN
Admin thêm sách
Admin cập nhật sách
Thêm sách vào giỏ hàng
Cập nhật số lượng giỏ hàng
Không cho thêm vượt tồn kho
Không cho update số lượng vượt tồn kho
Tạo đơn hàng thành công
Không cho tạo đơn khi giỏ hàng rỗng
Thanh toán thành công
Thanh toán thất bại
Webhook thanh toán hợp lệ
Không để lộ stack trace khi API lỗi
```

Chạy test:

```bash
mvn test
```

---

## 14. Các API chính theo nhóm

| Module    | API chính                                        |
| --------- | ------------------------------------------------ |
| Auth      | `/tai-khoan/**`                                  |
| User      | `/user/**`, `/user_for_admin/**`                 |
| Book      | `/user/books/**`, `/admin/books/**`              |
| Category  | `/api/categories/**`, `/api/admin/categories/**` |
| Author    | `/api/authors/**`, `/admin/authors/**`           |
| Cart      | `/user/cart/**`                                  |
| Order     | `/user/orders/**`, `/admin/orders/**`            |
| Payment   | `/api/payment/**`                                |
| Inventory | `/user/inventory/**`, `/admin/inventory/**`      |
| Review    | `/user/reviews/**`, `/admin/reviews/**`          |
| Voucher   | `/user/vouchers/**`, `/admin/vouchers/**`        |
| Wishlist  | `/user/wishlist/**`                              |

---

## 15. Các vấn đề cần kiểm tra trước deploy

Trước khi deploy, cần kiểm tra kỹ các vấn đề sau:

```text
Không để password database mặc định trong application.properties
Không để JWT secret mặc định
Không commit API key Cloudinary/SePay
Bật CORS đúng domain frontend
Kiểm tra phân quyền USER/ADMIN
Kiểm tra API cần token phải trả 401 nếu thiếu token
Kiểm tra USER không truy cập được API ADMIN
Kiểm tra payment webhook
Kiểm tra payment thành công/thất bại
Kiểm tra transaction khi tạo đơn hàng
Kiểm tra không cho đặt hàng vượt tồn kho
Kiểm tra không cho cập nhật giỏ hàng vượt tồn kho
Kiểm tra không trả stack trace ra client
Chạy đầy đủ test trước deploy
```

Một số rủi ro kỹ thuật cần đặc biệt chú ý:

```text
Role trong Spring Security cần thống nhất giữa hasRole và hasAuthority.
JWT subject cần thống nhất là username, email hoặc userId.
Payment thành công cần đồng bộ trạng thái với Order.
Cart update quantity cần kiểm tra tồn kho.
Test environment cần cấu hình riêng để tránh lỗi Cloudinary, SePay hoặc database thật.
```

---

## 16. Hướng phát triển tiếp theo

Dự án có thể mở rộng thêm các chức năng sau:

* Dashboard thống kê doanh thu cho admin.
* Thống kê sách bán chạy.
* Quản lý vận chuyển đơn hàng.
* Gửi email xác nhận đơn hàng.
* Gửi thông báo realtime khi trạng thái đơn hàng thay đổi.
* Tích hợp Swagger/OpenAPI để tài liệu hóa API.
* Bổ sung automation testing cho backend.
* Tích hợp CI/CD để chạy test tự động trước khi deploy.
* Cải thiện logging và monitoring production.
* Phân quyền chi tiết hơn cho admin, staff hoặc manager.

---

## 17. Tóm tắt

Dự án **Web Bán Sách Backend** là một hệ thống REST API tương đối đầy đủ cho website thương mại điện tử bán sách. Hệ thống hỗ trợ các nghiệp vụ chính như quản lý tài khoản, xác thực JWT, phân quyền USER/ADMIN, quản lý sách, danh mục, tác giả, giỏ hàng, đơn hàng, thanh toán, tồn kho, đánh giá, voucher và wishlist.

Dự án phù hợp để phát triển thành một hệ thống bán sách hoàn chỉnh với frontend riêng. Trước khi deploy, cần tập trung kiểm thử kỹ các phần quan trọng như authentication, authorization, cart, order, payment, transaction, validation và security.
