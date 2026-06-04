src/
│
├── assets/
│   ├── images/
│   ├── icons/
│   └── styles/
│
├── components/
│   ├── common/
│   │   ├── Button.jsx
│   │   ├── Input.jsx
│   │   ├── Modal.jsx
│   │   ├── Pagination.jsx
│   │   ├── StatusBadge.jsx
│   │   ├── Loading.jsx
│   │   └── EmptyState.jsx
│   │
│   ├── user/
│   │   ├── BookCard.jsx
│   │   ├── BookFilter.jsx
│   │   ├── CartItem.jsx
│   │   ├── WishlistItem.jsx
│   │   ├── OrderCard.jsx
│   │   └── PriceSummary.jsx
│   │
│   └── admin/
│       ├── AdminSidebar.jsx
│       ├── AdminTopbar.jsx
│       ├── StatCard.jsx
│       ├── DataTable.jsx
│       ├── BookForm.jsx
│       ├── CategoryForm.jsx
│       ├── UserForm.jsx
│       └── VoucherForm.jsx
│
├── layouts/
│   ├── UserLayout.jsx
│   ├── AdminLayout.jsx
│   └── AuthLayout.jsx
│
├── pages/
│   ├── auth/
│   │   ├── LoginPage.jsx
│   │   ├── RegisterPage.jsx
│   │   ├── ForgotPasswordPage.jsx
│   │   └── ResetPasswordPage.jsx
│   │
│   ├── user/
│   │   ├── HomePage.jsx
│   │   ├── BookListPage.jsx
│   │   ├── BookDetailPage.jsx
│   │   ├── CartPage.jsx
│   │   ├── WishlistPage.jsx
│   │   ├── CheckoutPage.jsx
│   │   ├── PaymentQRPage.jsx
│   │   ├── PaymentResultPage.jsx
│   │   ├── MyOrdersPage.jsx
│   │   ├── OrderDetailPage.jsx
│   │   ├── ProfilePage.jsx
│   │   └── UpdateProfilePage.jsx
│   │
│   └── admin/
│       ├── DashboardPage.jsx
│       ├── BookManagementPage.jsx
│       ├── CreateBookPage.jsx
│       ├── EditBookPage.jsx
│       ├── CategoryManagementPage.jsx
│       ├── AuthorManagementPage.jsx
│       ├── UserManagementPage.jsx
│       ├── OrderManagementPage.jsx
│       ├── AdminOrderDetailPage.jsx
│       ├── InventoryManagementPage.jsx
│       ├── VoucherManagementPage.jsx
│       ├── ReviewManagementPage.jsx
│       └── PaymentManagementPage.jsx
│
├── routes/
│   ├── AppRoutes.jsx
│   ├── UserRoutes.jsx
│   ├── AdminRoutes.jsx
│   └── ProtectedRoute.jsx
│
├── services/
│   ├── api.js
│   ├── authService.js
│   ├── bookService.js
│   ├── categoryService.js
│   ├── authorService.js
│   ├── cartService.js
│   ├── wishlistService.js
│   ├── orderService.js
│   ├── paymentService.js
│   ├── inventoryService.js
│   ├── voucherService.js
│   └── reviewService.js
│
├── hooks/
│   ├── useAuth.js
│   ├── useFetch.js
│   └── useDebounce.js
│
├── contexts/
│   ├── AuthContext.jsx
│   └── CartContext.jsx
│
├── utils/
│   ├── formatCurrency.js
│   ├── formatDate.js
│   └── constants.js
│
├── App.jsx
└── main.jsx