import { useState } from 'react';
import { NavLink, Link } from 'react-router-dom';

type SidebarProps = {
    isOpen?: boolean;
};

const Sidebar = ({ isOpen = true }: SidebarProps) => {
    const [collapsed, setCollapsed] = useState(false);

    const menuItems = [
        { icon: 'dashboard', label: 'Tổng quan', to: '/admin/dashboard' },
        { icon: 'book', label: 'Sách', to: '/admin/books' },
        { icon: 'category', label: 'Danh mục', to: '/admin/categories' },
        { icon: 'shopping_bag', label: 'Đơn hàng', to: '/admin/orders' },
        { icon: 'group', label: 'Người dùng', to: '/admin/users' },
        { icon: 'inventory', label: 'Kho hàng', to: '/admin/inventory' },
        { icon: 'confirmation_number', label: 'Voucher', to: '/admin/vouchers' },
        { icon: 'person', label: 'Tác giả', to: '/admin/authors' },
    ];

    const linkClassName = ({ isActive }: { isActive: boolean }) =>
        `flex items-center gap-4 px-4 py-3 mx-2 rounded-lg transition-all duration-150 ${isActive
            ? 'bg-primary-container text-secondary-container'
            : 'text-on-primary-container dark:text-on-surface-variant hover:bg-primary-container dark:hover:bg-surface-container-high hover:text-secondary-container'
        }`;

    if (!isOpen) {
        return null;
    }

    return (
        <aside
            className={`fixed left-0 top-0 h-full flex flex-col z-40 bg-primary dark:bg-surface-container-lowest text-on-primary dark:text-primary font-label-md text-label-md h-screen w-64 shadow-md transition-all duration-300 ${collapsed ? 'w-20' : 'w-64'
                }`}
        >
            {/* Header */}
            <div className="px-6 py-4 border-b border-on-primary-fixed-variant">
                <Link to="/admin/dashboard" className="block">
                    <h2 className={`font-h3 text-h3 text-primary-fixed dark:text-primary ${collapsed ? 'hidden' : 'block'}`}>
                        Admin Panel
                    </h2>
                    <p className={`font-caption text-caption text-on-primary-container mt-1 ${collapsed ? 'hidden' : 'block'}`}>
                        Hệ thống quản trị
                    </p>
                </Link>
            </div>

            {/* Navigation */}
            <nav className="flex-1 py-4 flex flex-col gap-1 overflow-y-auto">
                {menuItems.map((item, idx) => (
                    <NavLink
                        key={idx}
                        to={item.to}
                        className={linkClassName}
                        title={collapsed ? item.label : ''}
                    >
                        {/* Icon - SVG simplified representations */}
                        <svg
                            className="w-6 h-6 flex-shrink-0"
                            fill="currentColor"
                            viewBox="0 0 24 24"
                        >
                            {item.icon === 'dashboard' && (
                                <path d="M3 3h8v8H3V3zm10 0h8v8h-8V3zM3 13h8v8H3v-8zm10 0h8v8h-8v-8z" />
                            )}
                            {item.icon === 'book' && (
                                <path d="M4 6h16v2H4V6zm0 5h16v2H4v-2zm0 5h16v2H4v-2z" />
                            )}
                            {item.icon === 'category' && (
                                <path d="M12 2L2 7v10c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V7l-10-5z" />
                            )}
                            {item.icon === 'shopping_bag' && (
                                <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z" />
                            )}
                            {item.icon === 'group' && (
                                <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.64 2.2 1.56 2.97 2.54.6.81 1.23 1.6 2 2.1h6v-2.5c0-2.33-4.67-3.5-7-3.5z" />
                            )}
                            {item.icon === 'inventory' && (
                                <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z" />
                            )}
                            {item.icon === 'confirmation_number' && (
                                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2V17zm4 0h-2V7h2V17zm4 0h-2v-4h2V17z" />
                            )}
                        </svg>
                        {!collapsed && <span>{item.label}</span>}
                    </NavLink>
                ))}
            </nav>

            {/* Profile */}
            <Link to="/admin/users" className={`p-6 border-t border-on-primary-fixed-variant flex ${collapsed ? 'justify-center' : 'items-center gap-4'}`}>
                <img
                    alt="Admin Profile"
                    className="w-10 h-10 rounded-full border border-primary-fixed-dim flex-shrink-0"
                    src="https://via.placeholder.com/40"
                />
                {!collapsed && (
                    <div>
                        <p className="font-label-md text-label-md text-on-primary">Admin User</p>
                        <p className="font-caption text-caption text-on-primary-container line-clamp-1">
                            admin@bookstore.com
                        </p>
                    </div>
                )}
            </Link>

            {/* Toggle Button */}
            <button
                className="px-4 py-2 border-t border-on-primary-fixed-variant text-on-primary-container hover:text-secondary-container transition-colors"
                onClick={() => setCollapsed(!collapsed)}
                title={collapsed ? 'Expand' : 'Collapse'}
            >
                <svg
                    className="w-5 h-5 mx-auto"
                    fill="currentColor"
                    viewBox="0 0 24 24"
                >
                    {collapsed ? (
                        <path d="M8 5v14l11-7z" />
                    ) : (
                        <path d="M5 8v14l11-7z" />
                    )}
                </svg>
            </button>
        </aside>
    );
};

export default Sidebar;
