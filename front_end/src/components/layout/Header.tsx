import { FormEvent, useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';

import { AccentButton, Icon, IconButton } from '@/components/ui/staticUi';

const navItems = [
  { label: 'Danh mục', to: '/categories' },
  { label: 'Sách mới', to: '/new-books' },
  { label: 'Tác giả', to: '/authors' },
  { label: 'Khuyến mãi', to: '/promotions' },
];

const Header = () => {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [open, setOpen] = useState(false);

  const onSearch = (event: FormEvent) => {
    event.preventDefault();
    const keyword = query.trim();
    if (keyword) {
      navigate(`/search?keyword=${encodeURIComponent(keyword)}`);
      setOpen(false);
    }
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
          <Link to="/profile" className="hidden sm:block" aria-label="Tài khoản">
            <IconButton><Icon name="user" /></IconButton>
          </Link>
          <Link to="/login" className="hidden xl:block">
            <AccentButton>Đăng nhập</AccentButton>
          </Link>
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
          </nav>
        </div>
      ) : null}
    </header>
  );
};

export default Header;
