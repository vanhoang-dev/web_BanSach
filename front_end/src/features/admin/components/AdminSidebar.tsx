import { Link, NavLink } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
import { Icon } from '@/components/ui/staticUi';

const menuGroups = [
  {
    label: 'Tổng quan',
    items: [{ icon: 'chart', label: 'Dashboard', to: '/admin/dashboard' }],
  },
  {
    label: 'Nội dung',
    items: [
      { icon: 'book', label: 'Sách', to: '/admin/books' },
      { icon: 'category', label: 'Danh mục', to: '/admin/categories' },
      { icon: 'user', label: 'Tác giả', to: '/admin/authors' },
    ],
  },
  {
    label: 'Vận hành',
    items: [
      { icon: 'order', label: 'Đơn hàng', to: '/admin/orders' },
      { icon: 'inventory', label: 'Tồn kho', to: '/admin/inventory' },
      { icon: 'ticket', label: 'Voucher', to: '/admin/vouchers' },
      { icon: 'users', label: 'Người dùng', to: '/admin/users' },
    ],
  },
];

type AdminSidebarProps = { open: boolean; onClose: () => void };

const AdminSidebar = ({ open, onClose }: AdminSidebarProps) => (
  <>
  {open ? <button type="button" aria-label="Đóng menu quản trị" className="fixed inset-0 z-40 bg-primary/40 backdrop-blur-[2px] lg:hidden" onClick={onClose} /> : null}
  <aside className={`fixed inset-y-0 left-0 z-50 flex w-72 flex-col border-r border-primary-container bg-primary px-3 py-4 text-on-primary transition-transform duration-200 lg:z-40 lg:translate-x-0 ${open ? 'translate-x-0' : '-translate-x-full'}`}>
    <div className="mb-5 flex items-center gap-2 px-1">
    <Link to="/admin/dashboard" onClick={onClose} className="flex min-w-0 flex-1 items-center gap-3 rounded-2xl bg-white/10 p-2.5 ring-1 ring-inset ring-white/10">
      <span className="flex h-11 w-11 shrink-0 items-center justify-center overflow-hidden rounded-[14px] bg-white">
        <img src={logoWeb} alt="Nhà Sách Tri Thức" className="h-9 w-9 object-contain" />
      </span>
      <div className="min-w-0">
        <p className="truncate text-sm font-bold leading-5">Nhà Sách Tri Thức</p>
        <p className="truncate text-xs text-on-primary/60">Không gian quản trị</p>
      </div>
    </Link>
    <button type="button" onClick={onClose} aria-label="Đóng menu" className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-on-primary/70 hover:bg-white/10 lg:hidden"><Icon name="x" /></button>
    </div>

    <nav className="flex-1 space-y-5 overflow-y-auto px-1 pb-4" aria-label="Điều hướng quản trị">
      {menuGroups.map((group) => (
        <div key={group.label}>
          <p className="mb-2 px-3 text-xs font-semibold text-on-primary/50">{group.label}</p>
          <div className="space-y-1">
            {group.items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={onClose}
                className={({ isActive }) =>
                  `group flex min-h-11 items-center gap-3 rounded-full px-3.5 text-sm font-semibold transition ${isActive ? 'bg-secondary-container text-on-secondary-container' : 'text-on-primary/75 hover:bg-primary-container hover:text-on-primary'}`
                }
              >
                {({ isActive }) => (
                  <>
                    <span className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-[10px] ${isActive ? 'bg-on-secondary-container/10 text-on-secondary-container' : 'bg-white/10 text-on-primary/70 group-hover:bg-white/15 group-hover:text-on-primary'}`}>
                      <Icon name={item.icon} className="h-[18px] w-[18px]" />
                    </span>
                    <span>{item.label}</span>
                  </>
                )}
              </NavLink>
            ))}
          </div>
        </div>
      ))}
    </nav>

    <div className="mt-auto border-t border-white/10 px-1 pt-4">
      <Link to="/" onClick={onClose} className="flex min-h-11 items-center gap-3 rounded-full bg-white/10 px-4 text-sm font-semibold text-on-primary ring-1 ring-inset ring-white/10 transition hover:bg-primary-container">
        <Icon name="arrow" className="h-4 w-4 rotate-180" />
        Trở về cửa hàng
      </Link>
    </div>
  </aside>
  </>
);

export default AdminSidebar;
