import { Link, NavLink } from 'react-router-dom';

import { Icon } from '@/components/ui/staticUi';

const menuItems = [
  { icon: 'chart', label: 'Dashboard', to: '/admin/dashboard' },
  { icon: 'book', label: 'Sách', to: '/admin/books' },
  { icon: 'category', label: 'Danh mục', to: '/admin/categories' },
  { icon: 'order', label: 'Đơn hàng', to: '/admin/orders' },
  { icon: 'users', label: 'Người dùng', to: '/admin/users' },
  { icon: 'inventory', label: 'Tồn kho', to: '/admin/inventory' },
  { icon: 'ticket', label: 'Voucher', to: '/admin/vouchers' },
  { icon: 'user', label: 'Tác giả', to: '/admin/authors' },
];

const Sidebar = () => (
  <aside className="fixed inset-y-0 left-0 z-40 hidden w-64 bg-primary px-4 py-6 text-on-primary shadow-md lg:flex lg:flex-col">
    <Link to="/admin/dashboard" className="mb-10 flex items-center gap-3 px-2">
      <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary-container text-on-secondary-container">
        <Icon name="book" />
      </span>
      <div>
        <p className="text-base font-bold">Quản trị nhà sách</p>
        <p className="text-xs font-semibold text-on-primary/60">Quản trị vận hành</p>
      </div>
    </Link>

    <nav className="flex-1 space-y-1 overflow-y-auto">
      {menuItems.map((item) => (
        <NavLink
          key={item.to}
          to={item.to}
          className={({ isActive }) =>
            `flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-bold transition ${isActive ? 'bg-secondary-container text-on-secondary-container' : 'text-on-primary/70 hover:bg-primary-container hover:text-on-primary'}`
          }
        >
          <Icon name={item.icon} />
          {item.label}
        </NavLink>
      ))}
    </nav>

    <div className="mt-auto space-y-1 border-t border-on-primary/10 pt-4">
      <Link to="/" className="flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-bold text-on-primary/70 transition hover:bg-primary-container hover:text-on-primary">
        <Icon name="arrow" />
        Về cửa hàng
      </Link>
    </div>
  </aside>
);

export default Sidebar;
