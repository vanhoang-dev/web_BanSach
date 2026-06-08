import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Panel } from '@/components/ui/staticUi';
import wishlistService from '@/features/wishlist/services/wishlistService';
import cartService from '@/features/cart/services/cartService';

const WishlistPage = () => {
    const [wishlist, setWishlist] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchWishlist = async () => {
            try {
                setLoading(true);
                setError('');
                const data = await wishlistService.getWishlist();
                setWishlist(Array.isArray(data) ? data : data?.items || []);
            } catch (err: any) {
                setError('Không thể tải danh sách yêu thích');
            } finally {
                setLoading(false);
            }
        };

        fetchWishlist();
    }, []);

    const handleRemove = async (bookId: number) => {
        try {
            await wishlistService.removeFromWishlist(bookId);
            setWishlist(prev => prev.filter(item => item.id !== bookId));
        } catch (err) {
            alert('Lỗi khi xóa từ danh sách yêu thích');
        }
    };

    const handleAddToCart = async (bookId: number) => {
        try {
            await cartService.addToCart(bookId, 1);
            alert('Đã thêm vào giỏ hàng');
        } catch (err) {
            alert('Lỗi khi thêm vào giỏ hàng');
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
        );
    }

    return (
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <h1 className="font-h1 text-h1 text-primary mb-stack-lg">Danh sách yêu thích</h1>

            {error && (
                <div className="bg-error/10 border border-error rounded-lg p-stack-md mb-stack-lg">
                    <p className="font-body-md text-error">{error}</p>
                </div>
            )}

            {wishlist.length === 0 ? (
                <Panel className="p-stack-lg text-center">
                    <p className="font-body-lg text-body-lg text-on-surface-variant mb-stack-md">
                        Danh sách yêu thích của bạn trống
                    </p>
                    <Link to="/catalog" className="text-primary hover:underline">
                        ← Khám phá sách
                    </Link>
                </Panel>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-gutter">
                    {wishlist.map((item: any) => (
                        <Panel key={item.id} className="p-stack-md flex flex-col">
                            {/* Book Image */}
                            <div className="bg-surface-container rounded-lg h-48 flex items-center justify-center mb-stack-md overflow-hidden">
                                <img
                                    alt={item.title}
                                    className="h-full object-contain rounded"
                                    src={item.cover || 'https://via.placeholder.com/150x200'}
                                />
                            </div>

                            {/* Book Info */}
                            <div className="flex-grow">
                                <Link to={`/books/${item.id}`}>
                                    <h3 className="font-body-lg text-body-lg text-primary font-bold hover:underline mb-unit">
                                        {item.title}
                                    </h3>
                                </Link>
                                <p className="font-caption text-caption text-on-surface-variant mb-stack-md">
                                    {item.author?.name || 'N/A'}
                                </p>
                                <p className="font-h3 text-h3 text-[#f97316] mb-stack-md">
                                    {item.price?.toLocaleString('vi-VN')} ₫
                                </p>
                            </div>

                            {/* Actions */}
                            <div className="space-y-unit pt-stack-md border-t border-outline-variant">
                                <button
                                    onClick={() => handleAddToCart(item.id)}
                                    className="w-full bg-primary text-on-primary font-label-md text-label-md py-2 px-4 rounded-lg hover:bg-primary-container transition-colors"
                                >
                                    Thêm vào giỏ
                                </button>
                                <button
                                    onClick={() => handleRemove(item.id)}
                                    className="w-full border border-error text-error font-label-md text-label-md py-2 px-4 rounded-lg hover:bg-error/10 transition-colors"
                                >
                                    Xóa
                                </button>
                            </div>
                        </Panel>
                    ))}
                </div>
            )}
        </div>
    );
};

export default WishlistPage;
