import type { ReactNode } from 'react';

import AdminHeader from './AdminHeader';
import Footer from './Footer';
import Header from './Header';
import Sidebar from './Sidebar';

type LayoutProps = {
  children: ReactNode;
};

export const MainLayout = ({ children }: LayoutProps) => (
  <div className="min-h-screen bg-background text-on-surface">
    <Header />
    <main className="min-h-[70vh]">{children}</main>
    <Footer />
  </div>
);

export const AdminLayout = ({ children }: LayoutProps) => (
  <div className="min-h-screen bg-background text-on-surface">
    <Sidebar />
    <div className="min-h-screen lg:pl-64">
      <AdminHeader />
      <main className="px-4 py-8 sm:px-6 lg:px-8">{children}</main>
    </div>
  </div>
);

export const BlankLayout = ({ children }: LayoutProps) => (
  <div className="min-h-screen bg-background text-on-surface">{children}</div>
);

export default MainLayout;
