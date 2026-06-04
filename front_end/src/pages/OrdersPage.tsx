import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Panel } from './staticUi';
import orderService from '../services/order';

const OrdersPage = () => {
    const [orders, setOrders] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                setLoading(true);
                setError('');
                const response = await orderService.getOrders(page, 10);
                setOrders(response?.data?.content || []);
                setTotalPages(response?.data?.totalPages || 0);
            } catch (err: any) {
                setError('Không thể tải danh sách đơn hàng');
            } finally {
                setLoading(false);
            }
        };

        fetchOrders();
    }, [page]);

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'PENDING':
                return 'text-warning bg-warning/10';
            case 'CONFIRMED':
                return 'text-blue-600 bg-blue-100';
            case 'SHIPPED':
                return 'text-blue-600 bg-blue-100';
            case 'DELIVERED':
                return 'text-green-600 bg-green-100';
            case 'CANCELLED':
                return 'text-error bg-error/10';
            default:
                return 'text-on-surface-variant';
        }
    };

    const getStatusLabel = (status: string) => {
        const labels: Record<string, string> = {
            PENDING: 'Chờ xác nhận',
            CONFIRMED: 'Đã xác nhận',
            SHIPPED: 'Đang giao',
            DELIVERED: 'Đã giao',
            CANCELLED: 'Đã hủy',
        };
        return labels[status] || status;
    };

    if (loading && orders.length === 0) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
        );
    }

    return (
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <h1 className="font-h1 text-h1 text-primary mb-stack-lg">Đơn hàng của tôi</h1>

            {error && (
                <div className="bg-error/10 border border-error rounded-lg p-stack-md mb-stack-lg">
                    <p className="font-body-md text-error">{error}</p>
                </div>
            )}

            {orders.length === 0 ? (
                <Panel className="p-stack-lg text-center">
                    <p className="font-body-lg text-body-lg text-on-surface-variant mb-stack-md">Bạn chưa có đơn hàng nào</p>
                    <Link to="/catalog" className="text-primary hover:underline">
                        ← Bắt đầu mua sắm
                    </Link>
                </Panel>
            ) : (
                <div className="space-y-stack-md">
                    {orders.map((order: any) => (
                        <Panel key={order.id} className="p-stack-lg">
                            <div className="grid grid-cols-1 md:grid-cols-4 gap-gutter items-start mb-stack-md">
                                <div>
                                    <p className="font-caption text-caption text-on-surface-variant mb-unit">Mã đơn</p>
                                    <p className="font-body-lg text-body-lg font-bold text-primary">#{order.id}</p>
                                </div>

                                <div>
                                    <p className="font-caption text-caption text-on-surface-variant mb-unit">Ngày đặt</p>
                                    <p className="font-body-md text-body-md">
                                        {new Date(order.createdAt || order.date).toLocaleDateString('vi-VN')}
                                    </p>
                                </div>

                                <div>
                                    <p className="font-caption text-caption text-on-surface-variant mb-unit">Trạng thái</p>
                                    <span className={`inline-block font-label-md text-label-md px-3 py-1 rounded-full ${getStatusColor(order.status)}`}>
                                        {getStatusLabel(order.status)}
                                    </span>
                                </div>

                                <div className="md:text-right">
                                    <p className="font-caption text-caption text-on-surface-variant mb-unit">Tổng tiền</p>
                                    <p className="font-h3 text-h3 text-primary">
                                        {order.totalPrice?.toLocaleString('vi-VN')} ₫
                                    </p>
                                </div>
                            </div>

                            <div className="border-t border-outline-variant pt-stack-md flex justify-between items-center">
                                <p className="font-body-md text-body-md text-on-surface-variant">
                                    {order.items?.length || 0} sản phẩm
                                </p>
                                <Link
                                    to={`/orders/${order.id}`}
                                    className="text-primary hover:underline font-label-md text-label-md"
                                >
                                    Xem chi tiết →
                                </Link>
                            </div>
                        </Panel>
                    ))}

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="flex justify-center gap-unit mt-stack-lg">
                            <button
                                disabled={page === 0}
                                onClick={() => setPage(Math.max(0, page - 1))}
                                className="px-4 py-2 border border-outline-variant rounded-lg hover:bg-surface-container disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                Trước
                            </button>
                            <span className="px-4 py-2">Trang {page + 1} / {totalPages}</span>
                            <button
                                disabled={page >= totalPages - 1}
                                onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                                className="px-4 py-2 border border-outline-variant rounded-lg hover:bg-surface-container disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                Tiếp
                            </button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default OrdersPage;
