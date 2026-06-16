import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { AccentButton, Container, EmptyState, formatVnd, Icon, IconButton, Panel, PrimaryButton, SecondaryButton, SectionHeading } from '@/components/ui/staticUi';
import cartService from '@/features/cart/services/cartService';

const CartPage = () => {
  const [cartData, setCartData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadCart = async () => {
    setLoading(true);
    setError('');
    try {
      setCartData(await cartService.getCart());
    } catch {
      setError('Không thể tải giỏ hàng. Vui lòng đăng nhập hoặc thử lại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadCart(); }, []);

  const updateQuantity = async (bookId: number, quantity: number) => {
    if (quantity < 1) return;
    setCartData(await cartService.updateCartItem(bookId, quantity));
  };

  const removeItem = async (bookId: number) => {
    setCartData(await cartService.removeFromCart(bookId));
  };

  const items = cartData?.items || [];
  const total = cartData?.totalPrice || 0;

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Giỏ hàng" title="Giỏ hàng của bạn" description="Kiểm tra sản phẩm, điều chỉnh số lượng và chuyển sang thanh toán." />

      {error ? <div className="mb-5 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}

      {loading ? (
        <div className="h-64 animate-pulse rounded-xl bg-surface-container" />
      ) : items.length === 0 ? (
        <EmptyState title="Giỏ hàng đang trống" description="Chọn vài cuốn sách yêu thích để bắt đầu đơn hàng." action={<Link to="/catalog"><PrimaryButton>Tiếp tục mua sắm</PrimaryButton></Link>} />
      ) : (
        <div className="grid gap-6 lg:grid-cols-[1fr_360px]">
          <div className="space-y-4">
            {items.map((item: any) => (
              <Panel key={item.bookId || item.id} className="p-4">
                <div className="grid gap-4 sm:grid-cols-[96px_1fr_auto] sm:items-center">
                  <img src={item.book?.cover || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=240&q=80'} alt={item.book?.title || 'Sách'} className="h-32 w-24 rounded-lg object-cover shadow" />
                  <div>
                    <Link to={`/books/${item.bookId}`} className="text-lg font-bold text-primary hover:text-secondary">{item.book?.title || 'Sách'}</Link>
                    <p className="mt-1 text-sm text-on-surface-variant">{formatVnd(item.price || item.book?.price)}</p>
                    <div className="mt-4 flex items-center gap-2">
                      <IconButton onClick={() => updateQuantity(item.bookId, item.quantity - 1)}>-</IconButton>
                      <span className="w-10 text-center text-sm font-bold">{item.quantity}</span>
                      <IconButton onClick={() => updateQuantity(item.bookId, item.quantity + 1)}>+</IconButton>
                    </div>
                  </div>
                  <div className="text-left sm:text-right">
                    <p className="text-lg font-bold text-primary">{formatVnd((item.price || 0) * item.quantity)}</p>
                    <button onClick={() => removeItem(item.bookId)} className="mt-3 inline-flex items-center gap-2 text-sm font-bold text-error hover:underline">
                      <Icon name="trash" className="h-4 w-4" /> Xóa
                    </button>
                  </div>
                </div>
              </Panel>
            ))}
          </div>

          <Panel className="h-fit p-5 lg:sticky lg:top-28">
            <h2 className="text-lg font-bold text-primary">Tóm tắt đơn hàng</h2>
            <div className="mt-5 space-y-3 border-b border-outline-variant pb-5 text-sm">
              <div className="flex justify-between"><span className="text-on-surface-variant">Tạm tính</span><span className="font-bold text-primary">{formatVnd(total)}</span></div>
              <div className="flex justify-between"><span className="text-on-surface-variant">Phí vận chuyển</span><span className="font-bold text-emerald-700">Miễn phí</span></div>
              <div className="flex justify-between"><span className="text-on-surface-variant">Voucher</span><button className="font-bold text-secondary hover:underline">Áp dụng mã</button></div>
            </div>
            <div className="mt-5 flex justify-between text-lg font-bold text-primary"><span>Tổng cộng</span><span>{formatVnd(total)}</span></div>
            <Link to="/checkout" className="mt-5 block"><AccentButton className="w-full">Thanh toán</AccentButton></Link>
            <Link to="/catalog" className="mt-3 block"><SecondaryButton className="w-full">Tiếp tục mua sắm</SecondaryButton></Link>
          </Panel>
        </div>
      )}
    </Container>
  );
};

export default CartPage;
