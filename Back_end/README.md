# Back_end

Cây thư mục bên dưới giữ nguyên cấu trúc dự án backend, nhưng mình thêm chú thích ngay sau từng class/file để bạn biết nó dùng để làm gì trong hệ thống.

```text
src/main/java/com/example/web_bansach
├── common/                                      → Phần dùng chung cho toàn dự án
│   ├── config/
│   │   ├── CorsConfig.java                      → Cấu hình CORS cho frontend gọi API
│   │   ├── SwaggerConfig.java                   → Cấu hình tài liệu API Swagger/OpenAPI
│   │   ├── JacksonConfig.java                   → Cấu hình serialize/deserialize JSON
│   │   └── WebMvcConfig.java                    → Cấu hình MVC chung cho ứng dụng
│   ├── constant/
│   │   ├── AppConstants.java                    → Hằng số ứng dụng như phân trang, upload, format
│   │   ├── RoleConstants.java                   → Tên các role như ADMIN, USER
│   │   ├── SecurityConstants.java               → Hằng số liên quan JWT và bảo mật
│   │   └── MessageConstants.java                → Các message lỗi/thành công dùng chung
│   ├── exception/
│   │   ├── BusinessException.java               → Lỗi nghiệp vụ do logic gây ra
│   │   ├── ResourceNotFoundException.java        → Lỗi không tìm thấy dữ liệu
│   │   ├── UnauthorizedException.java            → Lỗi chưa xác thực / token sai
│   │   ├── ForbiddenException.java               → Lỗi không đủ quyền truy cập
│   │   ├── ValidationException.java              → Lỗi validate dữ liệu đầu vào
│   │   ├── ErrorResponse.java                    → Cấu trúc response lỗi
│   │   └── GlobalExceptionHandler.java           → Bắt và chuẩn hóa lỗi toàn hệ thống
│   ├── response/
│   │   ├── ApiResponse.java                      → Wrapper response chuẩn cho API
│   │   ├── PageResponse.java                     → Wrapper cho dữ liệu phân trang
│   │   └── PaginationMeta.java                   → Metadata của phân trang
│   ├── util/
│   │   ├── DateUtils.java                        → Hàm tiện ích xử lý ngày giờ
│   │   ├── SlugUtils.java                        → Hàm tạo slug / xử lý chuỗi
│   │   ├── CurrencyUtils.java                    → Hàm tiện ích xử lý tiền tệ
│   │   └── FileUtils.java                        → Hàm tiện ích xử lý file
│   └── mapper/
│       └── BaseMapper.java                       → Interface nền để chuẩn hóa mapping entity/DTO
│
├── security/                                    → Phần xác thực và phân quyền
│   ├── config/
│   │   └── SecurityConfiguration.java            → Cấu hình Spring Security cho toàn app
│   ├── jwt/
│   │   ├── JwtAuthenticationFilter.java          → Filter đọc JWT từ request
│   │   ├── JwtTokenProvider.java                 → Tạo, kiểm tra và phân tích JWT
│   │   └── JwtProperties.java                    → Cấu hình secret, thời gian sống của token
│   ├── principal/
│   │   └── UserPrincipal.java                    → Đại diện user đang đăng nhập trong SecurityContext
│   ├── service/
│   │   └── CustomUserDetailsService.java          → Load user từ database cho Spring Security
│   └── handler/
│       ├── JwtAuthenticationEntryPoint.java      → Trả lỗi khi chưa đăng nhập
│       └── JwtAccessDeniedHandler.java           → Trả lỗi khi không đủ quyền
│
├── infrastructure/                               → Tầng tích hợp với dịch vụ ngoài và hạ tầng
│   ├── cloudinary/
│   │   ├── CloudinaryConfig.java                 → Cấu hình Cloudinary client
│   │   └── CloudinaryFileStorageService.java     → Upload/xóa ảnh trên Cloudinary
│   ├── file/
│   │   ├── FileUploadService.java                → Abstraction cho việc upload file
│   │   └── impl/
│   │       └── CloudinaryFileUploadService.java   → Triển khai upload file bằng Cloudinary
│   ├── payment/
│   │   ├── PaymentGateway.java                   → Interface chiến lược thanh toán
│   │   ├── VNPayGateway.java                     → Triển khai thanh toán VNPay
│   │   ├── MomoGateway.java                      → Triển khai thanh toán Momo
│   │   └── PaymentStrategyFactory.java           → Chọn gateway theo phương thức thanh toán
│   ├── messaging/
│   │   └── NotificationProducer.java             → Gửi thông báo ra hệ thống queue nếu có
│   ├── persistence/
│   │   ├── JpaConfig.java                        → Cấu hình JPA repositories
│   │   ├── AuditingConfig.java                   → Bật audit createdAt/updatedAt
│   │   └── BaseEntity.java                       → Base entity chứa các trường audit chung
│   └── external/
│       └── EmailSender.java                      → Gửi email qua dịch vụ ngoài
│
└── module/                                       → Các module nghiệp vụ chính của dự án
    ├── auth/
    │   ├── controller/
    │   │   └── AuthController.java               → API đăng ký, đăng nhập, xác thực
    │   ├── service/
    │   │   ├── AuthService.java                  → Interface cho nghiệp vụ auth
    │   │   ├── TokenService.java                 → Interface xử lý token/JWT
    │   │   └── impl/
    │   │       ├── AuthServiceImpl.java          → Xử lý đăng ký và đăng nhập
    │   │       └── TokenServiceImpl.java         → Tạo và kiểm tra token
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── LoginRequest.java             → Dữ liệu request đăng nhập
    │   │   │   └── RegisterRequest.java          → Dữ liệu request đăng ký
    │   │   └── response/
    │   │       └── AuthResponse.java             → Dữ liệu trả về sau đăng nhập
    │   ├── mapper/
    │   │   └── AuthMapper.java                   → Chuyển đổi dữ liệu auth giữa entity và DTO
    │   └── validator/
    │       └── AuthValidator.java                → Kiểm tra dữ liệu đăng ký/đăng nhập
    │
    ├── user/
    │   ├── controller/
    │   │   ├── UserProfileController.java         → API cho user tự xem/sửa hồ sơ
    │   │   └── UserAdminController.java          → API admin quản lý user
    │   ├── service/
    │   │   ├── UserProfileService.java           → Nghiệp vụ hồ sơ cá nhân
    │   │   ├── UserAdminQueryService.java        → Đọc danh sách/chi tiết user
    │   │   ├── UserAdminCommandService.java      → Tạo/sửa/khóa user
    │   │   ├── UserValidationService.java        → Validate các rule của user
    │   │   └── impl/
    │   │       ├── UserProfileServiceImpl.java   → Hiện thực nghiệp vụ hồ sơ cá nhân
    │   │       ├── UserAdminQueryServiceImpl.java→ Hiện thực đọc dữ liệu admin
    │   │       ├── UserAdminCommandServiceImpl.java → Hiện thực ghi dữ liệu admin
    │   │       └── UserValidationServiceImpl.java→ Hiện thực validate user
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── UpdateUserProfileRequest.java  → Request cập nhật hồ sơ cá nhân
    │   │   │   ├── ChangePasswordRequest.java     → Request đổi mật khẩu
    │   │   │   ├── CreateUserRequest.java        → Request tạo user
    │   │   │   ├── UpdateUserRequest.java        → Request update user
    │   │   │   └── UpdateUserStatusRequest.java  → Request đổi trạng thái user
    │   │   └── response/
    │   │       ├── UserProfileResponse.java      → Response hồ sơ cá nhân
    │   │       ├── UserAdminResponse.java        → Response danh sách user cho admin
    │   │       └── UserDetailResponse.java       → Response chi tiết user
    │   ├── entity/
    │   │   └── User.java                         → Entity user ánh xạ database
    │   ├── repository/
    │   │   └── UserRepository.java               → Truy vấn dữ liệu user
    │   ├── mapper/
    │   │   └── UserMapper.java                   → Map Users sang response DTO
    │   └── validator/
    │       └── UserValidator.java                → Validate dữ liệu user
    │
    ├── book/
    │   ├── controller/
    │   │   ├── BookPublicController.java         → API xem sách cho khách/user
    │   │   └── BookAdminController.java          → API quản lý sách cho admin
    │   ├── service/
    │   │   ├── BookQueryService.java             → Interface đọc dữ liệu sách
    │   │   ├── BookCommandService.java           → Interface ghi dữ liệu sách
    │   │   ├── BookApprovalService.java          → Interface duyệt/từ chối sách
    │   │   ├── BookValidationService.java        → Interface validate sách
    │   │   └── impl/
    │   │       ├── BookQueryServiceImpl.java     → Đọc danh sách/chi tiết/tìm kiếm sách
    │   │       ├── BookCommandServiceImpl.java   → Tạo/sửa/xóa sách
    │   │       ├── BookApprovalServiceImpl.java  → Duyệt hoặc từ chối sách
    │   │       └── BookValidationServiceImpl.java → Validate dữ liệu sách
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateBookRequest.java       → Request tạo sách
    │   │   │   ├── UpdateBookRequest.java       → Request cập nhật sách
    │   │   │   ├── BookSearchRequest.java       → Request tìm kiếm/lọc sách
    │   │   │   └── RejectBookRequest.java       → Request từ chối sách
    │   │   └── response/
    │   │       ├── BookResponse.java            → Response sách cho user
    │   │       ├── BookDetailResponse.java      → Response chi tiết sách
    │   │       └── BookAdminResponse.java       → Response sách cho admin
    │   ├── entity/
    │   │   └── Book.java                        → Entity sách
    │   ├── repository/
    │   │   └── BookRepository.java              → Truy vấn DB sách
    │   ├── mapper/
    │   │   └── BookMapper.java                  → Map Book sang DTO
    │   ├── validator/
    │   │   └── BookValidator.java               → Validate rule sách
    │   └── specification/
    │       └── BookSpecification.java           → Query động cho search/filter sách
    │
    ├── author/
    │   ├── controller/
    │   │   ├── AuthorPublicController.java      → API xem tác giả
    │   │   └── AuthorAdminController.java       → API admin quản lý tác giả
    │   ├── service/
    │   │   ├── AuthorQueryService.java          → Interface đọc dữ liệu tác giả
    │   │   ├── AuthorCommandService.java        → Interface ghi dữ liệu tác giả
    │   │   ├── AuthorValidationService.java     → Interface validate tác giả
    │   │   └── impl/
    │   │       ├── AuthorQueryServiceImpl.java  → Đọc danh sách tác giả
    │   │       ├── AuthorCommandServiceImpl.java→ Tạo/sửa/xóa tác giả
    │   │       └── AuthorValidationServiceImpl.java → Validate dữ liệu tác giả
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateAuthorRequest.java     → Request tạo tác giả
    │   │   │   └── UpdateAuthorRequest.java     → Request cập nhật tác giả
    │   │   └── response/
    │   │       ├── AuthorResponse.java          → Response tác giả
    │   │       └── AuthorDetailResponse.java    → Response chi tiết tác giả
    │   ├── entity/
    │   │   └── Author.java                      → Entity tác giả
    │   ├── repository/
    │   │   └── AuthorRepository.java            → Truy vấn DB tác giả
    │   ├── mapper/
    │   │   └── AuthorMapper.java                → Map Author sang DTO
    │   └── validator/
    │       └── AuthorValidator.java             → Validate rule tác giả
    │
    ├── category/
    │   ├── controller/
    │   │   ├── CategoryPublicController.java    → API đọc danh mục cho người dùng
    │   │   └── CategoryAdminController.java     → API quản lý danh mục cho admin
    │   ├── service/
    │   │   ├── CategoryQueryService.java        → Interface đọc danh mục
    │   │   ├── CategoryCommandService.java      → Interface ghi danh mục
    │   │   ├── CategoryValidationService.java   → Interface validate danh mục
    │   │   └── impl/
    │   │       ├── CategoryQueryServiceImpl.java→ Đọc danh mục / tìm kiếm
    │   │       ├── CategoryCommandServiceImpl.java → Tạo/sửa/xóa/activate/deactivate danh mục
    │   │       └── CategoryValidationServiceImpl.java → Validate danh mục
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateCategoryRequest.java   → Request tạo danh mục
    │   │   │   └── UpdateCategoryRequest.java   → Request cập nhật danh mục
    │   │   └── response/
    │   │       ├── CategoryResponse.java       → Response danh mục
    │   │       └── CategoryDetailResponse.java → Response chi tiết danh mục
    │   ├── entity/
    │   │   └── Category.java                    → Entity danh mục
    │   ├── repository/
    │   │   └── CategoryRepository.java         → Truy vấn DB danh mục
    │   ├── mapper/
    │   │   └── CategoryMapper.java             → Map Category sang DTO
    │   └── validator/
    │       └── CategoryValidator.java          → Validate rule danh mục
    │
    ├── cart/
    │   ├── controller/
    │   │   └── CartController.java              → API giỏ hàng cho user
    │   ├── service/
    │   │   ├── CartQueryService.java           → Interface đọc giỏ hàng
    │   │   ├── CartCommandService.java         → Interface thao tác giỏ hàng
    │   │   ├── CartPricingService.java         → Interface tính giá giỏ hàng
    │   │   ├── CartValidationService.java      → Interface validate giỏ hàng
    │   │   └── impl/
    │   │       ├── CartQueryServiceImpl.java   → Đọc giỏ hàng
    │   │       ├── CartCommandServiceImpl.java  → Thêm/sửa/xóa/clear giỏ hàng
    │   │       ├── CartPricingServiceImpl.java  → Tính giá và giảm giá giỏ hàng
    │   │       └── CartValidationServiceImpl.java → Validate số lượng/sách/item
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── AddToCartRequest.java       → Request thêm sách vào giỏ
    │   │   │   └── UpdateCartItemRequest.java  → Request sửa số lượng item
    │   │   └── response/
    │   │       ├── CartResponse.java           → Response toàn bộ giỏ hàng
    │   │       └── CartItemResponse.java       → Response từng item trong giỏ
    │   ├── entity/
    │   │   ├── Cart.java                       → Entity giỏ hàng
    │   │   └── CartItem.java                   → Entity item trong giỏ
    │   ├── repository/
    │   │   ├── CartRepository.java             → Truy vấn DB cart
    │   │   └── CartItemRepository.java         → Truy vấn DB cart item
    │   ├── mapper/
    │   │   └── CartItemMapper.java             → Map CartItem sang response
    │   └── validator/
    │       └── CartValidator.java              → Validate rule giỏ hàng
    │
    ├── order/
    │   ├── controller/
    │   │   ├── OrderUserController.java        → API user tạo và xem đơn hàng
    │   │   └── OrderAdminController.java       → API admin xử lý đơn hàng
    │   ├── service/
    │   │   ├── OrderQueryService.java          → Interface đọc đơn hàng
    │   │   ├── OrderCommandService.java        → Interface tạo/cập nhật đơn
    │   │   ├── OrderStatusService.java         → Interface chuyển trạng thái đơn
    │   │   ├── CheckoutFacade.java             → Interface gom luồng checkout
    │   │   ├── OrderValidationService.java     → Interface validate đơn hàng
    │   │   └── impl/
    │   │       ├── OrderQueryServiceImpl.java  → Đọc danh sách/chi tiết đơn
    │   │       ├── OrderCommandServiceImpl.java→ Tạo/cập nhật đơn
    │   │       ├── OrderStatusServiceImpl.java → Chuyển trạng thái đơn hàng
    │   │       ├── CheckoutFacadeImpl.java     → Điều phối checkout end-to-end
    │   │       └── OrderValidationServiceImpl.java → Validate đơn hàng
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateOrderRequest.java     → Request tạo đơn
    │   │   │   ├── CancelOrderRequest.java     → Request hủy đơn
    │   │   │   ├── UpdateOrderStatusRequest.java → Request đổi trạng thái đơn
    │   │   │   └── OrderSearchRequest.java     → Request tìm kiếm đơn
    │   │   └── response/
    │   │       ├── OrderResponse.java          → Response đơn hàng
    │   │       ├── OrderDetailResponse.java    → Response chi tiết đơn
    │   │       └── OrderAdminResponse.java     → Response đơn cho admin
    │   ├── entity/
    │   │   ├── Order.java                      → Entity đơn hàng
    │   │   └── OrderItem.java                  → Entity item trong đơn
    │   ├── repository/
    │   │   ├── OrderRepository.java            → Truy vấn DB đơn hàng
    │   │   └── OrderItemRepository.java        → Truy vấn DB item đơn hàng
    │   ├── mapper/
    │   │   └── OrderMapper.java               → Map Order và OrderItem sang DTO
    │   ├── validator/
    │   │   └── OrderValidator.java            → Validate rule nghiệp vụ đơn hàng
    │   └── specification/
    │       └── OrderSpecification.java       → Query động cho lọc/tìm kiếm đơn
    │
    ├── payment/
    │   ├── controller/
    │   │   └── PaymentController.java         → API khởi tạo và theo dõi thanh toán
    │   ├── service/
    │   │   ├── PaymentService.java            → Interface điều phối payment
    │   │   ├── PaymentCallbackService.java    → Xử lý callback từ cổng thanh toán
    │   │   ├── PaymentStatusService.java      → Tra cứu/cập nhật trạng thái payment
    │   │   ├── PaymentValidationService.java  → Validate dữ liệu thanh toán
    │   │   └── impl/
    │   │       ├── PaymentServiceImpl.java    → Hiện thực flow thanh toán
    │   │       ├── PaymentCallbackServiceImpl.java → Hiện thực callback payment
    │   │       ├── PaymentStatusServiceImpl.java   → Hiện thực trạng thái payment
    │   │       └── PaymentValidationServiceImpl.java → Validate payment
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreatePaymentRequest.java  → Request tạo payment
    │   │   │   └── PaymentCallbackRequest.java→ Request callback từ gateway
    │   │   └── response/
    │   │       └── PaymentResponse.java      → Response payment
    │   ├── entity/
    │   │   └── Payment.java                  → Entity thanh toán
    │   ├── repository/
    │   │   └── PaymentRepository.java        → Truy vấn DB payment
    │   ├── mapper/
    │   │   └── PaymentMapper.java            → Map Payment sang DTO
    │   └── validator/
    │       └── PaymentValidator.java         → Validate rule payment
    │
    ├── review/
    │   ├── controller/
    │   │   ├── ReviewPublicController.java   → API xem review công khai
    │   │   └── ReviewUserController.java     → API user tạo/sửa/xóa review
    │   ├── service/
    │   │   ├── ReviewQueryService.java       → Interface đọc review
    │   │   ├── ReviewCommandService.java     → Interface ghi review
    │   │   ├── ReviewValidationService.java  → Interface validate review
    │   │   └── impl/
    │   │       ├── ReviewQueryServiceImpl.java → Đọc danh sách/chi tiết review
    │   │       ├── ReviewCommandServiceImpl.java → Tạo/cập nhật/xóa review
    │   │       └── ReviewValidationServiceImpl.java → Validate review
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateReviewRequest.java  → Request tạo review
    │   │   │   └── UpdateReviewRequest.java  → Request cập nhật review
    │   │   └── response/
    │   │       └── ReviewResponse.java       → Response review
    │   ├── entity/
    │   │   └── Review.java                   → Entity đánh giá sách
    │   ├── repository/
    │   │   └── ReviewRepository.java        → Truy vấn DB review
    │   ├── mapper/
    │   │   └── ReviewMapper.java            → Map Review sang DTO
    │   └── validator/
    │       └── ReviewValidator.java         → Validate rule review
    │
    ├── voucher/
    │   ├── controller/
    │   │   ├── VoucherPublicController.java → API user xem/áp voucher
    │   │   └── VoucherAdminController.java  → API admin quản lý voucher
    │   ├── service/
    │   │   ├── VoucherQueryService.java     → Interface đọc voucher
    │   │   ├── VoucherCommandService.java   → Interface ghi voucher
    │   │   ├── VoucherApplyService.java     → Interface áp voucher vào đơn
    │   │   ├── VoucherValidationService.java→ Interface validate voucher
    │   │   └── impl/
    │   │       ├── VoucherQueryServiceImpl.java → Đọc danh sách voucher
    │   │       ├── VoucherCommandServiceImpl.java→ Tạo/sửa/xóa voucher
    │   │       ├── VoucherApplyServiceImpl.java → Áp voucher vào checkout
    │   │       └── VoucherValidationServiceImpl.java → Validate voucher
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateVoucherRequest.java → Request tạo voucher
    │   │   │   ├── UpdateVoucherRequest.java → Request cập nhật voucher
    │   │   │   └── ApplyVoucherRequest.java  → Request áp voucher
    │   │   └── response/
    │   │       ├── VoucherResponse.java     → Response voucher
    │   │       └── VoucherApplyResponse.java→ Response khi áp voucher
    │   ├── entity/
    │   │   └── Voucher.java                 → Entity voucher
    │   ├── repository/
    │   │   └── VoucherRepository.java       → Truy vấn DB voucher
    │   ├── mapper/
    │   │   └── VoucherMapper.java           → Map Voucher sang DTO
    │   └── validator/
    │       └── VoucherValidator.java        → Validate rule voucher
    │
    ├── wishlist/
    │   ├── controller/
    │   │   └── WishlistController.java      → API quản lý danh sách yêu thích
    │   ├── service/
    │   │   ├── WishlistQueryService.java    → Interface đọc wishlist
    │   │   ├── WishlistCommandService.java   → Interface ghi wishlist
    │   │   ├── WishlistValidationService.java→ Interface validate wishlist
    │   │   └── impl/
    │   │       ├── WishlistQueryServiceImpl.java → Đọc danh sách yêu thích
    │   │       ├── WishlistCommandServiceImpl.java→ Thêm/xóa wishlist
    │   │       └── WishlistValidationServiceImpl.java → Validate wishlist
    │   ├── dto/
    │   │   ├── request/
    │   │   │   └── AddWishlistRequest.java → Request thêm sách vào wishlist
    │   │   └── response/
    │   │       └── WishlistResponse.java    → Response wishlist
    │   ├── entity/
    │   │   └── Wishlist.java               → Entity wishlist
    │   ├── repository/
    │   │   └── WishlistRepository.java     → Truy vấn DB wishlist
    │   ├── mapper/
    │   │   └── WishlistMapper.java         → Map Wishlist sang DTO
    │   └── validator/
    │       └── WishlistValidator.java      → Validate rule wishlist
    │
    └── inventory/
        ├── controller/
        │   └── InventoryAdminController.java → API admin quản lý tồn kho
        ├── service/
        │   ├── InventoryQueryService.java    → Interface đọc tồn kho
        │   ├── InventoryCommandService.java  → Interface cập nhật tồn kho
        │   ├── InventoryCheckService.java    → Interface kiểm tra hàng còn/hết
        │   ├── InventoryValidationService.java → Interface validate tồn kho
        │   └── impl/
        │       ├── InventoryQueryServiceImpl.java → Đọc tồn kho
        │       ├── InventoryCommandServiceImpl.java → Cập nhật tồn kho
        │       ├── InventoryCheckServiceImpl.java → Kiểm tra trạng thái tồn
        │       └── InventoryValidationServiceImpl.java → Validate tồn kho
        ├── dto/
        │   ├── request/
        │   │   └── UpdateInventoryRequest.java → Request cập nhật số lượng tồn
        │   └── response/
        │       └── InventoryResponse.java    → Response tồn kho
        ├── entity/
        │   └── Inventory.java                → Entity tồn kho
        ├── repository/
        │   └── InventoryRepository.java      → Truy vấn DB tồn kho
        ├── mapper/
        │   └── InventoryMapper.java          → Map Inventory sang DTO
        └── validator/
            └── InventoryValidator.java       → Validate rule tồn kho

WebBansachApplication.java                     → Class khởi động Spring Boot
```
