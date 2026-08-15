import type { ReactNode } from 'react';

import Footer from './Footer';
import Header from './Header';

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

export const BlankLayout = ({ children }: LayoutProps) => (
  <div className="min-h-screen bg-background text-on-surface">{children}</div>
);

export default MainLayout;
