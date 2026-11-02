import { useState, type ReactNode } from 'react';

import AdminHeader from '@/features/admin/components/AdminHeader';
import AdminSidebar from '@/features/admin/components/AdminSidebar';

const AdminLayout = ({ children }: { children: ReactNode }) => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className="admin-shell min-h-screen text-on-surface">
      <AdminSidebar open={menuOpen} onClose={() => setMenuOpen(false)} />
      <div className="min-h-screen min-w-0 lg:pl-72">
        <AdminHeader onMenuOpen={() => setMenuOpen(true)} />
        <main className="min-w-0 px-4 pb-10 pt-6 sm:px-6 sm:pb-12 sm:pt-8 xl:px-10 2xl:px-12">{children}</main>
      </div>
    </div>
  );
};

export default AdminLayout;
