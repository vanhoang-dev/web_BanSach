import { useState } from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    return (
        <header className="bg-surface dark:bg-background w-full top-0 sticky z-50 shadow-md shadow-sm dark:bg-surface-container transition-all duration-200 ease-in-out">
            <div className="max-w-[1280px] mx-auto px-6 flex items-center justify-between h-20">
                {/* Brand */}
                <div className="flex-shrink-0">
                    <Link
                        to="/"
                        className="font-h2 text-h2 text-primary dark:text-primary-fixed-dim tracking-tight text-2xl font-bold"
                    >
                        BookStore
                    </Link>
                </div>

                {/* Search (hidden on mobile) */}
                <div className="hidden md:flex flex-1 max-w-md mx-6 relative">
                    <input
                        className="w-full bg-surface-container-low border border-outline-variant rounded-full py-2 pl-4 pr-10 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary font-body-md text-body-md text-on-surface transition-colors"
                        placeholder="Tìm kiếm sách, tác giả..."
                        type="text"
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

                {/* Navigation Links (hidden on mobile) */}
                <nav className="hidden md:flex items-center gap-6">
                    <Link
                        to="/new-books"
                        className="font-body-md text-body-md text-secondary dark:text-secondary-fixed font-bold border-b-2 border-secondary hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors px-2 py-1"
                    >
                        Sách mới
                    </Link>
                    <Link
                        to="/categories"
                        className="font-body-md text-body-md text-on-surface-variant dark:text-outline hover:text-primary hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors px-2 py-1"
                    >
                        Danh mục
                    </Link>
                    <Link
                        to="/authors"
                        className="font-body-md text-body-md text-on-surface-variant dark:text-outline hover:text-primary hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors px-2 py-1"
                    >
                        Tác giả
                    </Link>
                    <Link
                        to="/promotions"
                        className="font-body-md text-body-md text-on-surface-variant dark:text-outline hover:text-primary hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors px-2 py-1"
                    >
                        Khuyến mãi
                    </Link>
                </nav>

                {/* Trailing Icons */}
                <div className="flex items-center gap-4 text-primary dark:text-primary-fixed-dim">
                    <Link to="/wishlist" title="Wishlist" aria-label="Wishlist">
                        <button
                            className="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors flex items-center justify-center"
                        >
                            <svg
                                fill="currentColor"
                                viewBox="0 0 24 24"
                                width="24"
                                height="24"
                            >
                                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                            </svg>
                        </button>
                    </Link>

                    <Link to="/cart" title="Shopping Cart" aria-label="Shopping Cart" className="relative">
                        <button
                            className="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors flex items-center justify-center"
                        >
                            <svg
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                                width="24"
                                height="24"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2 8m10 0l2-8m0 0h2m-2 0h-2m0 8h-4m0 0h4"
                                />
                            </svg>
                        </button>
                        <span className="absolute top-1 right-1 w-2 h-2 bg-secondary-container rounded-full"></span>
                    </Link>

                    <Link to="/profile" title="Profile" aria-label="Profile">
                        <button
                            className="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-high transition-colors flex items-center justify-center"
                        >
                            <svg
                                fill="currentColor"
                                viewBox="0 0 24 24"
                                width="24"
                                height="24"
                            >
                                <circle cx="12" cy="8" r="4" />
                                <path d="M12 14c-6 0-8 3-8 3v3h16v-3s-2-3-8-3z" />
                            </svg>
                        </button>
                    </Link>

                    {/* Mobile Menu Toggle */}
                    <button
                        className="md:hidden p-2 rounded-full hover:bg-surface-container-low transition-colors flex items-center justify-center"
                        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                        title="Menu"
                        aria-label="Menu"
                    >
                        <svg
                            fill="currentColor"
                            viewBox="0 0 24 24"
                            width="24"
                            height="24"
                        >
                            <path d="M3 5h18v2H3V5zm0 7h18v2H3v-2zm0 7h18v2H3v-2z" />
                        </svg>
                    </button>
                </div>
            </div>

            {/* Mobile Menu */}
            {isMobileMenuOpen && (
                <div className="md:hidden bg-surface-container-low border-t border-surface-variant">
                    <div className="px-6 py-4 flex flex-col gap-4">
                        <Link className="font-body-md text-on-surface hover:text-primary transition-colors" to="/new-books">
                            Sách mới
                        </Link>
                        <Link className="font-body-md text-on-surface hover:text-primary transition-colors" to="/categories">
                            Danh mục
                        </Link>
                        <Link className="font-body-md text-on-surface hover:text-primary transition-colors" to="/authors">
                            Tác giả
                        </Link>
                        <Link className="font-body-md text-on-surface hover:text-primary transition-colors" to="/promotions">
                            Khuyến mãi
                        </Link>
                    </div>
                </div>
            )}
        </header>
    );
};

export default Header;
