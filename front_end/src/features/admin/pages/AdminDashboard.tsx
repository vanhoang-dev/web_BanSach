import { useEffect, useState } from 'react';

import { AdminTable, formatVnd, Icon, Panel, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import adminService from '@/features/admin/services';

type RecentOrder = {
  id: number;
  receiverName?: string;
  totalAmount?: number;
  orderDate?: string;
  status?: string;
};

type DashboardStats = {
  totalOrders: number;
  totalPaidPayments: number;
  totalRevenue: number;
  totalBooks: number;
  totalUsers: number;
  totalBooksSold: number;
  recentOrders: RecentOrder[];
};

const emptyStats: DashboardStats = {
  totalOrders: 0,
  totalPaidPayments: 0,
  totalRevenue: 0,
  totalBooks: 0,
  totalUsers: 0,
  totalBooksSold: 0,
  recentOrders: [],
};

const AdminDashboard = () => {
  const [dashboard, setDashboard] = useState<DashboardStats>(emptyStats);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    adminService.getDashboardStats()
      .then((response) => {
        if (active) setDashboard({ ...emptyStats, ...response });
      })
      .catch(() => setDashboard(emptyStats))
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading
        eyebrow="Quản trị"
        title="Tổng quan vận hành"
        description="Theo dõi doanh thu, đơn hàng, thanh toán, người dùng và tồn kho từ các phân hệ hệ thống."
      />

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        <StatCard label="Tổng đơn hàng" value={loading ? '...' : dashboard.totalOrders.toLocaleString('vi-VN')} icon="order" />
        <StatCard label="Thanh toán thành công" value={loading ? '...' : dashboard.totalPaidPayments.toLocaleString('vi-VN')} icon="ticket" tone="success" />
        <StatCard label="Doanh thu" value={loading ? '...' : formatVnd(dashboard.totalRevenue)} icon="chart" tone="success" />
        <StatCard label="Sách bán ra" value={loading ? '...' : dashboard.totalBooksSold.toLocaleString('vi-VN')} icon="cart" />
        <StatCard label="Đầu sách" value={loading ? '...' : dashboard.totalBooks.toLocaleString('vi-VN')} icon="book" tone="warning" />
        <StatCard label="Người dùng" value={loading ? '...' : dashboard.totalUsers.toLocaleString('vi-VN')} icon="users" />
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_360px]">
        <AdminTable minWidth="720px">
          <thead className="border-b border-outline-variant bg-surface-container-high">
            <tr>
              {['Mã đơn', 'Người nhận', 'Số tiền', 'Ngày đặt', 'Trạng thái'].map((head) => (
                <th key={head} className="px-5 py-4 text-xs font-bold uppercase text-on-surface-variant">{head}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-outline-variant">
            {dashboard.recentOrders.map((order) => (
              <tr key={order.id} className="hover:bg-surface-container-low">
                <td className="px-5 py-4 text-sm font-bold text-primary">#{order.id}</td>
                <td className="px-5 py-4 text-sm text-on-surface">{order.receiverName || 'Chưa cập nhật'}</td>
                <td className="px-5 py-4 text-sm font-bold text-primary">{formatVnd(order.totalAmount)}</td>
                <td className="px-5 py-4 text-sm text-on-surface-variant">{order.orderDate ? new Date(order.orderDate).toLocaleString('vi-VN') : '-'}</td>
                <td className="px-5 py-4"><StatusBadge status={order.status} /></td>
              </tr>
            ))}
            {!loading && dashboard.recentOrders.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-5 py-12 text-center text-sm text-on-surface-variant">Chưa có đơn hàng.</td>
              </tr>
            ) : null}
          </tbody>
        </AdminTable>

        <Panel className="p-5">
          <h2 className="text-lg font-bold text-primary">Luồng nghiệp vụ</h2>
          <div className="mt-5 space-y-4">
            {[
              ['Sách', 'Thêm, sửa, xóa sách, ảnh bìa, tác giả, danh mục', 'book'],
              ['Bán hàng', 'Giỏ hàng, voucher, tạo đơn, thanh toán', 'cart'],
              ['Vận hành', 'Duyệt đơn, cập nhật kho, quản lý người dùng', 'inventory'],
            ].map(([title, desc, icon]) => (
              <div key={title} className="flex gap-3 rounded-lg border border-outline-variant p-4">
                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/5 text-primary">
                  <Icon name={icon} />
                </div>
                <div>
                  <p className="font-bold text-primary">{title}</p>
                  <p className="mt-1 text-sm leading-6 text-on-surface-variant">{desc}</p>
                </div>
              </div>
            ))}
          </div>
        </Panel>
      </div>
    </div>
  );
};

export default AdminDashboard;

