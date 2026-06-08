import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Panel } from '@/components/ui/staticUi';
import orderService from '@/features/orders/services/orderService';

const CheckoutPage = () => {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        fullName: '',
        phoneNumber: '',
        shippingAddress: '',
        shippingMethod: 'STANDARD',
        paymentMethod: 'COD' as const,
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // Validation
        if (!form.fullName.trim()) {
            setError('Vui lòng nhập tên người nhận');
            return;
        }
        if (!form.phoneNumber.trim()) {
            setError('Vui lòng nhập số điện thoại');
            return;
        }
        if (!form.shippingAddress.trim()) {
            setError('Vui lòng nhập địa chỉ giao hàng');
            return;
        }

        try {
            setLoading(true);
            await orderService.createOrder({
                items: [],
                totalPrice: 0,
                fullName: form.fullName,
                phoneNumber: form.phoneNumber,
                shippingAddress: form.shippingAddress,
                paymentMethod: form.paymentMethod as 'COD' | 'BANK_TRANSFER' | 'SEPAY' | 'MOMO',
            });

            setSuccess('Đặt hàng thành công! Đang chuyển hướng...');
            setTimeout(() => {
                navigate('/orders');
            }, 2000);
        } catch (err: any) {
            setError(err.message || 'Đặt hàng thất bại. Vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <h1 className="font-h1 text-h1 text-primary mb-stack-lg">Thanh toán</h1>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-gutter">
                {/* Checkout Form */}
                <Panel className="lg:col-span-2 p-stack-lg">
                    <form onSubmit={handleSubmit} className="space-y-stack-md">
                        {error && (
                            <div className="bg-error/10 border border-error rounded-lg p-3">
                                <p className="font-body-md text-error">{error}</p>
                            </div>
                        )}

                        {success && (
                            <div className="bg-green-100 border border-green-500 rounded-lg p-3">
                                <p className="font-body-md text-green-600">{success}</p>
                            </div>
                        )}

                        <div>
                            <label className="block font-label-md text-label-md text-on-surface mb-unit">
                                Tên người nhận *
                            </label>
                            <input
                                type="text"
                                name="fullName"
                                value={form.fullName}
                                onChange={handleInputChange}
                                placeholder="Nhập tên người nhận"
                                className="w-full border border-outline-variant rounded-lg px-4 py-3 font-body-md focus:outline-none focus:ring-2 focus:ring-primary"
                            />
                        </div>

                        <div>
                            <label className="block font-label-md text-label-md text-on-surface mb-unit">
                                Số điện thoại *
                            </label>
                            <input
                                type="tel"
                                name="phoneNumber"
                                value={form.phoneNumber}
                                onChange={handleInputChange}
                                placeholder="Nhập số điện thoại"
                                className="w-full border border-outline-variant rounded-lg px-4 py-3 font-body-md focus:outline-none focus:ring-2 focus:ring-primary"
                            />
                        </div>

                        <div>
                            <label className="block font-label-md text-label-md text-on-surface mb-unit">
                                Địa chỉ giao hàng *
                            </label>
                            <input
                                type="text"
                                name="shippingAddress"
                                value={form.shippingAddress}
                                onChange={handleInputChange}
                                placeholder="Nhập địa chỉ giao hàng"
                                className="w-full border border-outline-variant rounded-lg px-4 py-3 font-body-md focus:outline-none focus:ring-2 focus:ring-primary"
                            />
                        </div>

                        <div>
                            <label className="block font-label-md text-label-md text-on-surface mb-unit">
                                Phương thức vận chuyển
                            </label>
                            <select
                                name="shippingMethod"
                                value={form.shippingMethod}
                                onChange={handleInputChange}
                                className="w-full border border-outline-variant rounded-lg px-4 py-3 font-body-md focus:outline-none focus:ring-2 focus:ring-primary"
                            >
                                <option value="STANDARD">Giao hàng tiêu chuẩn (3-5 ngày)</option>
                                <option value="EXPRESS">Giao hàng express (1-2 ngày)</option>
                                <option value="OVERNIGHT">Giao hàng qua đêm</option>
                            </select>
                        </div>

                        <div>
                            <label className="block font-label-md text-label-md text-on-surface mb-unit">
                                Phương thức thanh toán
                            </label>
                            <select
                                name="paymentMethod"
                                value={form.paymentMethod}
                                onChange={handleInputChange}
                                className="w-full border border-outline-variant rounded-lg px-4 py-3 font-body-md focus:outline-none focus:ring-2 focus:ring-primary"
                            >
                                <option value="COD">Thanh toán khi nhận hàng (COD)</option>
                                <option value="BANK_TRANSFER">Chuyển khoản ngân hàng</option>
                                <option value="SEPAY">SePay</option>
                                <option value="MOMO">MOMO</option>
                            </select>
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-primary text-on-primary font-label-md text-label-md py-3 rounded-lg hover:bg-primary-container transition-colors disabled:opacity-50 disabled:cursor-not-allowed mt-stack-lg"
                        >
                            {loading ? 'Đang xử lý...' : 'Xác nhận đặt hàng'}
                        </button>
                    </form>
                </Panel>

                {/* Order Summary */}
                <div className="lg:col-span-1">
                    <Panel className="p-stack-lg sticky top-24">
                        <h3 className="font-h3 text-h3 text-on-surface mb-stack-md">Đơn hàng của bạn</h3>
                        <div className="space-y-unit pb-stack-lg border-b border-outline-variant mb-stack-lg">
                            <div className="flex justify-between font-body-md text-on-surface-variant">
                                <span>Tạm tính</span>
                                <span>Tên riêng</span>
                            </div>
                            <div className="flex justify-between font-body-md text-on-surface-variant">
                                <span>Phí vận chuyển</span>
                                <span>Miễn phí</span>
                            </div>
                            <div className="flex justify-between font-body-md text-on-surface-variant">
                                <span>Mã giảm giá</span>
                                <span>Không có</span>
                            </div>
                        </div>

                        <div className="flex justify-between font-h3 text-h3 text-primary">
                            <span>Tổng cộng</span>
                            <span>Tên riêng</span>
                        </div>

                        <p className="font-caption text-caption text-on-surface-variant mt-stack-md text-center">
                            * Giá sẽ cập nhật dựa trên giỏ hàng của bạn
                        </p>
                    </Panel>
                </div>
            </div>
        </div>
    );
};

export default CheckoutPage;
