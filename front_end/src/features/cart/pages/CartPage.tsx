import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import bookCoverPlaceholder from '@/assets/icons/book-cover-placeholder.svg';
import { Container, EmptyState, formatVnd, Icon, IconButton, LinkButton, Panel, SectionHeading } from '@/components/ui/staticUi';
import cartService, { Cart } from '@/features/cart/services/cartService';

const CartPage = () => {
  const [cartData, setCartData] = useState<Cart | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [pendingBookId, setPendingBookId] = useState<number | null>(null);

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
    if (quantity < 1 || pendingBookId !== null) return;
    setError('');
    setPendingBookId(bookId);
    try {
      setCartData(await cartService.updateCartItem(bookId, quantity));
    } catch {
      setError('Không thể cập nhật số lượng. Vui lòng thử lại.');
    } finally {
      setPendingBookId(null);
    }
  };

  const removeItem = async (bookId: number) => {
    if (pendingBookId !== null) return;
    setError('');
    setPendingBookId(bookId);
    try {
      setCartData(await cartService.removeFromCart(bookId));
    } catch {
      setError('Không thể xóa sách khỏi giỏ hàng. Vui lòng thử lại.');
    } finally {
      setPendingBookId(null);
    }
  };

  const items = cartData?.items || [];
  const total = cartData?.totalPrice || 0;

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Giỏ hàng" title="Giỏ hàng của bạn" description="Kiểm tra sản phẩm, điều chỉnh số lượng và chuyển sang thanh toán." />

      {error ? <div role="alert" className="mb-5 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}

      {loading ? (
        <div className="h-64 animate-pulse rounded-xl bg-surface-container" />
      ) : items.length === 0 ? (
        <EmptyState title="Giỏ hàng đang trống" description="Chọn vài cuốn sách yêu thích để bắt đầu đơn hàng." action={<LinkButton to="/catalog">Tiếp tục mua sắm</LinkButton>} />
      ) : (
        <div className="grid min-w-0 gap-6 xl:grid-cols-[minmax(0,1fr)_360px]">
          <div className="min-w-0 space-y-4">
            {items.map((item) => (
              <Panel key={item.bookId || item.id} className="p-4">
                <div className="grid min-w-0 gap-4 sm:grid-cols-[96px_minmax(0,1fr)] lg:grid-cols-[96px_minmax(0,1fr)_auto] lg:items-center">
                  <img src={item.book?.cover || bookCoverPlaceholder} onError={(event) => { event.currentTarget.src = bookCoverPlaceholder; }} alt={item.book?.title || 'Sách'} loading="lazy" width="96" height="128" className="h-32 w-24 rounded-lg bg-white object-contain shadow" />
                  <div className="min-w-0">
                    <Link to={`/books/${item.bookId}`} className="block break-words text-lg font-bold leading-snug text-primary hover:text-secondary">{item.book?.title || 'Sách'}</Link>
                    <p className="mt-1 text-sm text-on-surface-variant">{formatVnd(item.price || item.book?.price)}</p>
                    <div className="mt-4 flex items-center gap-2">
                      <IconButton aria-label={`Giảm số lượng ${item.book?.title || 'sách'}`} disabled={pendingBookId !== null || item.quantity <= 1} onClick={() => updateQuantity(item.bookId, item.quantity - 1)}>-</IconButton>
                      <span className="w-10 text-center text-sm font-bold" aria-live="polite">{item.quantity}</span>
                      <IconButton aria-label={`Tăng số lượng ${item.book?.title || 'sách'}`} disabled={pendingBookId !== null} onClick={() => updateQuantity(item.bookId, item.quantity + 1)}>+</IconButton>
                    </div>
                  </div>
                  <div className="min-w-0 text-left sm:col-start-2 lg:col-start-auto lg:text-right">
                    <p className="text-lg font-bold text-primary">{formatVnd((item.price || 0) * item.quantity)}</p>
                    <button disabled={pendingBookId !== null} onClick={() => removeItem(item.bookId)} className="mt-3 inline-flex min-h-11 items-center gap-2 text-sm font-bold text-error hover:underline disabled:cursor-not-allowed disabled:opacity-60">
                      <Icon name="trash" className="h-4 w-4" /> {pendingBookId === item.bookId ? 'Đang xóa...' : 'Xóa'}
                    </button>
                  </div>
                </div>
              </Panel>
            ))}
          </div>

          <Panel className="h-fit p-5 lg:sticky lg:top-28">
            <h2 className="text-lg font-bold text-primary">Tóm tắt đơn hàng</h2>
            <div className="mt-5 space-y-3 border-b border-outline-variant pb-5 text-sm">
              <div className="flex min-w-0 justify-between gap-3"><span className="min-w-0 text-on-surface-variant">Tạm tính</span><span className="shrink-0 text-right font-bold text-primary">{formatVnd(total)}</span></div>
              <div className="flex min-w-0 justify-between gap-3"><span className="min-w-0 text-on-surface-variant">Phí vận chuyển</span><span className="shrink-0 text-right font-bold text-emerald-700">Miễn phí</span></div>
              <div className="flex min-w-0 justify-between gap-3"><span className="min-w-0 text-on-surface-variant">Voucher</span><Link to="/checkout" className="text-right font-bold text-secondary hover:underline">Chọn khi thanh toán</Link></div>
            </div>
            <div className="mt-5 flex min-w-0 justify-between gap-3 text-lg font-bold text-primary"><span>Tổng cộng</span><span className="text-right">{formatVnd(total)}</span></div>
            <LinkButton to="/checkout" variant="accent" className="mt-5 w-full">Thanh toán</LinkButton>
            <LinkButton to="/catalog" variant="secondary" className="mt-3 w-full">Tiếp tục mua sắm</LinkButton>
          </Panel>
        </div>
      )}
    </Container>
  );
};

export default CartPage;
