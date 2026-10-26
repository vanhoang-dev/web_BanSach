import { useState, type ReactNode } from 'react';

import AdminHeader from '@/features/admin/components/AdminHeader';
import AdminSidebar from '@/features/admin/components/AdminSidebar';

const AdminLayout = ({ children }: { children: ReactNode }) => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-background text-on-surface">
      <AdminSidebar open={menuOpen} onClose={() => setMenuOpen(false)} />
      <div className="min-h-screen min-w-0 lg:pl-64">
        <AdminHeader onMenuOpen={() => setMenuOpen(true)} />
        <main className="min-w-0 px-4 py-6 sm:px-6 sm:py-8 xl:px-10 2xl:px-12">{children}</main>
      </div>
    </div>
  );
};

export default AdminLayout;
