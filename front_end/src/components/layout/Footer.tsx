import { Link } from 'react-router-dom';

const Footer = () => {
    return (
        <footer className="bg-primary dark:bg-surface-container text-on-primary dark:text-on-surface mt-section-gap">
            <div className="max-w-container-max mx-auto px-gutter py-section-gap">
                {/* Footer Content Grid */}
                <div className="grid grid-cols-1 md:grid-cols-4 gap-gutter mb-section-gap">
                    {/* Company Info */}
                    <div>
                        <h3 className="font-h3 text-h3 text-on-primary dark:text-on-surface mb-stack-md">
                            BookStore
                        </h3>
                        <p className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80">
                            Khơi nguồn tri thức, mở lối tương lai
                        </p>
                        <p className="font-caption text-caption text-on-primary/60 dark:text-on-surface/60 mt-stack-md">
                            © 2024 BookStore. All rights reserved.
                        </p>
                    </div>

                    {/* About */}
                    <div>
                        <h4 className="font-label-md text-label-md text-on-primary dark:text-on-surface mb-stack-md font-bold">
                            Về chúng tôi
                        </h4>
                        <ul className="space-y-stack-sm">
                            <li>
                                <Link
                                    to="/about"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Giới thiệu
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/blog"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Blog
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/career"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Tuyển dụng
                                </Link>
                            </li>
                        </ul>
                    </div>

                    {/* Support */}
                    <div>
                        <h4 className="font-label-md text-label-md text-on-primary dark:text-on-surface mb-stack-md font-bold">
                            Hỗ trợ
                        </h4>
                        <ul className="space-y-stack-sm">
                            <li>
                                <Link
                                    to="/contact"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Liên hệ
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/faq"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    FAQ
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/shipping"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Vận chuyển
                                </Link>
                            </li>
                        </ul>
                    </div>

                    {/* Legal */}
                    <div>
                        <h4 className="font-label-md text-label-md text-on-primary dark:text-on-surface mb-stack-md font-bold">
                            Pháp lý
                        </h4>
                        <ul className="space-y-stack-sm">
                            <li>
                                <Link
                                    to="/privacy"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Chính sách bảo mật
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/terms"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Điều khoản dịch vụ
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/cookies"
                                    className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80 hover:text-on-primary hover:underline transition-colors"
                                >
                                    Chính sách Cookie
                                </Link>
                            </li>
                        </ul>
                    </div>
                </div>

                {/* Divider */}
                <div className="border-t border-on-primary/20 dark:border-on-surface/20 pt-stack-lg">
                    {/* Social & Contact */}
                    <div className="flex flex-col md:flex-row items-center justify-between gap-stack-md">
                        <p className="font-body-md text-body-md text-on-primary/80 dark:text-on-surface/80">
                            📧 contact@bookstore.com | 📞 1-800-BOOK-STORE
                        </p>

                        {/* Social Links */}
                        <div className="flex gap-stack-md">
                            <a
                                href="https://facebook.com"
                                className="w-10 h-10 flex items-center justify-center bg-on-primary/10 dark:bg-on-surface/10 rounded-full hover:bg-on-primary/20 dark:hover:bg-on-surface/20 transition-colors"
                                title="Facebook"
                                aria-label="Facebook"
                            >
                                <svg
                                    className="w-5 h-5 text-on-primary dark:text-on-surface"
                                    fill="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                                </svg>
                            </a>
                            <a
                                href="https://twitter.com"
                                className="w-10 h-10 flex items-center justify-center bg-on-primary/10 dark:bg-on-surface/10 rounded-full hover:bg-on-primary/20 dark:hover:bg-on-surface/20 transition-colors"
                                title="Twitter"
                                aria-label="Twitter"
                            >
                                <svg
                                    className="w-5 h-5 text-on-primary dark:text-on-surface"
                                    fill="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path d="M23 3a10.9 10.9 0 01-3.14 1.53 4.48 4.48 0 00-7.86 3v1A10.66 10.66 0 013 4s-4 9 5 13a11.64 11.64 0 01-7 2s9 5 20 5a9.5 9.5 0 00-9-5.5c4.75 2.25 7-7 7-7a10.6 10.6 0 01-9-5.5z" />
                                </svg>
                            </a>
                            <a
                                href="https://instagram.com"
                                className="w-10 h-10 flex items-center justify-center bg-on-primary/10 dark:bg-on-surface/10 rounded-full hover:bg-on-primary/20 dark:hover:bg-on-surface/20 transition-colors"
                                title="Instagram"
                                aria-label="Instagram"
                            >
                                <svg
                                    className="w-5 h-5 text-on-primary dark:text-on-surface"
                                    fill="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.204-.012 3.584-.07 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zM5.838 12a6.162 6.162 0 1112.324 0 6.162 6.162 0 01-12.324 0zM12 16a4 4 0 110-8 4 4 0 010 8zm4.965-10.322a1.44 1.44 0 110-2.881 1.44 1.44 0 010 2.881z" />
                                </svg>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
