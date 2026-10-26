import { Link } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
import { Icon, IconButton } from '@/components/ui/staticUi';

const AdminHeader = ({ onMenuOpen }: { onMenuOpen: () => void }) => (
  <header className="sticky top-0 z-30 border-b border-outline-variant bg-surface/95 backdrop-blur">
    <div className="flex h-16 items-center gap-3 px-4 sm:px-6 xl:px-10 2xl:px-12">
      <IconButton className="shrink-0 lg:hidden" aria-label="Mở menu quản trị" onClick={onMenuOpen}><Icon name="menu" /></IconButton>
      <Link to="/admin/dashboard" className="flex items-center gap-3 font-bold text-primary lg:hidden">
        <span className="flex h-10 w-10 items-center justify-center overflow-hidden rounded-lg border border-outline-variant bg-white shadow-sm">
          <img src={logoWeb} alt="Nhà Sách Tri Thức" className="h-full w-full object-cover" />
        </span>
        Quản trị
      </Link>

      <div className="hidden min-w-0 flex-1 lg:block">
        <p className="text-sm font-semibold text-on-surface">Không gian quản trị</p>
        <p className="text-xs text-on-surface-variant">Theo dõi và vận hành nhà sách</p>
      </div>

      <div className="ml-auto flex items-center gap-2">
        <Link to="/admin/users" className="flex min-h-10 items-center gap-3 rounded-md border border-outline-variant bg-surface px-2.5 py-1.5 transition hover:bg-surface-container-low sm:px-3">
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
