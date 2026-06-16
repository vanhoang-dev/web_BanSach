import { FormEvent, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { AccentButton, Container, Field, Icon, Panel, SectionHeading } from '@/components/ui/staticUi';
import orderService from '@/features/orders/services/orderService';
import paymentService from '@/features/payment/services/paymentService';

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

  const update = (name: string, value: string) => setForm((current) => ({ ...current, [name]: value }));

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');
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
        voucherCode: form.voucherCode || undefined,
      };
      const order = bookId
        ? await orderService.buyNow({ ...payload, bookId, quantity })
        : await orderService.createOrder(payload);

      if (form.paymentMethod === 'SEPAY' && order.id) {
        const payment = await paymentService.initiatePayment({
          orderId: order.id,
          amount: Number(order.totalAmount || order.totalPrice || 0),
          returnUrl: `${window.location.origin}/orders/${order.id}`,
          description: `Thanh toán đơn hàng #${order.id}`,
        });
        if (payment.paymentUrl) {
          window.location.href = payment.paymentUrl;
          return;
        }
        setMessage(payment.message || 'Đã khởi tạo thanh toán. Kiểm tra trạng thái trong chi tiết đơn hàng.');
        window.setTimeout(() => navigate(`/orders/${order.id}`), 1200);
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

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Thanh toán" title="Hoàn tất đơn hàng" description={bookId ? 'Tạo đơn mua ngay từ sách bạn vừa chọn.' : 'Tạo đơn hàng từ giỏ hàng của bạn.'} />
      <div className="grid gap-6 lg:grid-cols-[1fr_360px]">
        <Panel className="p-5">
          {error ? <div className="mb-4 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          {message ? <div className="mb-4 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}
          <form onSubmit={submit} className="grid gap-5">
            <div className="grid gap-4 md:grid-cols-2">
              <Field label="Tên người nhận" value={form.receiverName} onChange={(event) => update('receiverName', event.target.value)} placeholder="Nguyễn Văn A" />
              <Field label="Số điện thoại" value={form.receiverPhone} onChange={(event) => update('receiverPhone', event.target.value)} placeholder="0901234567" />
            </div>
            <Field label="Địa chỉ giao hàng" value={form.shippingAddress} onChange={(event) => update('shippingAddress', event.target.value)} placeholder="Số nhà, đường, phường/xã..." textarea />
            <div className="grid gap-4 md:grid-cols-3">
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
              <Field label="Mã voucher" value={form.voucherCode} onChange={(event) => update('voucherCode', event.target.value)} placeholder="SALE20" />
            </div>
            <AccentButton type="submit" disabled={loading} className="mt-2">{loading ? 'Đang xử lý...' : 'Xác nhận đặt hàng'}</AccentButton>
          </form>
        </Panel>

        <Panel className="h-fit p-5">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/5 text-primary"><Icon name="order" /></div>
          <h2 className="mt-4 text-lg font-bold text-primary">Luồng thanh toán</h2>
          <p className="mt-2 text-sm leading-6 text-on-surface-variant">Hệ thống sẽ tạo đơn hàng từ giỏ hàng hoặc mua ngay. Nếu chọn SePay, giao diện sẽ gọi `/api/payment/initiate` sau khi tạo đơn.</p>
          <div className="mt-5 space-y-3 text-sm">
            <div className="flex items-center gap-2"><Icon name="cart" className="h-4 w-4 text-secondary" /> {bookId ? `Mua ngay sách #${bookId}, số lượng ${quantity}` : 'Kiểm tra giỏ hàng'}</div>
            <div className="flex items-center gap-2"><Icon name="order" className="h-4 w-4 text-secondary" /> Tạo đơn hàng</div>
            <div className="flex items-center gap-2"><Icon name="ticket" className="h-4 w-4 text-secondary" /> Thanh toán và đối soát</div>
          </div>
        </Panel>
      </div>
    </Container>
  );
};

export default CheckoutPage;
