import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import bookService from '@/features/books/services/bookService';
import type { Book } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';
import wishlistService from '@/features/wishlist/services/wishlistService';
import { useAuth } from '@/hooks/useAuth';

const BookDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const [book, setBook] = useState<Book | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [isInWishlist, setIsInWishlist] = useState(false);
    const [adding, setAdding] = useState(false);

    useEffect(() => {
        const fetchBook = async () => {
            try {
                setLoading(true);
                setError('');
                const bookData = await bookService.getBookById(Number(id));
                setBook(bookData);

                // Kiểm tra wishlist
                if (isAuthenticated) {
                    const inWishlist = await wishlistService.isInWishlist(Number(id));
                    setIsInWishlist(inWishlist);
                }
            } catch (err: any) {
                setError('Không thể tải chi tiết sách');
            } finally {
                setLoading(false);
            }
        };

        if (id) fetchBook();
    }, [id, isAuthenticated]);

    const handleAddToCart = async () => {
        try {
            setAdding(true);
            await cartService.addToCart(Number(id), quantity);
            alert('Đã thêm vào giỏ hàng');
            setQuantity(1);
        } catch (err) {
            alert('Lỗi khi thêm vào giỏ hàng');
        } finally {
            setAdding(false);
        }
    };

    const handleToggleWishlist = async () => {
        if (!isAuthenticated) {
            navigate('/login');
            return;
        }

        try {
            if (isInWishlist) {
                await wishlistService.removeFromWishlist(Number(id));
            } else {
                await wishlistService.addToWishlist(Number(id));
            }
            setIsInWishlist(!isInWishlist);
        } catch (err) {
            alert('Lỗi khi cập nhật wishlist');
        }
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
                    <Link to="/catalog" className="text-primary hover:underline">← Quay lại danh sách</Link>
                </div>
            </div>
        );
    }

    if (!book) return null;

    return (
        <div className="w-full">
            {/* Breadcrumb */}
            <div className="max-w-container-max mx-auto px-gutter py-stack-md">
                <nav className="flex gap-unit text-body-md text-on-surface-variant">
                    <Link to="/" className="hover:text-primary transition-colors">Trang chủ</Link>
                    <span>/</span>
                    <Link to="/catalog" className="hover:text-primary transition-colors">Sách</Link>
                    <span>/</span>
                    <span className="text-on-surface font-bold">{book.title}</span>
                </nav>
            </div>

            {/* Main Content */}
            <section className="max-w-container-max mx-auto px-gutter py-section-gap">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-gutter">
                    {/* Book Image & Actions */}
                    <div className="md:col-span-1">
                        <div className="relative bg-surface-container-lowest rounded-xl overflow-hidden shadow-lg mb-stack-lg">
                            <img
                                alt={book.title}
                                className="w-full aspect-[3/4] object-cover"
                                src={book.cover || 'https://via.placeholder.com/300x400'}
                            />
                            {book.discount && book.discount > 0 && (
                                <div className="absolute top-stack-lg right-stack-lg bg-secondary-container text-on-secondary-container font-label-md text-label-md px-3 py-1 rounded-full">
                                    -{book.discount}%
                                </div>
                            )}
                        </div>

                        {/* Action Buttons */}
                        <div className="space-y-stack-md">
                            <button
                                disabled={adding}
                                onClick={handleAddToCart}
                                className="w-full bg-primary text-on-primary font-label-md text-label-md py-3 px-4 rounded-lg hover:bg-primary-container transition-colors shadow-sm flex items-center justify-center gap-unit disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {adding ? 'Đang thêm...' : 'Thêm vào giỏ hàng'}
                            </button>

                            <button
                                onClick={handleToggleWishlist}
                                className={`w-full font-label-md text-label-md py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-unit ${isInWishlist
                                    ? 'bg-secondary-container text-on-secondary-container'
                                    : 'bg-surface-container-low text-primary border border-outline-variant hover:bg-surface-container'
                                    }`}
                            >
                                {isInWishlist ? '♥ Đã lưu' : '♡ Lưu sách'}
                            </button>
                        </div>

                        {/* Quantity */}
                        <div className="mt-stack-lg border border-outline-variant rounded-lg p-stack-md">
                            <label className="block mb-unit font-label-md text-label-md text-on-surface">Số lượng</label>
                            <div className="flex items-center gap-unit">
                                <button
                                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                    className="w-10 h-10 border border-outline-variant rounded hover:bg-surface-container"
                                >
                                    −
                                </button>
                                <span className="flex-1 text-center">{quantity}</span>
                                <button
                                    onClick={() => setQuantity(quantity + 1)}
                                    className="w-10 h-10 border border-outline-variant rounded hover:bg-surface-container"
                                >
                                    +
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Book Details */}
                    <div className="md:col-span-2">
                        <h1 className="font-h2 text-h2 text-primary mb-stack-md">{book.title}</h1>

                        {/* Author & Category */}
                        <div className="grid grid-cols-2 gap-gutter mb-stack-lg">
                            <div>
                                <p className="font-caption text-caption text-on-surface-variant mb-unit">Tác giả</p>
                                <p className="font-body-lg text-body-lg">{book.author?.name || 'N/A'}</p>
                            </div>
                            <div>
                                <p className="font-caption text-caption text-on-surface-variant mb-unit">Danh mục</p>
                                <p className="font-body-lg text-body-lg">{book.category?.name || 'N/A'}</p>
                            </div>
                        </div>

                        {/* Rating */}
                        {book.rating !== undefined && (
                            <div className="mb-stack-lg">
                                <p className="font-label-md text-label-md text-on-surface-variant mb-unit">Đánh giá</p>
                                <div className="flex items-center gap-unit">
                                    <span className="font-h3 text-h3 text-primary">{book.rating?.toFixed(1)}/5</span>
                                    <span className="font-body-md text-body-md text-on-surface-variant">({book.reviews || 0} bình luận)</span>
                                </div>
                            </div>
                        )}

                        {/* Price */}
                        <div className="bg-surface-container-low rounded-xl p-stack-lg mb-stack-lg">
                            <p className="font-h1 text-h1 text-[#f97316]">
                                {book.price?.toLocaleString('vi-VN')} ₫
                            </p>
                            {book.discount && book.discount > 0 && (
                                <p className="font-caption text-caption text-on-surface-variant mt-unit">
                                    Tiết kiệm {book.discount}%
                                </p>
                            )}
                        </div>

                        {/* Description */}
                        <div>
                            <h3 className="font-h3 text-h3 text-on-surface mb-stack-md">Mô tả sách</h3>
                            <p className="font-body-md text-body-md text-on-surface-variant leading-relaxed">
                                {book.description || 'Chưa có mô tả cho sách này.'}
                            </p>
                        </div>

                        {/* Stock Info */}
                        {book.stock !== undefined && (
                            <div className="mt-stack-lg pt-stack-lg border-t border-outline-variant">
                                <p className={`font-body-md ${book.stock > 0 ? 'text-green-600' : 'text-error'}`}>
                                    {book.stock > 0 ? `Còn ${book.stock} cuốn` : 'Hết hàng'}
                                </p>
                            </div>
                        )}
                    </div>
                </div>
            </section>
        </div>
    );
};

export default BookDetailPage;
