# 📚 BookStore React App - Cấu Trúc Dự Án

## Giới thiệu

Đây là dự án BookStore được chuyển đổi từ HTML/Tailwind sang React. Dự án sử dụng:

- **React 18+** - Thư viện UI
- **Tailwind CSS** - Utility-first CSS framework
- **Design System** - Modern Literary Interface với color tokens, typography, spacing

## 📁 Cấu Trúc Thư Mục

```
src/
├── components/
│   └── layout/
│       ├── Header.js          # Thanh điều hướng chính
│       ├── Sidebar.js         # Sidebar admin (có toggle collapse)
│       ├── Footer.js          # Footer
│       └── Layout.js          # Layout wrappers (MainLayout, AdminLayout, BlankLayout)
├── pages/
│   ├── HomePage.js            # Trang chủ - Hero + Featured Books
│   └── AdminDashboard.js      # Bảng điều khiển admin
├── constants/
│   └── theme.js               # Design tokens (colors, typography, spacing)
├── styles/
│   └── index.css              # CSS custom (nếu cần)
└── App.js                     # Component gốc
```

## 🎨 Design System

### Sử dụng Design Tokens

Tất cả các màu, kích thước, và font đã được định nghĩa trong Tailwind config:

```javascript
// Tailwind classes được tạo từ design system:
<div className="bg-primary text-on-primary">        {/* Primary color */}
<h1 className="font-h1 text-h1">              {/* Heading 1 style */}
<div className="px-gutter py-section-gap">      {/* Spacing tokens */}
```

### Danh sách Token Chính

**Colors:**
- `primary` / `on-primary`
- `secondary` / `on-secondary`
- `surface` / `on-surface`
- `error` / `on-error`
- Và nhiều colors khác...

**Typography:**
- `font-h1` / `text-h1` - Heading 1 (40px, 800 weight)
- `font-h2` / `text-h2` - Heading 2 (32px, 700 weight)
- `font-h3` / `text-h3` - Heading 3 (24px, 700 weight)
- `font-body-lg` / `text-body-lg` - Large body (18px)
- `font-body-md` / `text-body-md` - Medium body (16px)
- `font-label-md` / `text-label-md` - Label (14px, 600 weight)
- `font-caption` / `text-caption` - Caption (12px)

**Spacing:**
- `unit` = 4px
- `stack-sm` = 8px
- `stack-md` = 16px
- `stack-lg` = 32px
- `gutter` = 24px
- `section-gap` = 64px
- `container-max` = 1280px

## 📖 Cách Sử Dụng

### 1. MainLayout - Trang Khách Hàng

Sử dụng cho trang chủ, chi tiết sách, giỏ hàng, etc.

```javascript
import { MainLayout } from './components/layout/Layout';
import HomePage from './pages/HomePage';

function App() {
  return (
    <MainLayout>
      <HomePage />
    </MainLayout>
  );
}
```

**Bao gồm:**
- Header (TopNavBar)
- Main content
- Footer

### 2. AdminLayout - Trang Quản Trị

Sử dụng cho admin dashboard, quản lý sách, etc.

```javascript
import { AdminLayout } from './components/layout/Layout';
import AdminDashboard from './pages/AdminDashboard';

function App() {
  return (
    <AdminLayout>
      <AdminDashboard />
    </AdminLayout>
  );
}
```

**Bao gồm:**
- Sidebar (fixed left, có toggle collapse)
- Header
- Main content

### 3. BlankLayout - Trang Đơn Giản

Sử dụng cho auth pages (login, register), errors, etc.

```javascript
import { BlankLayout } from './components/layout/Layout';
import LoginPage from './pages/LoginPage';

function App() {
  return (
    <BlankLayout>
      <LoginPage />
    </BlankLayout>
  );
}
```

**Chỉ bao gồm:**
- Content

## 🔄 Các Components Chung

### Header Component
- Logo/Brand
- Search bar
- Navigation links
- User actions (wishlist, cart, profile)
- Mobile menu toggle

**Props:**
- Hiện tại không có props, sử dụng inline configuration

**File:** [Header.js](./src/components/layout/Header.js)

### Sidebar Component
- Admin menu items
- Profile section
- Toggle collapse button
- Dark mode support

**Props:**
- `isOpen` (boolean) - Mở/đóng sidebar
- Mặc định: `true`

**File:** [Sidebar.js](./src/components/layout/Sidebar.js)

### Footer Component
- About section
- Quick links (About, Blog, Career)
- Support links (Contact, FAQ, Shipping)
- Legal links (Privacy, Terms, Cookies)
- Social media buttons
- Contact info

**Props:**
- Hiện tại không có props

**File:** [Footer.js](./src/components/layout/Footer.js)

## 📝 Tạo Trang Mới

### Bước 1: Tạo Component Page

```javascript
// src/pages/NewPage.js
import React from 'react';

const NewPage = () => {
  return (
    <div className="max-w-container-max mx-auto px-gutter py-section-gap">
      <h1 className="font-h1 text-h1 text-primary">Trang Mới</h1>
      {/* Content */}
    </div>
  );
};

export default NewPage;
```

### Bước 2: Sử dụng Layout Phù Hợp

```javascript
// App.js hoặc Router config
import { MainLayout } from './components/layout/Layout';
import NewPage from './pages/NewPage';

<MainLayout>
  <NewPage />
</MainLayout>
```

### Bước 3: Sử Dụng Tailwind Classes

Luôn sử dụng design tokens từ Tailwind config:

```javascript
// ✅ ĐÚNG - Sử dụng design tokens
<div className="bg-surface text-on-surface font-body-md">
  <h1 className="font-h1 text-h1 text-primary">Tiêu đề</h1>
  <p className="font-body-md text-body-md text-on-surface-variant">Nội dung</p>
  <button className="bg-primary text-on-primary px-gutter py-stack-md">
    Nút
  </button>
</div>

// ❌ SAI - Không sử dụng hardcode colors/sizes
<div className="bg-blue-500 text-white" style={{fontSize: '16px'}}>
```

## 🌙 Dark Mode Support

Dark mode được hỗ trợ qua Tailwind's `dark:` prefix:

```javascript
<div className="bg-surface dark:bg-surface-container text-on-surface dark:text-on-background">
  {/* Light mode: bg-surface, Dark mode: bg-surface-container */}
</div>
```

## 📦 Cập Nhật Tailwind Config

Tailwind config đã được cấu hình tại [tailwind.config.js](./tailwind.config.js) với:
- Design tokens colors
- Typography scales
- Spacing tokens
- Border radius
- Dark mode

**Không cần chỉnh sửa lại** trừ khi thêm token mới.

## 🚀 Next Steps

Các trang cần được tạo:

### Trang Khách Hàng (MainLayout)
- [ ] Trang chủ (HomePage.js) - ✅ DONE
- [ ] Danh sách sách (BooksPage.js)
- [ ] Chi tiết sách (BookDetailPage.js)
- [ ] Giỏ hàng (CartPage.js)
- [ ] Checkout (CheckoutPage.js)
- [ ] Đăng nhập (LoginPage.js)
- [ ] Đăng ký (RegisterPage.js)
- [ ] Tài khoản người dùng (ProfilePage.js)

### Trang Admin (AdminLayout)
- [ ] Dashboard (AdminDashboard.js) - ✅ DONE
- [ ] Quản lý sách (BookManagementPage.js)
- [ ] Quản lý danh mục (CategoryManagementPage.js)
- [ ] Quản lý đơn hàng (OrderManagementPage.js)
- [ ] Quản lý người dùng (UserManagementPage.js)
- [ ] Quản lý voucher (VoucherManagementPage.js)

## 💡 Tips & Best Practices

1. **Luôn sử dụng design tokens** - Không hardcode màu, size, spacing
2. **Tái sử dụng components chung** - Header, Sidebar, Footer
3. **Responsive design** - Sử dụng Tailwind breakpoints (md:, lg:)
4. **Accessibility** - Thêm `aria-label` và semantic HTML
5. **Dark mode** - Luôn xem xét dark mode support
6. **Mobile first** - Thiết kế cho mobile trước

## 📚 Tài Liệu

- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [Modern Literary Interface Design System](./src/constants/theme.js)
- [Tailwind Config](./tailwind.config.js)

## 📞 Support

Nếu cần hỗ trợ, liên hệ nhóm phát triển.
