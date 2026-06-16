import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { Container, EmptyState, formatVnd, Panel, PrimaryButton, SecondaryButton, SectionHeading, StatusBadge } from '@/components/ui/staticUi';
import orderService from '@/features/orders/services/orderService';

const OrdersPage = () => {
  const [orders, setOrders] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    setLoading(true);
    setError('');
    orderService.getOrders(page, 10)
      .then((response) => {
        setOrders(response?.data?.content || []);
        setTotalPages(response?.data?.totalPages || 0);
      })
      .catch(() => setError('Không thể tải danh sách đơn hàng.'))
      .finally(() => setLoading(false));
  }, [page]);

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Tài khoản" title="Đơn hàng của tôi" description="Theo dõi trạng thái đơn hàng, thanh toán và thông tin giao nhận." />
      {error ? <div className="mb-5 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}

      {loading ? (
        <div className="h-64 animate-pulse rounded-xl bg-surface-container" />
      ) : orders.length === 0 ? (
        <EmptyState title="Bạn chưa có đơn hàng" description="Các đơn đã đặt sẽ xuất hiện ở đây." action={<Link to="/catalog"><PrimaryButton>Bắt đầu mua sắm</PrimaryButton></Link>} />
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <Panel key={order.id} className="p-5">
              <div className="grid gap-4 md:grid-cols-5 md:items-center">
                <div>
                  <p className="text-xs font-bold uppercase text-on-surface-variant">Mã đơn</p>
                  <p className="mt-1 text-lg font-bold text-primary">#{order.id}</p>
                </div>
                <div>
                  <p className="text-xs font-bold uppercase text-on-surface-variant">Ngày đặt</p>
                  <p className="mt-1 text-sm font-semibold text-on-surface">{order.createdAt ? new Date(order.createdAt).toLocaleDateString('vi-VN') : '-'}</p>
                </div>
                <div>
                  <p className="text-xs font-bold uppercase text-on-surface-variant">Trạng thái</p>
                  <div className="mt-1"><StatusBadge status={order.status} /></div>
                </div>
                <div>
                  <p className="text-xs font-bold uppercase text-on-surface-variant">Tổng tiền</p>
                  <p className="mt-1 text-lg font-bold text-primary">{formatVnd(order.totalPrice)}</p>
                </div>
                <div className="md:text-right">
                  <Link to={`/orders/${order.id}`} className="text-sm font-bold text-secondary hover:underline">Xem chi tiết</Link>
                </div>
              </div>
            </Panel>
          ))}
          {totalPages > 1 ? (
            <div className="flex justify-center gap-3 pt-4">
              <SecondaryButton disabled={page === 0} onClick={() => setPage((value) => Math.max(0, value - 1))}>Trước</SecondaryButton>
              <SecondaryButton disabled={page >= totalPages - 1} onClick={() => setPage((value) => value + 1)}>Tiếp</SecondaryButton>
            </div>
          ) : null}
        </div>
      )}
    </Container>
  );
};

export default OrdersPage;
