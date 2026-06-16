import { FormEvent, useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';

import { AccentButton, Icon, IconButton } from '@/components/ui/staticUi';
import { useAuth } from '@/hooks/useAuth';

const navItems = [
  { label: 'Danh mục', to: '/categories' },
  { label: 'Sách mới', to: '/new-books' },
  { label: 'Tác giả', to: '/authors' },
  { label: 'Khuyến mãi', to: '/promotions' },
];

const Header = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated, logout } = useAuth();
  const [query, setQuery] = useState('');
  const [open, setOpen] = useState(false);
  const [accountOpen, setAccountOpen] = useState(false);

  const displayName = user?.fullName || 'Tài khoản';
  const avatarText = displayName.trim().charAt(0).toUpperCase() || 'U';

  const onSearch = (event: FormEvent) => {
    event.preventDefault();
    const keyword = query.trim();
    if (keyword) {
      navigate(`/search?keyword=${encodeURIComponent(keyword)}`);
      setOpen(false);
    }
  };

  const handleLogout = () => {
    logout();
    setAccountOpen(false);
    setOpen(false);
    navigate('/');
  };

  return (
    <header className="sticky top-0 z-50 border-b border-outline-variant bg-surface/95 backdrop-blur">
      <div className="mx-auto flex h-20 max-w-container-max items-center gap-5 px-4 sm:px-6 lg:px-8">
        <Link to="/" className="flex shrink-0 items-center gap-3">
          <span className="flex h-11 w-11 items-center justify-center rounded-lg bg-primary text-on-primary">
            <Icon name="book" />
          </span>
          <span className="text-lg font-bold text-primary">Nhà Sách Tri Thức</span>
        </Link>

        <nav className="hidden items-center gap-6 lg:flex">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `text-sm font-semibold transition ${isActive ? 'text-secondary' : 'text-on-surface-variant hover:text-secondary'}`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <form onSubmit={onSearch} className="ml-auto hidden flex-1 justify-end md:flex">
          <div className="relative w-full max-w-xs">
            <Icon name="search" className="pointer-events-none absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-outline" />
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              className="h-11 w-full rounded-full border-0 bg-surface-container pl-10 pr-4 text-sm outline-none transition focus:ring-2 focus:ring-primary/25"
              placeholder="Tìm kiếm sách..."
            />
          </div>
        </form>

        <div className="flex items-center gap-2">
          <Link to="/wishlist" aria-label="Yêu thích">
            <IconButton><Icon name="heart" /></IconButton>
          </Link>
          <Link to="/cart" aria-label="Giỏ hàng">
            <IconButton className="relative">
              <Icon name="cart" />
              <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-secondary-container" />
            </IconButton>
          </Link>
          {isAuthenticated ? (
            <div className="relative hidden sm:block">
              <button
                type="button"
                onClick={() => setAccountOpen((value) => !value)}
                className="flex h-11 items-center gap-2 rounded-lg border border-outline-variant bg-surface px-2.5 text-sm font-bold text-primary shadow-sm transition hover:bg-surface-container"
                aria-label="Tài khoản"
              >
                <span className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-bold text-on-primary">
                  {avatarText}
                </span>
                <span className="hidden max-w-28 truncate xl:block">{displayName}</span>
              </button>
              {accountOpen ? (
                <div className="absolute right-0 mt-2 w-56 overflow-hidden rounded-lg border border-outline-variant bg-surface shadow-lg">
                  <div className="border-b border-outline-variant px-4 py-3">
                    <p className="truncate text-sm font-bold text-primary">{displayName}</p>
                    <p className="truncate text-xs text-on-surface-variant">{user?.role === 'ADMIN' ? 'Quản trị viên' : 'Khách hàng'}</p>
                  </div>
                  <Link to="/profile" onClick={() => setAccountOpen(false)} className="block px-4 py-3 text-sm font-semibold text-on-surface-variant hover:bg-surface-container">Hồ sơ của tôi</Link>
                  <Link to="/orders" onClick={() => setAccountOpen(false)} className="block px-4 py-3 text-sm font-semibold text-on-surface-variant hover:bg-surface-container">Đơn hàng của tôi</Link>
                  {user?.role === 'ADMIN' ? <Link to="/admin" onClick={() => setAccountOpen(false)} className="block px-4 py-3 text-sm font-semibold text-on-surface-variant hover:bg-surface-container">Trang quản trị</Link> : null}
                  <button type="button" onClick={handleLogout} className="block w-full px-4 py-3 text-left text-sm font-semibold text-error hover:bg-error-container">Đăng xuất</button>
                </div>
              ) : null}
            </div>
          ) : (
            <Link to="/login" className="hidden xl:block">
              <AccentButton>Đăng nhập</AccentButton>
            </Link>
          )}
          <IconButton className="lg:hidden" onClick={() => setOpen((value) => !value)} aria-label="Mở menu">
            <Icon name={open ? 'x' : 'menu'} />
          </IconButton>
        </div>
      </div>

      {open ? (
        <div className="border-t border-outline-variant bg-surface px-4 py-4 lg:hidden">
          <form onSubmit={onSearch} className="mb-4">
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              className="h-11 w-full rounded-full border-0 bg-surface-container px-4 text-sm outline-none focus:ring-2 focus:ring-primary/25"
              placeholder="Tìm kiếm sách"
            />
          </form>
          <nav className="grid gap-1">
            {navItems.map((item) => (
              <Link key={item.to} to={item.to} onClick={() => setOpen(false)} className="rounded-lg px-3 py-3 text-sm font-bold text-on-surface-variant hover:bg-surface-container">
                {item.label}
              </Link>
            ))}
            <Link to="/orders" onClick={() => setOpen(false)} className="rounded-lg px-3 py-3 text-sm font-bold text-on-surface-variant hover:bg-surface-container">
              Đơn hàng của tôi
            </Link>
            {isAuthenticated ? (
              <>
                <Link to="/profile" onClick={() => setOpen(false)} className="rounded-lg px-3 py-3 text-sm font-bold text-on-surface-variant hover:bg-surface-container">
                  Hồ sơ của tôi
                </Link>
                {user?.role === 'ADMIN' ? (
                  <Link to="/admin" onClick={() => setOpen(false)} className="rounded-lg px-3 py-3 text-sm font-bold text-on-surface-variant hover:bg-surface-container">
                    Trang quản trị
                  </Link>
                ) : null}
                <button type="button" onClick={handleLogout} className="rounded-lg px-3 py-3 text-left text-sm font-bold text-error hover:bg-error-container">
                  Đăng xuất
                </button>
              </>
            ) : (
              <Link to="/login" onClick={() => setOpen(false)} className="rounded-lg px-3 py-3 text-sm font-bold text-secondary hover:bg-surface-container">
                Đăng nhập
              </Link>
            )}
          </nav>
        </div>
      ) : null}
    </header>
  );
};

export default Header;
