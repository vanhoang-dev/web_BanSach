import { FormEvent, useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';

import { AccentButton, Container, Field, Icon, Panel, SectionHeading } from '@/components/ui/staticUi';
import { env } from '@/config/env';
import authService from '@/features/auth/services';
import bookService from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';
import orderService from '@/features/orders/services/orderService';
import paymentService from '@/features/payment/services/paymentService';
import type { PaymentResponse } from '@/features/payment/services/paymentService';
import voucherService, { type Voucher } from '@/features/vouchers/services/voucherService';
import { tokenStorage } from '@/services/storage/tokenStorage';

type InlinePayment = PaymentResponse & {
  orderId: number;
};

type CheckoutItem = {
  bookId: number;
  title: string;
  cover?: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
};

type ConfirmedTotals = {
  voucherCode?: string;
  voucherDiscount: number;
  totalAmount: number;
};

const successStatuses = ['SUCCESS', 'PAID', 'COMPLETED'];
const failedStatuses = ['FAILED', 'CANCELLED'];
const formatVnd = (value: number) => `${Number(value || 0).toLocaleString('vi-VN')}đ`;

const statusLabel = (status?: string) => {
  switch ((status || 'PENDING').toUpperCase()) {
    case 'SUCCESS':
    case 'PAID':
    case 'COMPLETED':
      return 'Đã thanh toán';
    case 'FAILED':
      return 'Thanh toán thất bại';
    case 'CANCELLED':
      return 'Đã hủy';
    default:
      return 'Đang chờ thanh toán';
  }
};

const CheckoutPage = () => {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const bookId = Number(params.get('bookId') || 0);
  const quantity = Number(params.get('quantity') || 1);
  const [form, setForm] = useState({
    receiverName: '',
    receiverPhone: '',
    shippingAddress: '',
    shippingMethod: 'STANDARD',
    shippingFee: 0,
    voucherCode: '',
    paymentMethod: 'COD',
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [paymentInfo, setPaymentInfo] = useState<InlinePayment | null>(null);
  const [paymentCompleted, setPaymentCompleted] = useState(false);
  const [myVouchers, setMyVouchers] = useState<Voucher[]>([]);
  const [vouchersLoading, setVouchersLoading] = useState(true);
  const [vouchersError, setVouchersError] = useState('');
  const [useVoucher, setUseVoucher] = useState(false);
  const [checkoutItems, setCheckoutItems] = useState<CheckoutItem[]>([]);
  const [itemsLoading, setItemsLoading] = useState(true);
  const [itemsError, setItemsError] = useState('');
  const [confirmedTotals, setConfirmedTotals] = useState<ConfirmedTotals | null>(null);
  const paymentId = paymentInfo?.paymentId;
  const orderId = paymentInfo?.orderId;
  const currentPaymentStatus = String(paymentInfo?.status || 'PENDING').toUpperCase();

  const itemsSubtotal = useMemo(
    () => checkoutItems.reduce((total, item) => total + item.subtotal, 0),
    [checkoutItems]
  );
  const selectedVoucher = useVoucher
    ? myVouchers.find((voucher) => voucher.code === form.voucherCode)
    : undefined;
  const estimatedVoucherDiscount = selectedVoucher
    ? Math.min(itemsSubtotal * selectedVoucher.discountPercent / 100, Number(selectedVoucher.maxDiscount || 0))
    : 0;
  const originalTotal = itemsSubtotal + Number(form.shippingFee || 0);
  const displayedVoucherDiscount = confirmedTotals?.voucherDiscount ?? estimatedVoucherDiscount;
  const displayedTotal = confirmedTotals?.totalAmount ?? Math.max(0, originalTotal - displayedVoucherDiscount);

  const update = (name: string, value: string) => setForm((current) => ({ ...current, [name]: value }));

  useEffect(() => {
    let cancelled = false;

    const fillShippingInformation = async () => {
      try {
        const profile = await authService.getProfile();
        if (cancelled) return;

        setForm((current) => ({
          ...current,
          receiverName: current.receiverName.trim() ? current.receiverName : profile?.fullName || '',
          receiverPhone: current.receiverPhone.trim() ? current.receiverPhone : profile?.phone || '',
          shippingAddress: current.shippingAddress.trim() ? current.shippingAddress : profile?.address || '',
        }));
      } catch {
        // Checkout remains usable when the profile is unavailable; missing fields
        // can still be entered manually by the customer.
      }
    };

    fillShippingInformation();

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    let cancelled = false;

    const loadCheckoutItems = async () => {
      setItemsLoading(true);
      setItemsError('');
      try {
        if (bookId) {
          const book = await bookService.getBookById(bookId);
          if (!cancelled) {
            const unitPrice = Number(book.price || 0);
            setCheckoutItems([{
              bookId,
              title: book.title,
              cover: book.cover,
              unitPrice,
              quantity,
              subtotal: unitPrice * quantity,
            }]);
          }
        } else {
          const cart = await cartService.getCart();
          if (!cancelled) {
            setCheckoutItems(cart.items.map((item) => {
              const unitPrice = Number(item.price ?? item.book?.price ?? 0);
              const itemQuantity = Number(item.quantity || 0);
              return {
                bookId: item.bookId,
                title: item.book?.title || `Sách #${item.bookId}`,
                cover: item.book?.cover,
                unitPrice,
                quantity: itemQuantity,
                subtotal: Number(item.subtotal ?? unitPrice * itemQuantity),
              };
            }));
          }
        }
      } catch {
        if (!cancelled) setItemsError('Không thể tải thông tin sách trong đơn hàng.');
      } finally {
        if (!cancelled) setItemsLoading(false);
      }
    };

    loadCheckoutItems();
    return () => {
      cancelled = true;
    };
  }, [bookId, quantity]);

  useEffect(() => {
    let cancelled = false;

    voucherService.getMyVouchers(0, 100)
      .then((response) => {
        if (!cancelled) {
          setMyVouchers(response.data.content || []);
          setVouchersError('');
        }
      })
      .catch(() => {
        if (!cancelled) {
          setMyVouchers([]);
          setVouchersError('Không thể tải kho voucher. Vui lòng tải lại trang để thử lại.');
        }
      })
      .finally(() => {
        if (!cancelled) setVouchersLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (!orderId || paymentCompleted || successStatuses.includes(currentPaymentStatus)) return;

    const token = tokenStorage.getToken();
    if (!token) {
      setError('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại để theo dõi thanh toán.');
      return;
    }

    const streamUrl = `${env.apiBaseUrl}/api/payment/sse/order/${orderId}?token=${encodeURIComponent(token)}`;
    const source = new EventSource(streamUrl, { withCredentials: true });

    const handlePaymentEvent = (event: MessageEvent) => {
      try {
        const notification = JSON.parse(event.data);
        const status = String(notification.status || notification.data?.status || 'PENDING').toUpperCase();
        const nextPaymentId = Number(notification.entityId || notification.data?.paymentId || paymentId || 0);
        const nextAmount = Number(notification.data?.amount || paymentInfo?.amount || 0);

        setPaymentInfo((current) => current ? {
          ...current,
          paymentId: nextPaymentId || current.paymentId,
          amount: nextAmount || current.amount,
          status,
          transactionId: notification.data?.transactionId || current.transactionId,
          orderId: current.orderId,
        } : current);

        if (successStatuses.includes(status)) {
          source.close();
          setPaymentCompleted(true);
          setMessage('Thanh toán thành công. Đang chuyển tới chi tiết đơn hàng...');
          window.setTimeout(() => navigate(`/orders/${orderId}`), 1800);
        } else if (failedStatuses.includes(status)) {
          setError('Thanh toán không thành công. Vui lòng thử lại hoặc chọn phương thức khác.');
        }
      } catch {
        setError('Không thể đọc trạng thái thanh toán realtime.');
      }
    };

    source.addEventListener('payment', handlePaymentEvent);
    source.onerror = () => {
      setError('Kết nối theo dõi thanh toán realtime bị gián đoạn. Vui lòng mở chi tiết đơn hàng để kiểm tra trạng thái.');
      source.close();
    };

    return () => source.close();
  }, [currentPaymentStatus, navigate, orderId, paymentCompleted, paymentId, paymentInfo?.amount]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');
    setPaymentInfo(null);
    setPaymentCompleted(false);
    if (!form.receiverName || !form.receiverPhone || !form.shippingAddress) {
      setError('Vui lòng nhập đầy đủ thông tin giao hàng.');
      return;
    }
    try {
      setLoading(true);
      const payload = {
        receiverName: form.receiverName,
        receiverPhone: form.receiverPhone,
        shippingAddress: form.shippingAddress,
        shippingMethod: form.shippingMethod,
        shippingFee: Number(form.shippingFee || 0),
        voucherCode: useVoucher && form.voucherCode ? form.voucherCode : undefined,
      };
      const order = bookId
        ? await orderService.buyNow({ ...payload, bookId, quantity })
        : await orderService.createOrder(payload);

      setConfirmedTotals({
        voucherCode: payload.voucherCode,
        voucherDiscount: Number(order.voucherDiscount || 0),
        totalAmount: Number(order.totalAmount ?? order.totalPrice ?? 0),
      });

      if (payload.voucherCode) {
        setMyVouchers((current) => current.filter((voucher) => voucher.code !== payload.voucherCode));
        setUseVoucher(false);
        update('voucherCode', '');
      }

      if (form.paymentMethod === 'SEPAY' && order.id) {
        const payment = await paymentService.initiatePayment({
          orderId: order.id,
          amount: Number(order.totalAmount || order.totalPrice || 0),
          returnUrl: `${window.location.origin}/orders/${order.id}`,
          description: `Thanh toán đơn hàng #${order.id}`,
        });
        if (payment.paymentUrl) {
          setPaymentInfo({ ...payment, orderId: order.id });
          setMessage('Đơn hàng đã được tạo. Vui lòng quét mã QR để hoàn tất thanh toán.');
          return;
        }
        setMessage(payment.message || 'Đã khởi tạo thanh toán. Kiểm tra trạng thái trong chi tiết đơn hàng.');
        return;
      }

      setMessage('Đặt hàng thành công. Đang chuyển tới danh sách đơn hàng...');
      window.setTimeout(() => navigate('/orders'), 1200);
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || 'Không thể tạo đơn hàng. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  const paymentStatus = String(paymentInfo?.status || 'PENDING').toUpperCase();
  const isPaid = successStatuses.includes(paymentStatus);

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Thanh toán" title="Hoàn tất đơn hàng" description={bookId ? 'Tạo đơn mua ngay từ sách bạn vừa chọn.' : 'Tạo đơn hàng từ giỏ hàng của bạn.'} />
      <div className="grid gap-6 lg:grid-cols-[1fr_380px]">
        <Panel className="p-5">
          {error ? <div className="mb-4 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          {message ? <div className={`mb-4 rounded-lg border px-4 py-3 text-sm font-semibold ${isPaid ? 'border-emerald-300 bg-emerald-100 text-emerald-800' : 'border-emerald-200 bg-emerald-50 text-emerald-700'}`}>{message}</div> : null}
          <form onSubmit={submit} className="grid gap-5">
            <div className="grid gap-4 md:grid-cols-2">
              <Field label="Tên người nhận" value={form.receiverName} onChange={(event) => update('receiverName', event.target.value)} placeholder="Nguyễn Văn A" />
              <Field label="Số điện thoại" value={form.receiverPhone} onChange={(event) => update('receiverPhone', event.target.value)} placeholder="0901234567" />
            </div>
            <Field label="Địa chỉ giao hàng" value={form.shippingAddress} onChange={(event) => update('shippingAddress', event.target.value)} placeholder="Số nhà, đường, phường/xã..." textarea />
            <div className="grid gap-4 md:grid-cols-2">
              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-on-surface">Vận chuyển</span>
                <select value={form.shippingMethod} onChange={(event) => update('shippingMethod', event.target.value)} className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm">
                  <option value="STANDARD">Tiêu chuẩn</option>
                  <option value="EXPRESS">Nhanh</option>
                </select>
              </label>
              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-on-surface">Thanh toán</span>
                <select value={form.paymentMethod} onChange={(event) => update('paymentMethod', event.target.value)} className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm">
                  <option value="COD">COD</option>
                  <option value="SEPAY">SePay</option>
                </select>
              </label>
            </div>
            <div className="rounded-lg border border-outline-variant bg-surface-container-low p-4">
              <label className="flex items-center gap-3 text-sm font-semibold text-on-surface">
                <input
                  type="checkbox"
                  checked={useVoucher}
                  disabled={vouchersLoading || !!vouchersError}
                  onChange={(event) => {
                    const checked = event.target.checked;
                    setUseVoucher(checked);
                    update('voucherCode', checked ? myVouchers[0]?.code || '' : '');
                  }}
                  className="rounded border-outline-variant text-primary focus:ring-primary"
                />
                Sử dụng voucher của tôi
              </label>

              {vouchersLoading ? (
                <p className="mt-3 text-sm text-on-surface-variant">Đang tải voucher...</p>
              ) : vouchersError ? (
                <p className="mt-3 text-sm text-error">{vouchersError}</p>
              ) : useVoucher && myVouchers.length === 0 ? (
                <p className="mt-3 text-sm text-on-surface-variant">Không có voucher khả dụng trong kho. Hãy vào trang Khuyến mãi để lấy voucher trước.</p>
              ) : useVoucher ? (
                <label className="mt-3 block">
                  <span className="mb-2 block text-sm font-semibold text-on-surface">Chọn voucher trong kho của bạn</span>
                  <select
                    value={form.voucherCode}
                    onChange={(event) => update('voucherCode', event.target.value)}
                    className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm"
                  >
                    {myVouchers.map((voucher) => (
                      <option key={voucher.id || voucher.code} value={voucher.code}>
                        {voucher.code} — giảm {voucher.discountPercent}% tối đa {Number(voucher.maxDiscount).toLocaleString('vi-VN')}đ
                      </option>
                    ))}
                  </select>
                </label>
              ) : null}
            </div>
            <div className="rounded-lg border border-outline-variant bg-surface p-4">
              <h2 className="text-base font-bold text-primary">Thông tin đơn hàng</h2>
              {itemsLoading ? (
                <p className="mt-3 text-sm text-on-surface-variant">Đang tải thông tin sách...</p>
              ) : itemsError ? (
                <p className="mt-3 text-sm text-error">{itemsError}</p>
              ) : checkoutItems.length === 0 ? (
                <p className="mt-3 text-sm text-on-surface-variant">Chưa có sách trong đơn hàng.</p>
              ) : (
                <div className="mt-3 divide-y divide-outline-variant">
                  {checkoutItems.map((item) => (
                    <div key={item.bookId} className="flex gap-3 py-3 first:pt-0">
                      {item.cover ? (
                        <img src={item.cover} alt={item.title} className="h-20 w-14 shrink-0 rounded-md object-cover" />
                      ) : (
                        <div className="flex h-20 w-14 shrink-0 items-center justify-center rounded-md bg-surface-container-low text-primary"><Icon name="book" /></div>
                      )}
                      <div className="min-w-0 flex-1">
                        <p className="font-semibold text-on-surface">{item.title}</p>
                        <p className="mt-1 text-sm text-on-surface-variant">{formatVnd(item.unitPrice)} × {item.quantity}</p>
                      </div>
                      <span className="shrink-0 text-sm font-bold text-primary">{formatVnd(item.subtotal)}</span>
                    </div>
                  ))}
                </div>
              )}

              <div className="mt-3 space-y-2 border-t border-outline-variant pt-3 text-sm">
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Tạm tính ({checkoutItems.reduce((total, item) => total + item.quantity, 0)} sản phẩm)</span><span className="font-semibold">{formatVnd(itemsSubtotal)}</span></div>
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Phí vận chuyển</span><span className="font-semibold">{formatVnd(Number(form.shippingFee || 0))}</span></div>
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Giá gốc đơn hàng</span><span className="font-semibold">{formatVnd(originalTotal)}</span></div>
                {(displayedVoucherDiscount > 0 || confirmedTotals?.voucherCode) ? (
                  <div className="flex justify-between gap-4 text-emerald-700"><span>Voucher {confirmedTotals?.voucherCode || selectedVoucher?.code}</span><span className="font-semibold">−{formatVnd(displayedVoucherDiscount)}</span></div>
                ) : null}
                <div className="flex justify-between gap-4 border-t border-outline-variant pt-3 text-base"><span className="font-bold text-primary">Tổng thanh toán</span><span className="font-bold text-secondary">{formatVnd(displayedTotal)}</span></div>
              </div>
            </div>
            <AccentButton type="submit" disabled={loading || itemsLoading || checkoutItems.length === 0 || !!paymentInfo} className="mt-2">{loading ? 'Đang xử lý...' : paymentInfo ? 'Đơn hàng đã được tạo' : 'Xác nhận đặt hàng'}</AccentButton>
          </form>
        </Panel>

        <Panel className="h-fit p-5">
          {paymentInfo?.paymentUrl ? (
            <>
              <div className={`flex h-12 w-12 items-center justify-center rounded-lg ${isPaid ? 'bg-emerald-100 text-emerald-700' : 'bg-primary/5 text-primary'}`}><Icon name={isPaid ? 'chart' : 'ticket'} /></div>
              <h2 className="mt-4 text-lg font-bold text-primary">{isPaid ? 'Thanh toán thành công' : 'Quét mã thanh toán'}</h2>
              <p className="mt-2 text-sm leading-6 text-on-surface-variant">{isPaid ? 'Hệ thống đã ghi nhận giao dịch. Bạn sẽ được chuyển sang chi tiết đơn hàng.' : 'Mở ứng dụng ngân hàng, quét mã QR bên dưới và giữ nguyên nội dung chuyển khoản để hệ thống đối soát đơn hàng.'}</p>
              {!isPaid ? (
                <div className="mt-5 rounded-lg border border-outline-variant bg-white p-4">
                  <img src={paymentInfo.paymentUrl} alt={`Mã QR thanh toán đơn hàng #${paymentInfo.orderId}`} className="mx-auto aspect-square w-full max-w-[300px] object-contain" />
                </div>
              ) : null}
              <div className="mt-5 space-y-3 rounded-lg bg-surface-container-low p-4 text-sm">
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Mã đơn hàng</span><span className="font-bold text-primary">#{paymentInfo.orderId}</span></div>
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Số tiền</span><span className="font-bold text-primary">{Number(paymentInfo.amount || 0).toLocaleString('vi-VN')}đ</span></div>
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Trạng thái</span><span className={isPaid ? 'font-bold text-emerald-700' : 'font-bold text-secondary'}>{statusLabel(paymentInfo.status)}</span></div>
              </div>
              <Link to={`/orders/${paymentInfo.orderId}`} className="mt-5 block">
                <AccentButton className="w-full">Xem chi tiết đơn hàng</AccentButton>
              </Link>
            </>
          ) : (
            <>
              <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/5 text-primary"><Icon name="order" /></div>
              <h2 className="mt-4 text-lg font-bold text-primary">Luồng thanh toán</h2>
              <p className="mt-2 text-sm leading-6 text-on-surface-variant">Hệ thống sẽ tạo đơn hàng từ giỏ hàng hoặc mua ngay. Nếu chọn SePay, mã QR sẽ hiển thị trực tiếp trong giao diện này sau khi tạo đơn.</p>
              <div className="mt-5 space-y-3 text-sm">
                <div className="flex items-center gap-2"><Icon name="cart" className="h-4 w-4 text-secondary" /> {bookId ? `Mua ngay sách #${bookId}, số lượng ${quantity}` : 'Kiểm tra giỏ hàng'}</div>
                <div className="flex items-center gap-2"><Icon name="order" className="h-4 w-4 text-secondary" /> Tạo đơn hàng</div>
                <div className="flex items-center gap-2"><Icon name="ticket" className="h-4 w-4 text-secondary" /> Thanh toán và đối soát</div>
              </div>
            </>
          )}
        </Panel>
      </div>
    </Container>
  );
};

export default CheckoutPage;
