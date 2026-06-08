
const AdminDashboard = () => {
    const stats = [
        {
            title: 'Tổng đơn hàng',
            value: '1,234',
            change: '+12% từ tuần trước',
            icon: 'shopping_bag',
        },
        {
            title: 'Doanh thu',
            value: '₫45.2M',
            change: '+8% từ tuần trước',
            icon: 'trending_up',
        },
        {
            title: 'Sách bán ra',
            value: '5,678',
            change: '+15% từ tuần trước',
            icon: 'book',
        },
        {
            title: 'Người dùng mới',
            value: '234',
            change: '+5% từ tuần trước',
            icon: 'group',
        },
    ];

    const recentOrders = [
        {
            id: '#ORD-001',
            customer: 'Nguyễn Văn A',
            amount: '₫250.000',
            date: '2024-05-09',
            status: 'Completed',
        },
        {
            id: '#ORD-002',
            customer: 'Trần Thị B',
            amount: '₫180.000',
            date: '2024-05-08',
            status: 'Processing',
        },
        {
            id: '#ORD-003',
            customer: 'Lê Văn C',
            amount: '₫420.000',
            date: '2024-05-08',
            status: 'Pending',
        },
        {
            id: '#ORD-004',
            customer: 'Phạm Thị D',
            amount: '₫95.000',
            date: '2024-05-07',
            status: 'Completed',
        },
    ];

    const getStatusColor = (status) => {
        switch (status) {
            case 'Completed':
                return 'bg-green-100 text-green-800';
            case 'Processing':
                return 'bg-blue-100 text-blue-800';
            case 'Pending':
                return 'bg-yellow-100 text-yellow-800';
            default:
                return 'bg-gray-100 text-gray-800';
        }
    };

    return (
        <div className="w-full">
            {/* Header */}
            <div className="mb-section-gap">
                <h1 className="font-h1 text-h1 text-primary mb-unit">Tổng quan</h1>
                <p className="font-body-md text-body-md text-on-surface-variant">
                    Chào mừng trở lại! Đây là bảng điều khiển quản trị cửa hàng của bạn.
                </p>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-gutter mb-section-gap">
                {stats.map((stat, idx) => (
                    <div
                        key={idx}
                        className="bg-surface-container-lowest rounded-xl p-stack-lg border border-surface-variant hover:shadow-md transition-all"
                    >
                        <div className="flex items-center justify-between mb-stack-md">
                            <h3 className="font-label-md text-label-md text-on-surface-variant">
                                {stat.title}
                            </h3>
                            <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
                                <svg
                                    className="w-6 h-6 text-primary"
                                    fill="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    {stat.icon === 'shopping_bag' && (
                                        <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z" />
                                    )}
                                    {stat.icon === 'trending_up' && (
                                        <path d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12v-6z" />
                                    )}
                                    {stat.icon === 'book' && (
                                        <path d="M4 6h16v2H4V6zm0 5h16v2H4v-2zm0 5h16v2H4v-2z" />
                                    )}
                                    {stat.icon === 'group' && (
                                        <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.64 2.2 1.56 2.97 2.54.6.81 1.23 1.6 2 2.1h6v-2.5c0-2.33-4.67-3.5-7-3.5z" />
                                    )}
                                </svg>
                            </div>
                        </div>
                        <div className="flex flex-col gap-unit">
                            <h2 className="font-h2 text-h2 text-primary">{stat.value}</h2>
                            <p className="font-caption text-caption text-on-surface-variant">
                                {stat.change}
                            </p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Recent Orders Table */}
            <div className="bg-surface-container-lowest rounded-xl border border-surface-variant overflow-hidden">
                <div className="p-stack-lg border-b border-surface-variant">
                    <h2 className="font-h2 text-h2 text-primary">Đơn hàng gần đây</h2>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-surface-container border-b border-surface-variant">
                            <tr>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">
                                    Mã đơn
                                </th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">
                                    Khách hàng
                                </th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">
                                    Số tiền
                                </th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">
                                    Ngày đặt
                                </th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">
                                    Trạng thái
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {recentOrders.map((order, idx) => (
                                <tr key={idx} className="border-b border-surface-variant hover:bg-surface-container-low transition-colors">
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-primary font-bold">
                                        {order.id}
                                    </td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface">
                                        {order.customer}
                                    </td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface font-bold">
                                        {order.amount}
                                    </td>
                                    <td className="px-stack-lg py-stack-md font-caption text-caption text-on-surface-variant">
                                        {order.date}
                                    </td>
                                    <td className="px-stack-lg py-stack-md">
                                        <span
                                            className={`inline-block px-3 py-1 rounded-full font-caption text-caption font-bold ${getStatusColor(
                                                order.status
                                            )}`}
                                        >
                                            {order.status}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                <div className="px-stack-lg py-stack-md border-t border-surface-variant">
                    <button className="text-primary font-label-md text-label-md hover:underline">
                        Xem tất cả đơn hàng →
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
