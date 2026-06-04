import { PageShell, Panel, SectionHeading, Icon } from './staticUi';

const OrderDetailPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Đơn hàng" title="Chi tiết đơn hàng" description="Mẫu tĩnh cho trang xem chi tiết đơn hàng với timeline và tóm tắt hóa đơn." />
            <div className="grid gap-gutter lg:grid-cols-3">
                <Panel className="p-stack-lg lg:col-span-2">
                    <h3 className="font-h3 text-h3 text-primary mb-stack-md">Trạng thái đơn hàng</h3>
                    <div className="space-y-stack-md">
                        {['Đã đặt hàng', 'Đang đóng gói', 'Đang giao', 'Hoàn thành'].map((step, index) => (
                            <div key={step} className="flex items-start gap-stack-md">
                                <div className={`mt-1 w-3 h-3 rounded-full ${index < 3 ? 'bg-primary' : 'bg-surface-variant'}`} />
                                <div>
                                    <p className="font-label-md text-label-md text-on-surface font-bold">{step}</p>
                                    <p className="font-caption text-caption text-on-surface-variant">09/05/2026 10:{index}0</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </Panel>
                <Panel className="p-stack-lg">
                    <h3 className="font-h3 text-h3 text-primary mb-stack-md">Tóm tắt</h3>
                    <div className="space-y-stack-sm text-body-md">
                        <div className="flex justify-between"><span>Mã đơn</span><span>#ORD-001</span></div>
                        <div className="flex justify-between"><span>Tổng tiền</span><span>250.000 ₫</span></div>
                        <div className="flex justify-between"><span>Thanh toán</span><span>COD</span></div>
                    </div>
                    <button className="mt-stack-lg w-full rounded-lg border border-primary px-4 py-3 text-primary font-label-md inline-flex items-center justify-center gap-unit">{Icon({ name: 'file' })}Xuất hóa đơn</button>
                </Panel>
            </div>
        </div>
    </PageShell>
);

export default OrderDetailPage;
