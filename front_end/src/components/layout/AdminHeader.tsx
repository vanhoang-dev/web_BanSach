import { Link } from 'react-router-dom';

import { Icon, IconButton } from '@/components/ui/staticUi';

const AdminHeader = () => (
  <header className="sticky top-0 z-30 border-b border-outline-variant bg-surface/95 backdrop-blur">
    <div className="flex h-16 items-center gap-4 px-4 sm:px-6 lg:px-8">
      <Link to="/admin/dashboard" className="flex items-center gap-3 font-bold text-primary lg:hidden">
        <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-on-primary">
          <Icon name="book" />
        </span>
        Quản trị
      </Link>

      <div className="relative hidden flex-1 md:block">
        <Icon name="search" className="pointer-events-none absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-outline" />
        <input
          className="h-11 w-full max-w-lg rounded-lg border border-outline-variant bg-surface-container-low pl-10 pr-4 text-sm outline-none transition focus:border-primary focus:bg-surface focus:ring-2 focus:ring-primary/20"
          placeholder="Tìm đơn hàng, sách, người dùng..."
        />
      </div>

      <div className="ml-auto flex items-center gap-2">
        <IconButton aria-label="Thông báo">
          <Icon name="bell" />
        </IconButton>
        <Link to="/admin/users" className="flex items-center gap-3 rounded-lg border border-outline-variant bg-surface px-3 py-2 shadow-sm">
          <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/5 text-primary">
            <Icon name="user" className="h-4 w-4" />
          </span>
          <span className="hidden text-sm font-bold text-on-surface sm:block">Quản trị viên</span>
        </Link>
      </div>
    </div>
  </header>
);

export default AdminHeader;
