
const AdminOrderManagementPage = () => {
    const orders = [
        { code: '#ORD-101', customer: 'Nguyễn Văn A', amount: '₫250.000', status: 'Completed' },
        { code: '#ORD-102', customer: 'Trần Thị B', amount: '₫180.000', status: 'Processing' },
        { code: '#ORD-103', customer: 'Lê Văn C', amount: '₫420.000', status: 'Pending' },
    ];

    const statusClass = (status) => {
        switch (status) {
            case 'Completed':
                return 'bg-green-100 text-green-800';
            case 'Processing':
                return 'bg-blue-100 text-blue-800';
            default:
                return 'bg-yellow-100 text-yellow-800';
        }
    };

    return (
        <div className="w-full">
            <div className="mb-section-gap">
                <h1 className="font-h1 text-h1 text-primary mb-unit">Quản lý đơn hàng</h1>
                <p className="font-body-md text-body-md text-on-surface-variant">Xem nhanh các đơn hàng và trạng thái xử lý trong hệ thống quản trị.</p>
            </div>

            <div className="bg-surface-container-lowest rounded-xl border border-surface-variant overflow-hidden">
                <div className="p-stack-lg border-b border-surface-variant flex items-center justify-between gap-4">
                    <h2 className="font-h2 text-h2 text-primary">Đơn hàng gần đây</h2>
                    <button className="inline-flex items-center justify-center rounded-lg bg-primary px-4 py-2 font-label-md text-label-md text-on-primary hover:bg-primary-container transition-colors">
                        Xuất báo cáo
                    </button>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-surface-container border-b border-surface-variant">
                            <tr>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Mã đơn</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Khách hàng</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Số tiền</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Trạng thái</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map((order) => (
                                <tr key={order.code} className="border-b border-surface-variant hover:bg-surface-container-low transition-colors">
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-primary font-bold">{order.code}</td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface">{order.customer}</td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface font-bold">{order.amount}</td>
                                    <td className="px-stack-lg py-stack-md">
                                        <span className={`inline-flex px-3 py-1 rounded-full font-caption text-caption font-bold ${statusClass(order.status)}`}>
                                            {order.status}
                                        </span>
                                    </td>
                                    <td className="px-stack-lg py-stack-md">
                                        <div className="flex gap-stack-sm">
                                            <button className="text-primary font-label-md text-label-md hover:underline">Chi tiết</button>
                                            <button className="text-secondary font-label-md text-label-md hover:underline">Cập nhật</button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminOrderManagementPage;
