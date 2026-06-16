import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { Container, formatVnd, Icon, Panel, SecondaryButton, SectionHeading, StatusBadge } from '@/components/ui/staticUi';
import orderService from '@/features/orders/services/orderService';

const steps = ['PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED'];

const OrderDetailPage = () => {
  const { id } = useParams();
  const [order, setOrder] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    orderService.getOrderById(Number(id)).then(setOrder).finally(() => setLoading(false));
  }, [id]);

  const activeIndex = Math.max(0, steps.indexOf(order?.status));

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Đơn hàng" title={`Chi tiết đơn hàng #${id || ''}`} description="Thông tin giao nhận, thanh toán và tiến trình xử lý đơn hàng." />
      {loading ? <div className="h-64 animate-pulse rounded-xl bg-surface-container" /> : (
        <div className="grid gap-6 lg:grid-cols-[1fr_360px]">
          <Panel className="p-5">
            <div className="mb-6 flex items-center justify-between gap-4">
              <h2 className="text-lg font-bold text-primary">Tiến trình</h2>
              <StatusBadge status={order?.status} />
            </div>
            <div className="space-y-5">
              {steps.map((step, index) => (
                <div key={step} className="flex gap-4">
                  <div className={`mt-1 h-3 w-3 rounded-full ${index <= activeIndex ? 'bg-primary' : 'bg-outline-variant'}`} />
                  <div>
                    <StatusBadge status={step} />
                    <p className="mt-1 text-sm text-on-surface-variant">{index <= activeIndex ? 'Đã cập nhật' : 'Đang chờ'}</p>
                  </div>
                </div>
              ))}
            </div>
          </Panel>
          <Panel className="p-5">
            <h2 className="text-lg font-bold text-primary">Tóm tắt</h2>
            <div className="mt-5 space-y-3 text-sm">
              <div className="flex justify-between"><span className="text-on-surface-variant">Người nhận</span><span className="font-bold">{order?.fullName || '-'}</span></div>
              <div className="flex justify-between"><span className="text-on-surface-variant">Số điện thoại</span><span className="font-bold">{order?.phoneNumber || '-'}</span></div>
              <div className="flex justify-between"><span className="text-on-surface-variant">Tổng tiền</span><span className="font-bold text-primary">{formatVnd(order?.totalPrice)}</span></div>
            </div>
            <p className="mt-5 rounded-lg bg-surface-container-low p-3 text-sm leading-6 text-on-surface-variant">{order?.shippingAddress || 'Chưa cập nhật địa chỉ giao hàng'}</p>
            <SecondaryButton className="mt-5 w-full"><Icon name="file" /> Xuất hóa đơn</SecondaryButton>
          </Panel>
        </div>
      )}
    </Container>
  );
};

export default OrderDetailPage;
