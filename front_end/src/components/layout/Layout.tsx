import { ReactNode } from 'react';
import Header from './Header';
import AdminHeader from './AdminHeader';
import Footer from './Footer';
import Sidebar from './Sidebar';

interface LayoutProps {
    children: ReactNode;
}

interface AdminLayoutProps {
    children: ReactNode;
    sidebarOpen?: boolean;
}

/**
 * MainLayout - Trang chính (với header + footer)
 */
export const MainLayout = ({ children }: LayoutProps) => {
    return (
        <div className="min-h-screen bg-background text-on-background font-body-md flex flex-col">
            <Header />
            <main className="flex-grow">
                {children}
            </main>
            <Footer />
        </div>
    );
};

/**
 * AdminLayout - Trang admin (với sidebar + header)
 */
export const AdminLayout = ({ children, sidebarOpen = true }: AdminLayoutProps) => {
    return (
        <div className="min-h-screen bg-background text-on-background font-body-md">
            {sidebarOpen && <Sidebar isOpen={true} />}
            <div className={sidebarOpen ? 'ml-64' : ''}>
                <AdminHeader />
                <main className="min-h-screen p-gutter">
                    {children}
                </main>
            </div>
        </div>
    );
};

/**
 * BlankLayout - Trang trống (chỉ có nội dung, không header/footer/sidebar)
 */
export const BlankLayout = ({ children }: LayoutProps) => {
    return (
        <div className="min-h-screen bg-background text-on-background font-body-md">
            {children}
        </div>
    );
};

export default MainLayout;
