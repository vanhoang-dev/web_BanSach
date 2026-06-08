import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Panel } from '@/components/ui/staticUi';
import cartService from '@/features/cart/services/cartService';

const CartPage = () => {
    const [cartData, setCartData] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const loadCart = async () => {
        try {
            setLoading(true);
            setError('');
            const data = await cartService.getCart();
            setCartData(data);
        } catch (err: any) {
            setError('Không thể tải giỏ hàng');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCart();
    }, []);

    const handleUpdateQuantity = async (bookId: number, quantity: number) => {
        if (quantity < 1) return;
        try {
            const updated = await cartService.updateCartItem(bookId, quantity);
            setCartData(updated);
        } catch (err) {
            alert('Lỗi khi cập nhật số lượng');
        }
    };

    const handleRemoveItem = async (bookId: number) => {
        try {
            const updated = await cartService.removeFromCart(bookId);
            setCartData(updated);
        } catch (err) {
            alert('Lỗi khi xóa sản phẩm');
        }
    };

    const handleClearCart = async () => {
        if (window.confirm('Bạn chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
            try {
                await cartService.clearCart();
                setCartData({ items: [], totalPrice: 0 });
            } catch (err) {
                alert('Lỗi khi xóa giỏ hàng');
            }
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
        );
    }

    const items = cartData?.items || [];
    const total = cartData?.totalPrice || 0;

    return (
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <h1 className="font-h1 text-h1 text-primary mb-stack-lg">Giỏ hàng của bạn</h1>

            {error && (
                <div className="bg-error/10 border border-error rounded-lg p-stack-md mb-stack-lg">
                    <p className="font-body-md text-error">{error}</p>
                </div>
            )}

            {items.length === 0 ? (
                <Panel className="p-stack-lg text-center">
                    <p className="font-body-lg text-body-lg text-on-surface-variant mb-stack-md">Giỏ hàng của bạn trống</p>
                    <Link to="/catalog" className="text-primary hover:underline">
                        ← Tiếp tục mua sắm
                    </Link>
                </Panel>
            ) : (
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-gutter">
                    <div className="lg:col-span-2 space-y-stack-md">
                        {items.map((item: any) => (
                            <Panel key={item.bookId || item.id} className="p-stack-md">
                                <div className="grid grid-cols-4 gap-gutter items-start">
                                    {/* Book Image */}
                                    <div className="col-span-1">
                                        <div className="bg-surface-container rounded-lg h-32 flex items-center justify-center">
                                            <img
                                                alt={item.book?.title || 'Book'}
                                                className="h-full object-contain rounded"
                                                src={item.book?.cover || 'https://via.placeholder.com/100x150'}
                                            />
                                        </div>
                                    </div>

                                    {/* Book Info */}
                                    <div className="col-span-2">
                                        <Link to={`/books/${item.bookId}`}>
                                            <h3 className="font-body-lg text-body-lg text-primary font-bold hover:underline mb-unit">
                                                {item.book?.title || 'N/A'}
                                            </h3>
                                        </Link>
                                        <p className="font-body-md text-body-md text-on-surface-variant mb-stack-md">
                                            {item.book?.price?.toLocaleString('vi-VN')} ₫
                                        </p>

                                        {/* Quantity Controls */}
                                        <div className="flex items-center gap-unit">
                                            <button
                                                onClick={() => handleUpdateQuantity(item.bookId, item.quantity - 1)}
                                                className="w-8 h-8 border border-outline-variant rounded hover:bg-surface-container text-sm"
                                            >
                                                −
                                            </button>
                                            <span className="w-12 text-center">{item.quantity}</span>
                                            <button
                                                onClick={() => handleUpdateQuantity(item.bookId, item.quantity + 1)}
                                                className="w-8 h-8 border border-outline-variant rounded hover:bg-surface-container text-sm"
                                            >
                                                +
                                            </button>
                                        </div>
                                    </div>

                                    {/* Price & Remove */}
                                    <div className="col-span-1 text-right">
                                        <p className="font-h3 text-h3 text-primary mb-stack-md">
                                            {(item.price * item.quantity)?.toLocaleString('vi-VN') || 0} ₫
                                        </p>
                                        <button
                                            onClick={() => handleRemoveItem(item.bookId)}
                                            className="text-error hover:text-error-container text-sm font-body-md"
                                        >
                                            Xóa
                                        </button>
                                    </div>
                                </div>
                            </Panel>
                        ))}
                    </div>

                    {/* Checkout Summary */}
                    <div className="lg:col-span-1">
                        <Panel className="p-stack-lg sticky top-24">
                            <h3 className="font-h3 text-h3 text-on-surface mb-stack-md">Tóm tắt đơn hàng</h3>

                            <div className="space-y-unit mb-stack-lg pb-stack-lg border-b border-outline-variant">
                                <div className="flex justify-between font-body-md text-body-md">
                                    <span>Tạm tính:</span>
                                    <span>{total?.toLocaleString('vi-VN')} ₫</span>
                                </div>
                                <div className="flex justify-between font-body-md text-body-md">
                                    <span>Phí vận chuyển:</span>
                                    <span>Miễn phí</span>
                                </div>
                            </div>

                            <div className="flex justify-between font-h3 text-h3 text-primary mb-stack-lg">
                                <span>Tổng cộng:</span>
                                <span>{total?.toLocaleString('vi-VN')} ₫</span>
                            </div>

                            <Link
                                to="/checkout"
                                className="block w-full text-center bg-primary text-on-primary font-label-md text-label-md py-3 rounded-lg hover:bg-primary-container transition-colors mb-unit"
                            >
                                Tiến hành thanh toán
                            </Link>

                            <button
                                onClick={handleClearCart}
                                className="w-full border border-outline-variant text-on-surface font-label-md text-label-md py-3 rounded-lg hover:bg-surface-container transition-colors"
                            >
                                Xóa giỏ hàng
                            </button>

                            <Link
                                to="/catalog"
                                className="block text-center text-primary hover:underline mt-stack-md font-body-md"
                            >
                                ← Tiếp tục mua sắm
                            </Link>
                        </Panel>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CartPage;
