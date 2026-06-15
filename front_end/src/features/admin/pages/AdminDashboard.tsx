import { useEffect, useState } from 'react';

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

const currency = new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
});

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
                if (active) {
                    setDashboard(response.data ?? emptyStats);
                }
            })
            .finally(() => {
                if (active) {
                    setLoading(false);
                }
            });

        return () => {
            active = false;
        };
    }, []);

    const stats = [
        { title: 'Tổng đơn hàng', value: dashboard.totalOrders.toLocaleString('vi-VN'), icon: 'shopping_bag' },
        { title: 'Thanh toán thành công', value: dashboard.totalPaidPayments.toLocaleString('vi-VN'), icon: 'paid' },
        { title: 'Doanh thu', value: currency.format(dashboard.totalRevenue), icon: 'trending_up' },
        { title: 'Sách bán ra', value: dashboard.totalBooksSold.toLocaleString('vi-VN'), icon: 'book' },
        { title: 'Đầu sách', value: dashboard.totalBooks.toLocaleString('vi-VN'), icon: 'library' },
        { title: 'Người dùng', value: dashboard.totalUsers.toLocaleString('vi-VN'), icon: 'group' },
    ];

    const getStatusColor = (status?: string) => {
        switch (status) {
            case 'COMPLETED':
                return 'bg-green-100 text-green-800';
            case 'CONFIRMED':
            case 'SHIPPING':
                return 'bg-blue-100 text-blue-800';
            case 'PENDING':
                return 'bg-yellow-100 text-yellow-800';
            case 'CANCELLED':
                return 'bg-red-100 text-red-800';
            default:
                return 'bg-gray-100 text-gray-800';
        }
    };

    return (
        <div className="w-full">
            <div className="mb-section-gap">
                <h1 className="font-h1 text-h1 text-primary mb-unit">Tổng quan</h1>
                <p className="font-body-md text-body-md text-on-surface-variant">
                    Theo dõi đơn hàng, thanh toán thành công và doanh thu thực tế từ SePay.
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-gutter mb-section-gap">
                {stats.map((stat) => (
                    <div
                        key={stat.title}
                        className="bg-surface-container-lowest rounded-xl p-stack-lg border border-surface-variant"
                    >
                        <div className="flex items-center justify-between mb-stack-md">
                            <h3 className="font-label-md text-label-md text-on-surface-variant">{stat.title}</h3>
                            <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
                                <span className="text-primary font-bold">{stat.icon === 'trending_up' ? '₫' : stat.value.slice(0, 1)}</span>
                            </div>
                        </div>
                        <h2 className="font-h2 text-h2 text-primary">{loading ? '...' : stat.value}</h2>
                    </div>
                ))}
            </div>

            <div className="bg-surface-container-lowest rounded-xl border border-surface-variant overflow-hidden">
                <div className="p-stack-lg border-b border-surface-variant">
                    <h2 className="font-h2 text-h2 text-primary">Đơn hàng gần đây</h2>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-surface-container border-b border-surface-variant">
                            <tr>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Mã đơn</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Người nhận</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Số tiền</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Ngày đặt</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            {dashboard.recentOrders.map((order) => (
                                <tr key={order.id} className="border-b border-surface-variant hover:bg-surface-container-low transition-colors">
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-primary font-bold">#{order.id}</td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface">{order.receiverName}</td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface font-bold">
                                        {currency.format(order.totalAmount ?? 0)}
                                    </td>
                                    <td className="px-stack-lg py-stack-md font-caption text-caption text-on-surface-variant">
                                        {order.orderDate ? new Date(order.orderDate).toLocaleString('vi-VN') : ''}
                                    </td>
                                    <td className="px-stack-lg py-stack-md">
                                        <span className={`inline-block px-3 py-1 rounded-full font-caption text-caption font-bold ${getStatusColor(order.status)}`}>
                                            {order.status}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                            {!loading && dashboard.recentOrders.length === 0 && (
                                <tr>
                                    <td colSpan={5} className="px-stack-lg py-stack-lg text-center text-on-surface-variant">
                                        Chưa có đơn hàng.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
