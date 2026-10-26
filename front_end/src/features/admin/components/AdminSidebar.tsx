import { Link, NavLink } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
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

type AdminSidebarProps = { open: boolean; onClose: () => void };

const AdminSidebar = ({ open, onClose }: AdminSidebarProps) => (
  <>
  {open ? <button type="button" aria-label="Đóng menu quản trị" className="fixed inset-0 z-40 bg-primary/40 backdrop-blur-[1px] lg:hidden" onClick={onClose} /> : null}
  <aside className={`fixed inset-y-0 left-0 z-50 flex w-64 flex-col bg-primary px-4 py-6 text-on-primary transition-transform duration-200 lg:z-40 lg:translate-x-0 ${open ? 'translate-x-0' : '-translate-x-full'}`}>
    <div className="mb-8 flex items-center gap-2">
    <Link to="/admin/dashboard" onClick={onClose} className="flex min-w-0 flex-1 items-center gap-3 px-2">
      <span className="flex h-11 w-11 items-center justify-center overflow-hidden rounded-lg border border-on-primary/10 bg-white shadow-sm">
        <img src={logoWeb} alt="Nhà Sách Tri Thức" className="h-full w-full object-cover" />
      </span>
      <div>
        <p className="text-base font-bold">Quản trị nhà sách</p>
        <p className="text-xs font-semibold text-on-primary/60">Quản trị vận hành</p>
      </div>
    </Link>
    <button type="button" onClick={onClose} aria-label="Đóng menu" className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md text-on-primary/70 hover:bg-on-primary/10 lg:hidden"><Icon name="x" /></button>
    </div>

    <nav className="flex-1 space-y-1 overflow-y-auto">
      {menuItems.map((item) => (
        <NavLink
          key={item.to}
          to={item.to}
          onClick={onClose}
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
      <Link to="/" onClick={onClose} className="flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-bold text-on-primary/70 transition hover:bg-primary-container hover:text-on-primary">
        <Icon name="arrow" />
        Về cửa hàng
      </Link>
    </div>
  </aside>
  </>
);

export default AdminSidebar;
