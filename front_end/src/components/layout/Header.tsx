import { FormEvent, useEffect, useRef, useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
import { Icon, IconButton } from '@/components/ui/staticUi';
import cartService, { cartChangedEvent } from '@/features/cart/services/cartService';
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
  const [cartCount, setCartCount] = useState(0);
  const accountRef = useRef<HTMLDivElement>(null);
  const displayName = user?.fullName || 'Tài khoản';
  const avatarText = displayName.trim().charAt(0).toUpperCase() || 'U';

  useEffect(() => {
    if (!isAuthenticated) {
      setCartCount(0);
      return;
    }
    let active = true;
    cartService.getCart()
      .then((cart) => {
        if (active) setCartCount(cart.totalQuantity ?? cart.items.reduce((total, item) => total + item.quantity, 0));
      })
      .catch(() => {
        if (active) setCartCount(0);
      });
    const handleCartChange = (event: Event) => {
      const cart = (event as CustomEvent<Awaited<ReturnType<typeof cartService.getCart>>>).detail;
      setCartCount(cart.totalQuantity ?? cart.items.reduce((total, item) => total + item.quantity, 0));
    };
    window.addEventListener(cartChangedEvent, handleCartChange);
    return () => {
      active = false;
      window.removeEventListener(cartChangedEvent, handleCartChange);
    };
  }, [isAuthenticated]);

  useEffect(() => {
    const closeMenus = (event: KeyboardEvent | MouseEvent) => {
      if (event instanceof KeyboardEvent && event.key === 'Escape') {
        setOpen(false);
        setAccountOpen(false);
      }
      if (event instanceof MouseEvent && accountRef.current && !accountRef.current.contains(event.target as Node)) {
        setAccountOpen(false);
      }
    };
    document.addEventListener('keydown', closeMenus);
    document.addEventListener('mousedown', closeMenus);
    return () => {
      document.removeEventListener('keydown', closeMenus);
      document.removeEventListener('mousedown', closeMenus);
    };
  }, []);

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
    <header className="sticky top-0 z-50 border-b border-outline-variant bg-surface">
      <div className="hidden bg-primary text-on-primary sm:block">
        <div className="mx-auto flex h-8 max-w-container-max items-center justify-between px-6 text-xs lg:px-8">
          <p className="font-medium">Miễn phí vận chuyển cho đơn hàng đủ điều kiện</p>
          <p className="hidden text-on-primary/75 lg:block">Sách chính hãng · Đóng gói cẩn thận · Đổi trả minh bạch</p>
        </div>
      </div>

      <div className="mx-auto flex h-16 max-w-container-max items-center gap-3 px-4 sm:h-[72px] sm:px-6 lg:gap-6 lg:px-8">
        <Link to="/" className="group flex shrink-0 items-center gap-2.5" aria-label="Nhà Sách Tri Thức - Trang chủ">
          <span className="flex h-10 w-10 items-center justify-center overflow-hidden rounded-md bg-primary-fixed transition group-hover:bg-primary-fixed-dim">
            <img src={logoWeb} alt="" className="h-9 w-9 object-contain" />
          </span>
          <span className="font-serif text-[17px] font-bold leading-snug text-primary sm:text-lg">
            Nhà Sách <span className="hidden xl:inline">Tri Thức</span>
          </span>
        </Link>

        <form onSubmit={onSearch} className="ml-auto hidden min-w-0 flex-1 md:block lg:ml-4">
          <div className="relative mx-auto max-w-2xl">
            <Icon name="search" className="pointer-events-none absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-outline" />
            <input
              aria-label="Tìm kiếm sách"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              className="h-11 w-full rounded-md border border-border-strong bg-surface-container-low pl-11 pr-24 text-sm text-on-surface outline-none transition placeholder:text-text-muted hover:border-outline focus:border-primary focus:bg-surface focus:ring-2 focus:ring-primary/15"
              placeholder="Tìm theo tên sách, tác giả hoặc ISBN"
            />
            <button type="submit" className="absolute right-1.5 top-1/2 hidden h-8 -translate-y-1/2 items-center rounded px-3 text-xs font-semibold text-primary transition hover:bg-primary-fixed lg:inline-flex">
              Tìm kiếm
            </button>
          </div>
        </form>

        <div className="flex items-center gap-1 sm:gap-1.5">
          <Link to="/wishlist" aria-label="Danh sách yêu thích" className="hidden h-11 w-11 items-center justify-center rounded-md text-on-surface-variant transition hover:bg-surface-container hover:text-primary sm:inline-flex">
            <Icon name="heart" />
          </Link>
          <Link to="/cart" aria-label={`Giỏ hàng, ${cartCount} sản phẩm`} className="relative inline-flex h-11 w-11 items-center justify-center rounded-md text-on-surface-variant transition hover:bg-surface-container hover:text-primary">
            <Icon name="cart" />
            {cartCount > 0 ? <span className="absolute right-0 top-0 min-w-5 rounded-full bg-secondary-container px-1 text-center text-[11px] font-bold leading-5 text-on-secondary-container">{cartCount > 99 ? '99+' : cartCount}</span> : null}
          </Link>

          {isAuthenticated ? (
            <div ref={accountRef} className="relative hidden sm:block">
              <button type="button" onClick={() => setAccountOpen((value) => !value)} className="flex h-11 items-center gap-2 rounded-md px-2 text-sm font-semibold text-primary transition hover:bg-surface-container" aria-label="Tài khoản" aria-expanded={accountOpen} aria-controls="account-menu">
                <span className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-bold text-on-primary">{avatarText}</span>
                <span className="hidden max-w-28 truncate xl:block">{displayName}</span>
              </button>
              {accountOpen ? (
                <div id="account-menu" className="absolute right-0 mt-2 w-56 overflow-hidden rounded-lg border border-outline-variant bg-surface py-1 shadow-lg">
                  <div className="border-b border-outline-variant px-4 py-3">
                    <p className="truncate text-sm font-semibold text-primary">{displayName}</p>
                    <p className="mt-0.5 truncate text-xs text-on-surface-variant">{user?.role === 'ADMIN' ? 'Quản trị viên' : 'Khách hàng'}</p>
                  </div>
                  <Link to="/profile" onClick={() => setAccountOpen(false)} className="block px-4 py-2.5 text-sm text-on-surface-variant hover:bg-surface-container hover:text-primary">Hồ sơ của tôi</Link>
                  <Link to="/orders" onClick={() => setAccountOpen(false)} className="block px-4 py-2.5 text-sm text-on-surface-variant hover:bg-surface-container hover:text-primary">Đơn hàng của tôi</Link>
                  {user?.role === 'ADMIN' ? <Link to="/admin" onClick={() => setAccountOpen(false)} className="block px-4 py-2.5 text-sm text-on-surface-variant hover:bg-surface-container hover:text-primary">Trang quản trị</Link> : null}
                  <div className="mt-1 border-t border-outline-variant pt-1">
                    <button type="button" onClick={handleLogout} className="block w-full px-4 py-2.5 text-left text-sm font-medium text-error hover:bg-error-container">Đăng xuất</button>
                  </div>
                </div>
              ) : null}
            </div>
          ) : (
            <Link to="/login" className="hidden h-11 items-center rounded-md bg-primary px-4 text-sm font-semibold text-on-primary transition hover:bg-primary-container sm:inline-flex">Đăng nhập</Link>
          )}

          <IconButton className="border-0 lg:hidden" onClick={() => setOpen((value) => !value)} aria-label={open ? 'Đóng menu' : 'Mở menu'} aria-expanded={open} aria-controls="mobile-navigation">
            <Icon name={open ? 'x' : 'menu'} />
          </IconButton>
        </div>
      </div>

      <div className="hidden border-t border-outline-variant lg:block">
        <div className="mx-auto flex h-11 max-w-container-max items-center justify-between px-8">
          <nav className="flex h-full items-center gap-8" aria-label="Điều hướng chính">
            {navItems.map((item) => (
              <NavLink key={item.to} to={item.to} className={({ isActive }) => `relative inline-flex h-full items-center text-sm font-medium transition after:absolute after:inset-x-0 after:bottom-0 after:h-0.5 after:origin-center after:bg-secondary after:transition-transform ${isActive ? 'text-primary after:scale-x-100' : 'text-on-surface-variant after:scale-x-0 hover:text-primary hover:after:scale-x-100'}`}>
                {item.label}
              </NavLink>
            ))}
          </nav>
          <Link to="/catalog" className="inline-flex items-center gap-2 text-sm font-semibold text-primary transition hover:text-secondary">
            Khám phá tất cả sách <Icon name="arrow" className="h-4 w-4" />
          </Link>
        </div>
      </div>

      {open ? (
        <div id="mobile-navigation" className="border-t border-outline-variant bg-surface px-4 pb-5 pt-4 shadow-lg lg:hidden">
          <form onSubmit={onSearch} className="relative mb-4 md:hidden">
            <Icon name="search" className="pointer-events-none absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-outline" />
            <input aria-label="Tìm kiếm sách" value={query} onChange={(event) => setQuery(event.target.value)} className="h-12 w-full rounded-md border border-border-strong bg-surface-container-low pl-11 pr-4 text-sm outline-none focus:border-primary focus:bg-surface focus:ring-2 focus:ring-primary/15" placeholder="Tìm tên sách hoặc tác giả" />
          </form>
          <nav className="grid gap-1" aria-label="Điều hướng di động">
            {navItems.map((item) => (
              <NavLink key={item.to} to={item.to} onClick={() => setOpen(false)} className={({ isActive }) => `flex min-h-11 items-center border-l-2 px-3 text-sm font-medium transition ${isActive ? 'border-secondary bg-secondary-fixed text-primary' : 'border-transparent text-on-surface-variant hover:bg-surface-container hover:text-primary'}`}>
                {item.label}
              </NavLink>
            ))}
            <Link to="/wishlist" onClick={() => setOpen(false)} className="flex min-h-11 items-center border-l-2 border-transparent px-3 text-sm font-medium text-on-surface-variant hover:bg-surface-container hover:text-primary sm:hidden">Danh sách yêu thích</Link>
            <Link to="/orders" onClick={() => setOpen(false)} className="flex min-h-11 items-center border-l-2 border-transparent px-3 text-sm font-medium text-on-surface-variant hover:bg-surface-container hover:text-primary">Đơn hàng của tôi</Link>
            {isAuthenticated ? (
              <>
                <Link to="/profile" onClick={() => setOpen(false)} className="flex min-h-11 items-center border-l-2 border-transparent px-3 text-sm font-medium text-on-surface-variant hover:bg-surface-container hover:text-primary sm:hidden">Hồ sơ của tôi</Link>
                {user?.role === 'ADMIN' ? <Link to="/admin" onClick={() => setOpen(false)} className="flex min-h-11 items-center border-l-2 border-transparent px-3 text-sm font-medium text-on-surface-variant hover:bg-surface-container hover:text-primary">Trang quản trị</Link> : null}
                <button type="button" onClick={handleLogout} className="min-h-11 border-l-2 border-transparent px-3 text-left text-sm font-medium text-error hover:bg-error-container">Đăng xuất</button>
              </>
            ) : (
              <Link to="/login" onClick={() => setOpen(false)} className="mt-3 inline-flex min-h-11 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-on-primary hover:bg-primary-container sm:hidden">Đăng nhập</Link>
            )}
          </nav>
        </div>
      ) : null}
    </header>
  );
};

export default Header;
