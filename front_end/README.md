├── public/
│   ├── favicon.ico
│   └── images/
│
├── src/
│   ├── app/
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   ├── providers/
│   │   │   ├── AppProvider.tsx
│   │   │   ├── QueryProvider.tsx
│   │   │   └── RouterProvider.tsx
│   │   └── routes/
│   │       ├── index.tsx
│   │       ├── publicRoutes.tsx
│   │       ├── userRoutes.tsx
│   │       ├── adminRoutes.tsx
│   │       └── protectedRoutes.tsx
│   │
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   │       ├── globals.css
│   │       └── variables.css
│   │
│   ├── components/
│   │   ├── common/
│   │   │   ├── Button/
│   │   │   ├── Input/
│   │   │   ├── Modal/
│   │   │   ├── Pagination/
│   │   │   ├── Loading/
│   │   │   ├── EmptyState/
│   │   │   └── ConfirmDialog/
│   │   │
│   │   ├── layout/
│   │   │   ├── Header/
│   │   │   ├── Footer/
│   │   │   ├── Sidebar/
│   │   │   ├── UserLayout.tsx
│   │   │   ├── AdminLayout.tsx
│   │   │   └── AuthLayout.tsx
│   │   │
│   │   └── ui/
│   │       ├── Card.tsx
│   │       ├── Badge.tsx
│   │       ├── Table.tsx
│   │       ├── Tabs.tsx
│   │       └── Dropdown.tsx
│   │
│   ├── features/
│   │   ├── auth/
│   │   ├── books/
│   │   ├── categories/
│   │   ├── authors/
│   │   ├── cart/
│   │   ├── orders/
│   │   ├── payment/
│   │   ├── reviews/
│   │   ├── wishlist/
│   │   ├── vouchers/
│   │   ├── inventory/
│   │   └── admin/
│   │
│   ├── services/
│   │   ├── api/
│   │   │   ├── axiosClient.ts
│   │   │   ├── endpoints.ts
│   │   │   └── interceptors.ts
│   │   ├── storage/
│   │   │   ├── tokenStorage.ts
│   │   │   └── localStorage.ts
│   │   └── websocket/
│   │       └── socketClient.ts
│   │
│   ├── stores/
│   │   ├── authStore.ts
│   │   ├── cartStore.ts
│   │   └── uiStore.ts
│   │
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useDebounce.ts
│   │   ├── usePagination.ts
│   │   └── useDisclosure.ts
│   │
│   ├── types/
│   │   ├── api.types.ts
│   │   ├── auth.types.ts
│   │   ├── book.types.ts
│   │   ├── cart.types.ts
│   │   ├── order.types.ts
│   │   ├── payment.types.ts
│   │   ├── user.types.ts
│   │   └── common.types.ts
│   │
│   ├── utils/
│   │   ├── formatCurrency.ts
│   │   ├── formatDate.ts
│   │   ├── buildQueryString.ts
│   │   ├── validateFile.ts
│   │   └── constants.ts
│   │
│   ├── config/
│   │   ├── env.ts
│   │   ├── appConfig.ts
│   │   └── routePaths.ts
│   │
│   └── tests/
│       ├── setup.ts
│       └── mocks/
│
├── .env
├── .env.example
├── .gitignore
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md