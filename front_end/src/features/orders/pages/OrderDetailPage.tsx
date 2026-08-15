import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';

import { Container, formatVnd, Icon, Panel, SecondaryButton, SectionHeading, StatusBadge } from '@/components/ui/staticUi';
import bookService from '@/features/books/services/bookService';
import orderService, { type Order } from '@/features/orders/services/orderService';
import paymentService, { type PaymentResponse } from '@/features/payment/services/paymentService';

const steps = ['PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED'];
const fallbackBookCover = 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=360&q=85';
const stepDetails: Record<string, { title: string; description: string }> = {
  PENDING: { title: 'Chờ xác nhận', description: 'Đơn hàng đã được tạo' },
  CONFIRMED: { title: 'Đã xác nhận', description: 'Đã duyệt và chuẩn bị sách' },
  SHIPPING: { title: 'Đang giao', description: 'Đơn hàng đang trên đường' },
  COMPLETED: { title: 'Hoàn tất', description: 'Đã giao hàng thành công' },
};

const formatDateTime = (value?: string) => value
  ? new Intl.DateTimeFormat('vi-VN', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value))
  : '-';

const shippingMethodLabel = (method?: string) => method === 'EXPRESS' ? 'Giao nhanh' : 'Tiêu chuẩn';

const OrderDetailPage = () => {
  const { id } = useParams();
  const [order, setOrder] = useState<Order | null>(null);
  const [payment, setPayment] = useState<PaymentResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [paymentError, setPaymentError] = useState('');

  useEffect(() => {
    if (!id) return;
    let cancelled = false;

    const loadDetail = async () => {
      setLoading(true);
      setError('');
      try {
        const orderData = await orderService.getOrderById(Number(id));
        if (cancelled) return;
        const enrichedItems = await Promise.all((orderData.items || []).map(async (item) => {
          if (item.book?.cover) return item;
          try {
            const book = await bookService.getBookById(item.bookId);
            return { ...item, book: { ...item.book, id: item.bookId, title: item.book?.title || book.title, cover: book.cover } };
          } catch {
            return item;
          }
        }));
        if (cancelled) return;
        setOrder({ ...orderData, items: enrichedItems });

        try {
          const paymentData = await paymentService.getOrderPaymentStatus(Number(id));
          if (!cancelled) setPayment(paymentData);
        } catch (requestError: any) {
          if (cancelled) return;
          // COD orders do not create a payment record, so 404 means COD rather
          // than an error loading the order.
          if (Number(requestError?.statusCode) === 404) {
            setPayment(null);
          } else {
            setPaymentError('Không thể kiểm tra trạng thái thanh toán lúc này.');
          }
        }
      } catch (requestError: any) {
        if (!cancelled) setError(requestError?.message || 'Không thể tải chi tiết đơn hàng.');
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    loadDetail();
    return () => {
      cancelled = true;
    };
  }, [id]);

  const activeIndex = Math.max(0, steps.indexOf(order?.status || 'PENDING'));
  const productsTotal = useMemo(
    () => (order?.items || []).reduce((total, item) => total + Number(item.price || 0) * Number(item.quantity || 0), 0),
    [order?.items]
  );
  const voucherDiscount = Number(order?.voucherDiscount || 0);
  const totalAmount = Number(order?.totalAmount ?? order?.totalPrice ?? 0);
  const originalTotal = totalAmount + voucherDiscount;
  const paymentStatus = String(payment?.status || '').toUpperCase();
  const isPaid = paymentStatus === 'SUCCESS' || paymentStatus === 'PAID' || (!payment && order?.status === 'COMPLETED');
  const paymentMethod = payment?.paymentMethod || (payment ? 'SEPAY' : 'COD');

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Đơn hàng" title={`Chi tiết đơn hàng #${id || ''}`} description="Thông tin sản phẩm, giao nhận, thanh toán và tiến trình xử lý đơn hàng." />

      {loading ? <div className="h-64 animate-pulse rounded-xl bg-surface-container" /> : error ? (
        <Panel className="p-6 text-sm font-semibold text-error">{error}</Panel>
      ) : order ? (
        <div className="grid gap-6 lg:grid-cols-[1fr_380px]">
          <div className="space-y-6">
            <Panel className="overflow-hidden">
              <div className="flex flex-col gap-3 border-b border-outline-variant bg-surface-container-low px-5 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-7">
                <div>
                  <h2 className="text-lg font-bold text-primary">Tiến trình đơn hàng</h2>
                  <p className="mt-1 text-sm text-on-surface-variant">Theo dõi trạng thái xử lý và vận chuyển đơn hàng của bạn.</p>
                </div>
                <StatusBadge status={order.status} />
              </div>
              {order.status === 'CANCELLED' ? (
                <div className="m-5 flex items-center gap-3 rounded-xl border border-error/20 bg-error-container p-4 text-sm font-semibold text-on-error-container sm:m-7">
                  <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-error text-white">×</span>
                  <div><p>Đơn hàng đã bị hủy</p><p className="mt-1 font-normal opacity-80">Tiến trình giao hàng của đơn này đã kết thúc.</p></div>
                </div>
              ) : (
                <div className="p-5 sm:p-7">
                  <div className="relative grid gap-3 sm:grid-cols-4 sm:gap-0">
                    <div className="absolute left-[12.5%] right-[12.5%] top-6 hidden h-1 overflow-hidden rounded-full bg-surface-container-high sm:block">
                      <div className="h-full rounded-full bg-primary transition-all duration-500" style={{ width: `${(activeIndex / (steps.length - 1)) * 100}%` }} />
                    </div>
                    {steps.map((step, index) => {
                      const completed = index < activeIndex;
                      const current = index === activeIndex;
                      const detail = stepDetails[step];
                      return (
                        <div key={step} className={`relative z-10 flex items-center gap-4 rounded-xl p-3 sm:flex-col sm:gap-3 sm:bg-transparent sm:p-0 sm:text-center ${current ? 'bg-secondary-container/15 ring-1 ring-secondary-container sm:ring-0' : 'bg-surface-container-low sm:bg-transparent'}`}>
                          <div className={`relative flex h-12 w-12 shrink-0 items-center justify-center rounded-full border-4 text-sm font-extrabold shadow-sm transition ${completed ? 'border-primary bg-primary text-on-primary' : current ? 'border-secondary-container bg-secondary text-on-secondary' : 'border-surface-container-high bg-surface text-on-surface-variant'}`}>
                            {completed ? (
                              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="h-5 w-5" aria-hidden="true"><path d="m5 12 4 4L19 6" /></svg>
                            ) : index + 1}
                            {current ? <span className="absolute -right-1 -top-1 h-3 w-3 animate-pulse rounded-full border-2 border-surface bg-secondary" /> : null}
                          </div>
                          <div className="min-w-0 sm:px-2">
                            <p className={`text-sm font-bold ${completed || current ? 'text-primary' : 'text-on-surface-variant'}`}>{detail.title}</p>
                            <p className="mt-1 text-xs leading-5 text-on-surface-variant">{current ? 'Trạng thái hiện tại' : completed ? detail.description : 'Đang chờ xử lý'}</p>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              )}
            </Panel>

            <Panel className="overflow-hidden">
              <div className="border-b border-outline-variant bg-surface-container-low px-5 py-4 sm:px-7">
                <h2 className="text-lg font-bold text-primary">Sản phẩm đã đặt</h2>
                <p className="mt-1 text-sm text-on-surface-variant">{order.items?.reduce((total, item) => total + item.quantity, 0) || 0} sản phẩm trong đơn hàng</p>
              </div>
              <div className="divide-y divide-outline-variant px-5 sm:px-7">
                {(order.items || []).map((item) => (
                  <div key={`${item.bookId}-${item.id || ''}`} className="flex flex-col gap-4 py-5 sm:flex-row sm:items-center">
                    <img
                      src={item.book?.cover || fallbackBookCover}
                      onError={(event) => { event.currentTarget.src = fallbackBookCover; }}
                      alt={`Ảnh bìa ${item.book?.title || `sách #${item.bookId}`}`}
                      className="h-36 w-24 shrink-0 rounded-lg border border-outline-variant bg-white object-cover shadow-sm sm:h-28 sm:w-20"
                    />
                    <div className="min-w-0 flex-1">
                      <p className="text-base font-bold text-primary">{item.book?.title || `Sách #${item.bookId}`}</p>
                      <div className="mt-3 flex flex-wrap gap-2 text-xs font-semibold">
                        <span className="rounded-full bg-surface-container-low px-3 py-1.5 text-on-surface-variant">Đơn giá: {formatVnd(item.price)}</span>
                        <span className="rounded-full bg-primary/5 px-3 py-1.5 text-primary">Số lượng: {item.quantity}</span>
                      </div>
                    </div>
                    <div className="sm:text-right"><p className="text-xs font-semibold uppercase tracking-wide text-on-surface-variant">Thành tiền</p><p className="mt-1 text-lg font-extrabold text-secondary">{formatVnd(Number(item.price) * Number(item.quantity))}</p></div>
                  </div>
                ))}
              </div>
            </Panel>

            <Panel className="overflow-hidden">
              <div className="border-b border-outline-variant bg-surface-container-low px-5 py-4 sm:px-7">
                <h2 className="text-lg font-bold text-primary">Thông tin giao nhận</h2>
                <p className="mt-1 text-sm text-on-surface-variant">Thông tin người nhận và phương thức giao hàng đã chọn.</p>
              </div>
              <div className="grid gap-4 p-5 text-sm sm:grid-cols-2 sm:p-7">
                {[
                  { icon: 'user', label: 'Người nhận', value: order.receiverName || order.fullName || '-' },
                  { icon: 'order', label: 'Số điện thoại', value: order.receiverPhone || order.phoneNumber || '-' },
                  { icon: 'cart', label: 'Phương thức vận chuyển', value: shippingMethodLabel(order.shippingMethod) },
                  { icon: 'ticket', label: 'Ngày đặt hàng', value: formatDateTime(order.orderDate || order.createdAt) },
                ].map((info) => (
                  <div key={info.label} className="flex items-start gap-3 rounded-xl border border-outline-variant bg-surface p-4 shadow-sm">
                    <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/5 text-primary"><Icon name={info.icon} className="h-5 w-5" /></span>
                    <div className="min-w-0"><p className="text-xs font-semibold uppercase tracking-wide text-on-surface-variant">{info.label}</p><p className="mt-1 break-words font-bold text-on-surface">{info.value}</p></div>
                  </div>
                ))}
                <div className="rounded-xl border border-outline-variant bg-primary/[0.03] p-4 sm:col-span-2">
                  <div className="flex items-start gap-3">
                    <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-secondary-container text-on-secondary-container"><Icon name="inventory" className="h-5 w-5" /></span>
                    <div><p className="text-xs font-semibold uppercase tracking-wide text-on-surface-variant">Địa chỉ giao hàng</p><p className="mt-1 font-bold leading-6 text-primary">{order.shippingAddress || '-'}</p></div>
                  </div>
                </div>
              </div>
            </Panel>
          </div>

          <div className="space-y-6">
            <Panel className="p-5">
              <div className="flex items-center justify-between gap-3">
                <h2 className="text-lg font-bold text-primary">Thanh toán</h2>
                <StatusBadge status={isPaid ? 'PAID' : 'UNPAID'} />
              </div>
              {paymentError ? <p className="mt-4 rounded-lg bg-error-container p-3 text-sm text-on-error-container">{paymentError}</p> : null}
              <div className="mt-5 space-y-3 text-sm">
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Trạng thái</span><span className={`font-bold ${isPaid ? 'text-emerald-700' : 'text-secondary'}`}>{isPaid ? 'Đã thanh toán' : 'Chưa thanh toán'}</span></div>
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Phương thức</span><span className="font-bold">{paymentMethod}</span></div>
                {payment?.transactionId ? <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Mã giao dịch</span><span className="break-all text-right font-bold">{payment.transactionId}</span></div> : null}
                {payment?.paidAt ? <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Thanh toán lúc</span><span className="text-right font-bold">{formatDateTime(payment.paidAt)}</span></div> : null}
                {!isPaid && paymentStatus ? <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Trạng thái cổng</span><span className="font-bold">{paymentStatus}</span></div> : null}
              </div>
            </Panel>

            <Panel className="p-5">
              <h2 className="text-lg font-bold text-primary">Tóm tắt đơn hàng</h2>
              <div className="mt-5 space-y-3 text-sm">
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Tiền sản phẩm</span><span className="font-semibold">{formatVnd(productsTotal)}</span></div>
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Phí vận chuyển</span><span className="font-semibold">{formatVnd(order.shippingFee)}</span></div>
                <div className="flex justify-between gap-4"><span className="text-on-surface-variant">Giá gốc đơn hàng</span><span className="font-semibold">{formatVnd(originalTotal)}</span></div>
                {order.voucherCode ? <div className="flex justify-between gap-4 text-emerald-700"><span>Voucher {order.voucherCode}</span><span className="font-semibold">−{formatVnd(voucherDiscount)}</span></div> : null}
                <div className="flex justify-between gap-4 border-t border-outline-variant pt-3 text-base"><span className="font-bold text-primary">Tổng thanh toán</span><span className="font-bold text-secondary">{formatVnd(totalAmount)}</span></div>
              </div>
              <SecondaryButton onClick={() => window.print()} className="mt-5 w-full"><Icon name="file" /> Xuất hóa đơn</SecondaryButton>
            </Panel>
          </div>
        </div>
      ) : null}
    </Container>
  );
};

export default OrderDetailPage;
