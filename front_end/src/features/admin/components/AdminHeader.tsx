import { Link, useLocation } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
import { Icon, IconButton } from '@/components/ui/staticUi';

const pageLabels: Record<string, string> = {
  '/admin/dashboard': 'Tổng quan',
  '/admin/books': 'Sách',
  '/admin/categories': 'Danh mục',
  '/admin/authors': 'Tác giả',
  '/admin/orders': 'Đơn hàng',
  '/admin/inventory': 'Tồn kho',
  '/admin/vouchers': 'Voucher',
  '/admin/users': 'Người dùng',
};

const AdminHeader = ({ onMenuOpen }: { onMenuOpen: () => void }) => {
  const { pathname } = useLocation();
  const currentPage = pageLabels[pathname] || 'Quản trị';

  return (
    <header className="sticky top-0 z-30 px-4 pt-3 sm:px-6 sm:pt-4 xl:px-10 2xl:px-12">
      <div className="flex h-16 items-center gap-3 rounded-full border border-[#e5e5e2] bg-white/95 px-3 backdrop-blur sm:px-4">
        <IconButton className="h-10 w-10 shrink-0 !rounded-full !border-[#e5e5e2] lg:hidden" aria-label="Mở menu quản trị" onClick={onMenuOpen}><Icon name="menu" /></IconButton>
        <Link to="/admin/dashboard" className="flex min-w-0 items-center gap-2.5 font-bold text-[#141414] lg:hidden">
          <span className="flex h-9 w-9 shrink-0 items-center justify-center overflow-hidden rounded-[12px] bg-[#f1f1ef]">
            <img src={logoWeb} alt="" className="h-8 w-8 object-contain" />
          </span>
          <span className="hidden sm:inline">Quản trị</span>
        </Link>

        <div className="hidden min-w-0 flex-1 items-center gap-2 lg:flex">
          <span className="text-sm text-[#858580]">Quản trị</span>
          <span className="text-[#b8b8b3]" aria-hidden="true">/</span>
          <span className="truncate text-sm font-semibold text-[#141414]">{currentPage}</span>
        </div>

        <div className="ml-auto flex items-center">
          <Link to="/admin/users" className="flex min-h-10 items-center gap-2 rounded-full bg-[#f2f2ef] py-1 pl-1 pr-3 text-[#141414] transition hover:bg-[#e9e9e6]">
            <span className="flex h-8 w-8 items-center justify-center rounded-full bg-[#141414] text-white">
              <Icon name="user" className="h-4 w-4" />
            </span>
            <span className="hidden text-sm font-semibold sm:block">Quản trị viên</span>
          </Link>
        </div>
      </div>
    </header>
  );
};

export default AdminHeader;
