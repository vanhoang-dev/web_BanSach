import { useState } from 'react';
import { Link } from 'react-router-dom';

const AdminHeader = () => {
    const [searchQuery, setSearchQuery] = useState('');

    return (
        <header className="bg-surface dark:bg-background w-full top-0 sticky z-50 shadow-md dark:bg-surface-container transition-all duration-200 ease-in-out border-b border-surface-variant">
            <div className="max-w-full mx-auto px-gutter flex items-center justify-between h-20">
                {/* Brand */}
                <div className="flex-shrink-0">
                    <Link className="font-h2 text-h2 text-primary dark:text-primary-fixed-dim tracking-tight text-2xl font-bold" to="/">
                        BookStore
                    </Link>
                </div>

                {/* Search (centered) */}
                <div className="flex flex-1 max-w-md mx-gutter">
                    <div className="w-full relative">
                        <input
                            className="w-full bg-surface-container-low border border-outline-variant rounded-full py-2 pl-4 pr-10 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary font-body-md text-body-md text-on-surface transition-colors"
                            placeholder="Tìm kiếm người dùng..."
                            type="text"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                        />
                        <svg
                            className="absolute right-3 top-2.5 text-on-surface-variant pointer-events-none"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                            width="20"
                            height="20"
                        >
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                        </svg>
                    </div>
                </div>

                {/* Admin Actions - Right Side */}
                <div className="flex items-center gap-stack-md text-primary dark:text-primary-fixed-dim">
                    <Link
                        to="/admin/dashboard"
                        className="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors flex items-center justify-center"
                        title="Dashboard"
                        aria-label="Dashboard"
                    >
                        <svg fill="currentColor" viewBox="0 0 24 24" width="24" height="24">
                            <path d="M3 3h8v8H3V3zm10 0h8v8h-8V3zM3 13h8v8H3v-8zm10 0h8v8h-8v-8z" />
                        </svg>
                    </Link>

                    <Link
                        to="/admin/orders"
                        className="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors flex items-center justify-center relative"
                        title="Orders"
                        aria-label="Orders"
                    >
                        <svg fill="currentColor" viewBox="0 0 24 24" width="24" height="24">
                            <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z" />
                        </svg>
                        <span className="absolute top-1 right-1 w-2 h-2 bg-secondary-container rounded-full"></span>
                    </Link>

                    <Link
                        to="/admin/users"
                        className="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors flex items-center justify-center"
                        title="Users"
                        aria-label="Users"
                    >
                        <svg fill="currentColor" viewBox="0 0 24 24" width="24" height="24">
                            <circle cx="12" cy="8" r="4" />
                            <path d="M12 14c-6 0-8 3-8 3v3h16v-3s-2-3-8-3z" />
                        </svg>
                    </Link>
                </div>
            </div>
        </header>
    );
};

export default AdminHeader;
