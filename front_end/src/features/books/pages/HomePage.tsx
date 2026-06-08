import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Icon } from '@/components/ui/staticUi';
import bookService, { Book, Category } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';

const HomePage = () => {
    const [featuredBooks, setFeaturedBooks] = useState<Book[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                setError('');
                // Lấy danh sách sách nổi bật
                const books = await bookService.getFeaturedBooks(5);
                setFeaturedBooks(books);
                
                // Lấy danh sách danh mục
                const cats = await bookService.getCategories();
                setCategories(cats);
            } catch (err: any) {
                console.error('Error fetching homepage data:', err);
                setError('Không thể tải dữ liệu. Vui lòng thử lại sau.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    const handleAddToCart = async (bookId: number) => {
        try {
            await cartService.addToCart(bookId, 1);
            alert('Đã thêm vào giỏ hàng');
        } catch (err) {
            alert('Lỗi khi thêm vào giỏ hàng');
        }
    };

    const icon = (name: string, className = 'w-5 h-5') => {
        const icons: any = {
            search: (
                <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
            ),
            favorite: <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.53L12 21.35z" />,
            shopping_cart: (
                <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2 8m10 0l2-8m0 0h2m-2 0h-2m0 8h-4m0 0h4"
                />
            ),
            account_circle: (
                <>
                    <circle cx="12" cy="8" r="4" />
                    <path d="M12 14c-6 0-8 3-8 3v3h16v-3s-2-3-8-3z" />
                </>
            ),
            menu: <path d="M4 6h16M4 12h16M4 18h16" />,
            arrow_forward: <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />,
            add_shopping_cart: (
                <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                />
            ),
            menu_book: (
                <>
                    <path d="M12 6.253v13m0-13C6.5 6.253 2 10.753 2 16.253v4m10-13c5.5 0 10 4.5 10 10.253v4M2 20.253v4h20v-4" />
                </>
            ),
        };
        return (
            <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {icons[name]}
            </svg>
        );
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="text-center">
                    <p className="text-error font-body-lg mb-4">{error}</p>
                    <button onClick={() => window.location.reload()} className="bg-primary text-on-primary px-6 py-2 rounded-lg">
                        Tải lại trang
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="antialiased text-on-surface">
            <section className="max-w-container-max mx-auto px-gutter py-section-gap">
                <div className="flex justify-between items-end mb-stack-lg">
                    <div>
                        <h2 className="font-h2 text-h2 text-primary">Sách hot tuần này</h2>
                        <p className="font-body-md text-body-md text-on-surface-variant mt-stack-sm">
                            Những tựa sách được tìm kiếm và mua nhiều nhất
                        </p>
                    </div>
                    <Link
                        to="/catalog"
                        className="text-primary font-label-md text-label-md hover:underline flex items-center gap-unit"
                    >
                        Xem tất cả
                        {icon('arrow_forward', 'w-4 h-4')}
                    </Link>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-4 gap-gutter">
                    {featuredBooks.length > 0 && (
                        <div className="md:col-span-2 md:row-span-2 bg-surface-container-lowest rounded-xl shadow-[0px_4px_12px_rgba(30,27,75,0.05)] hover:shadow-[0px_8px_24px_rgba(30,27,75,0.1)] transition-all duration-300 overflow-hidden group flex flex-col">
                            <div className="relative h-64 md:h-full bg-surface-container flex items-center justify-center p-stack-lg overflow-hidden">
                                <img
                                    alt={featuredBooks[0].title}
                                    className="h-full object-contain rounded drop-shadow-md group-hover:scale-105 transition-transform duration-500 z-10"
                                    src={featuredBooks[0].cover || 'https://via.placeholder.com/300x400'}
                                />
                                <div className="absolute inset-0 bg-gradient-to-t from-primary/10 to-transparent" />
                                {featuredBooks[0].discount && (
                                    <span className="absolute top-stack-md left-stack-md bg-secondary-container text-on-secondary font-caption text-caption px-3 py-1 rounded-full z-20">
                                        -{featuredBooks[0].discount}%
                                    </span>
                                )}
                            </div>
                            <div className="p-stack-md bg-surface-container-lowest z-20">
                                <Link to={`/books/${featuredBooks[0].id}`}>
                                    <h3 className="font-h3 text-h3 text-primary mb-unit line-clamp-1 hover:underline">
                                        {featuredBooks[0].title}
                                    </h3>
                                </Link>
                                <p className="font-caption text-caption text-on-surface-variant mb-stack-sm">
                                    Tác giả: {featuredBooks[0].author?.name || 'N/A'}
                                </p>
                                <div className="flex items-center justify-between mt-stack-md">
                                    <span className="font-h3 text-h3 text-[#f97316]">
                                        {featuredBooks[0].price?.toLocaleString('vi-VN')} ₫
                                    </span>
                                    <button
                                        onClick={() => handleAddToCart(featuredBooks[0].id || 0)}
                                        className="bg-primary text-on-primary font-label-md text-label-md py-2 px-4 rounded-lg hover:bg-primary-container transition-colors shadow-sm flex items-center gap-unit"
                                    >
                                        {icon('add_shopping_cart', 'w-5 h-5')}
                                        Thêm
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    {featuredBooks.slice(1).map((book) => (
                        <div
                            key={book.id}
                            className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_12px_rgba(30,27,75,0.05)] hover:shadow-[0px_8px_24px_rgba(30,27,75,0.1)] transition-all duration-300 overflow-hidden group flex flex-col p-stack-md"
                        >
                            <div className="relative h-48 bg-surface-container rounded-lg flex items-center justify-center p-stack-sm mb-stack-md overflow-hidden">
                                <img
                                    alt={book.title}
                                    className="h-full object-contain rounded drop-shadow-sm group-hover:scale-105 transition-transform duration-500"
                                    src={book.cover || 'https://via.placeholder.com/200x300'}
                                />
                            </div>
                            <div className="flex-grow flex flex-col justify-end">
                                <Link to={`/books/${book.id}`}>
                                    <h3 className="font-body-lg text-body-lg text-primary mb-unit font-bold line-clamp-1 hover:underline">
                                        {book.title}
                                    </h3>
                                </Link>
                                <p className="font-caption text-caption text-on-surface-variant mb-stack-sm line-clamp-1">
                                    {book.author?.name || 'N/A'}
                                </p>
                                <div className="flex items-center justify-between mt-auto pt-stack-sm border-t border-surface-variant">
                                    <span className="font-label-md text-label-md text-primary font-bold">
                                        {book.price?.toLocaleString('vi-VN')} ₫
                                    </span>
                                    <button
                                        onClick={() => handleAddToCart(book.id || 0)}
                                        title="Add to cart"
                                        className="text-primary hover:text-secondary-container transition-colors p-1"
                                    >
                                        {icon('add_shopping_cart', 'w-5 h-5')}
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            <section className="max-w-container-max mx-auto px-gutter py-section-gap">
                <div className="bg-primary rounded-2xl overflow-hidden shadow-[0px_12px_48px_rgba(30,27,75,0.15)] relative flex flex-col md:flex-row items-center">
                    <div
                        className="absolute inset-0 opacity-10 pointer-events-none"
                        style={{ backgroundImage: 'radial-gradient(circle at 2px 2px, white 1px, transparent 0)', backgroundSize: '24px 24px' }}
                    />
                    <div className="p-stack-lg md:p-[64px] flex-1 z-10 text-on-primary">
                        <span className="bg-[#f97316] text-white font-label-md text-label-md px-3 py-1 rounded-full font-bold mb-stack-md inline-block uppercase tracking-wider text-[10px]">
                            Flash Sale
                        </span>
                        <h2 className="font-h1 text-h1 mb-stack-sm text-white">Giảm Giá 50%</h2>
                        <p className="font-h3 text-h3 text-primary-fixed-dim mb-stack-lg font-normal">Toàn bộ Tác phẩm Kinh điển</p>

                        <div className="flex gap-stack-md mb-stack-lg flex-wrap">
                            {[
                                ['03', 'Ngày'],
                                ['14', 'Giờ'],
                                ['45', 'Phút'],
                            ].map(([value, label]) => (
                                <div key={label} className="flex flex-col items-center bg-white/10 backdrop-blur-sm rounded-lg p-3 min-w-[70px]">
                                    <span className="font-h2 text-h2 font-bold leading-none">{value}</span>
                                    <span className="font-caption text-caption text-primary-fixed-dim mt-1">{label}</span>
                                </div>
                            ))}
                        </div>

                        <button className="bg-white text-primary font-label-md text-label-md py-3 px-8 rounded-lg font-bold hover:bg-surface-variant transition-colors shadow-sm">
                            Mua ngay
                        </button>
                    </div>

                    <div className="w-full md:w-2/5 h-64 md:h-auto self-stretch relative">
                        <img
                            alt="Classic Books"
                            className="w-full h-full object-cover"
                            src="https://lh3.googleusercontent.com/aida-public/AB6AXuCFXPSlmyi2ZTEOuUGkj9QiJ0wqTCPgjA7CqmMsL2agOeE-nYHu5NVjE5Fo1U2JtT3fQlqxzpwnKDrp8llADTT5gTfwhetnBJtSbBksjMkrQT3lKDxrK4-VDNdsasvd5-yZtAxCrYDNc3GWdt27U9Mhxml2AA0wKE325f1sZ8GhJ34P1sO5yvXMfIJFsuOVTXwceaNj1VY6vPDfi7-l4YdK3zS7e2OweEvtl13yhiP-g5eVsfDtEZvb-fdGRkXsa5XgX0EJz6M-H1M"
                        />
                        <div className="absolute inset-0 bg-gradient-to-r from-primary to-transparent hidden md:block" />
                        <div className="absolute inset-0 bg-gradient-to-t from-primary to-transparent md:hidden" />
                    </div>
                </div>
            </section>

            <section className="bg-surface-container-low py-section-gap">
                <div className="max-w-container-max mx-auto px-gutter">
                    <div className="text-center mb-stack-lg">
                        <h2 className="font-h2 text-h2 text-primary">Danh mục nổi bật</h2>
                        <p className="font-body-md text-body-md text-on-surface-variant mt-stack-sm">
                            Khám phá các chủ đề được yêu thích nhất
                        </p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-gutter">
                        {categories.map((category) => (
                            <Link key={category.id} to={`/catalog?category=${category.id}`}>
                                <div className="relative h-64 rounded-xl overflow-hidden group cursor-pointer shadow-[0px_4px_12px_rgba(30,27,75,0.05)] hover:shadow-[0px_8px_24px_rgba(30,27,75,0.1)] transition-shadow">
                                    <div className="absolute inset-0 w-full h-full object-cover group-hover:scale-110 transition-transform duration-700 bg-gradient-to-br from-primary/30 to-secondary/30" />
                                    <div className="absolute inset-0 bg-primary/40 group-hover:bg-primary/50 transition-colors" />
                                    <div className="absolute inset-0 p-stack-lg flex flex-col justify-end">
                                        <div className="bg-white/20 backdrop-blur-md rounded-lg p-stack-md border border-white/30 transform translate-y-2 group-hover:translate-y-0 transition-transform">
                                            <div className="flex items-center justify-between">
                                                <h3 className="font-h3 text-h3 text-white">{category.name}</h3>
                                            </div>
                                            <p className="font-caption text-caption text-white/80 mt-unit opacity-0 group-hover:opacity-100 transition-opacity delay-100">
                                                {category.bookCount || 0}+ sách
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>
            </section>

            <section className="max-w-container-max mx-auto px-gutter py-section-gap flex flex-col items-center text-center">
                <span className="mb-stack-sm text-primary">{icon('menu_book', 'w-10 h-10')}</span>
                <h2 className="font-h2 text-h2 text-primary mb-stack-md">Sứ mệnh của chúng tôi</h2>
                <p className="font-body-lg text-body-lg text-on-surface-variant max-w-2xl mx-auto leading-relaxed">
                    BookStore được xây dựng với niềm tin rằng mỗi cuốn sách là một cánh cửa mở ra thế giới mới. Chúng tôi cam kết
                    mang đến không gian mua sắm sách trực tuyến hiện đại, tiện lợi, tuyển chọn những tác phẩm giá trị nhất để đồng
                    hành cùng quá trình phát triển tri thức của bạn.
                </p>

                <div className="mt-stack-lg flex gap-stack-md justify-center flex-wrap">
                    {[
                        ['10K+', 'Tựa sách'],
                        ['50K+', 'Khách hàng'],
                        ['4.9/5', 'Đánh giá'],
                    ].map(([value, label], index) => (
                        <div key={label} className={`flex flex-col items-center px-6 ${index < 2 ? 'border-r border-outline-variant' : ''}`}>
                            <span className="font-h2 text-h2 text-primary font-bold">{value}</span>
                            <span className="font-caption text-caption text-on-surface-variant mt-1 uppercase tracking-wider">{label}</span>
                        </div>
                    ))}
                </div>
            </section>
        </div>
    );
};

export default HomePage;
