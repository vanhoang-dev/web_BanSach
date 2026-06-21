import { useEffect, useState } from 'react';

import { AdminTable, AdminToolbar, formatVnd, Icon, PrimaryButton, SecondaryButton, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import AdminPagination from '@/features/admin/components/AdminPagination';
import orderAdminService from '@/features/admin/services/orderAdminService';

const statuses = ['PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'COMPLETED', 'CANCELLED'];
const pageSize = 10;

const AdminOrderManagementPage = () => {
  const [orders, setOrders] = useState<any[]>([]);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const response = await orderAdminService.getAll(page, pageSize);
      setOrders(response.data.content || []);
      setPageInfo({ totalElements: response.data.totalElements, totalPages: response.data.totalPages });
    } catch {
      setError('Không thể tải đơn hàng.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [page]);

  const updateStatus = async (id: number, status: string) => {
    await orderAdminService.updateStatus(id, status);
    fetchOrders();
  };

  const cancel = async (id: number) => {
    if (!window.confirm('Hủy đơn hàng nay?')) return;
    await orderAdminService.cancel(id);
    fetchOrders();
  };

  const visibleOrders = filter ? orders.filter((order) => String(order.id).includes(filter) || String(order.receiverName || order.fullName || '').toLowerCase().includes(filter.toLowerCase())) : orders;

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading eyebrow="Quản trị" title="Quản lý đơn hàng" description="Theo dõi đơn, cập nhật trạng thái, hủy đơn và đối chiếu thanh toán qua /admin/orders." action={<SecondaryButton><Icon name="file" /> Xuất báo cáo</SecondaryButton>} />
      <div className="grid gap-4 md:grid-cols-4">
        <StatCard label="Chờ xử lý" value={orders.filter((item) => item.status === 'PENDING').length} icon="order" tone="warning" />
        <StatCard label="Đang giao" value={orders.filter((item) => ['SHIPPING', 'SHIPPED'].includes(item.status)).length} icon="inventory" />
        <StatCard label="Hoàn tất" value={orders.filter((item) => ['DELIVERED', 'COMPLETED'].includes(item.status)).length} icon="chart" tone="success" />
        <StatCard label="Đã hủy" value={orders.filter((item) => item.status === 'CANCELLED').length} icon="trash" tone="danger" />
      </div>
      {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      <div className="mt-6">
        <AdminToolbar>
          <input value={filter} onChange={(e) => { setFilter(e.target.value); setPage(0); }} className="h-11 w-full rounded-lg border border-outline-variant bg-surface px-4 text-sm outline-none focus:border-primary md:max-w-sm" placeholder="Tìm mã đơn, khách hàng..." />
          <span className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${pageInfo.totalElements} đơn hàng`}</span>
        </AdminToolbar>
        <AdminTable minWidth="880px">
          <thead className="border-b border-outline-variant bg-surface-container-high text-xs uppercase text-on-surface-variant"><tr>{['Mã đơn', 'Khách hàng', 'Số tiền', 'Địa chỉ', 'Trạng thái', 'Hành động'].map((head) => <th key={head} className="px-5 py-4">{head}</th>)}</tr></thead>
          <tbody className="divide-y divide-outline-variant">
            {visibleOrders.map((order) => (
              <tr key={order.id} className="hover:bg-surface-container-low">
                <td className="px-5 py-4 font-bold text-primary">#{order.id}</td>
                <td className="px-5 py-4 text-sm text-on-surface">{order.receiverName || order.fullName || order.userName || '-'}</td>
                <td className="px-5 py-4 text-sm font-bold text-primary">{formatVnd(Number(order.totalAmount || order.totalPrice || 0))}</td>
                <td className="max-w-xs truncate px-5 py-4 text-sm text-on-surface-variant">{order.shippingAddress || '-'}</td>
                <td className="px-5 py-4"><StatusBadge status={order.status} /></td>
                <td className="px-5 py-4">
                  <div className="flex flex-wrap gap-2">
                    <select value={order.status || 'PENDING'} onChange={(e) => updateStatus(order.id, e.target.value)} className="h-10 rounded-lg border-outline-variant bg-surface text-sm">
                      {statuses.map((status) => <option key={status} value={status}>{status}</option>)}
                    </select>
                    <PrimaryButton onClick={() => cancel(order.id)} disabled={order.status === 'CANCELLED'}>Hủy</PrimaryButton>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </AdminTable>
        <AdminPagination page={page} pageSize={pageSize} totalElements={pageInfo.totalElements} totalPages={pageInfo.totalPages} onPageChange={setPage} />
      </div>
    </div>
  );
};

export default AdminOrderManagementPage;
