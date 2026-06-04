import { Link } from 'react-router-dom';
import { PageShell, Panel, SectionHeading } from './staticUi';

const CtaButton = ({ to, children, className = '' }) => (
    <Link
        to={to}
        className={`inline-flex items-center justify-center gap-unit rounded-lg px-5 py-3 font-label-md text-label-md transition-colors ${className}`}
    >
        {children}
    </Link>
);

const promotions = [
    { title: 'Giảm 20% cho sách mới', desc: 'Áp dụng cho các đầu sách ra mắt trong tháng này.' },
    { title: 'Mua 2 tặng 1', desc: 'Ưu đãi dành cho các bộ sách kỹ năng và thiếu nhi.' },
    { title: 'Miễn phí vận chuyển', desc: 'Đơn hàng từ 299.000 ₫ được giao miễn phí.' },
];

const PromotionsPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading
                eyebrow="Khuyến mãi"
                title="Ưu đãi đang diễn ra"
                description="Trang khuyến mãi tĩnh để menu 'Khuyến mãi' dẫn tới một màn hình có nội dung rõ ràng, không bị rơi về home."
            />

            <div className="grid gap-gutter lg:grid-cols-[1.2fr_0.8fr]">
                <Panel className="p-stack-lg bg-gradient-to-br from-primary to-secondary text-on-primary">
                    <p className="font-caption text-caption uppercase tracking-wider text-on-primary/80">Hot deal</p>
                    <h2 className="font-h2 text-h2 mt-unit">Giảm giá theo mùa cho độc giả BookStore</h2>
                    <p className="font-body-md text-body-md text-on-primary/80 mt-stack-md max-w-2xl">
                        Khối nội dung này giữ đúng tinh thần layout hiện tại: đậm, rõ và đủ để người dùng nhận ra đây là một trang đích riêng.
                    </p>
                    <div className="mt-stack-lg flex flex-wrap gap-stack-sm">
                        <CtaButton to="/catalog" className="bg-on-primary text-primary hover:bg-on-primary/90">
                            Mua ngay
                        </CtaButton>
                        <CtaButton to="/catalog" className="border border-on-primary text-on-primary hover:bg-on-primary/10">
                            Xem sách
                        </CtaButton>
                    </div>
                </Panel>

                <div className="space-y-gutter">
                    {promotions.map((promo) => (
                        <Panel key={promo.title} className="p-stack-md">
                            <h3 className="font-body-lg text-body-lg text-primary font-bold">{promo.title}</h3>
                            <p className="font-body-md text-body-md text-on-surface-variant mt-unit">{promo.desc}</p>
                        </Panel>
                    ))}
                </div>
            </div>
        </div>
    </PageShell>
);

export default PromotionsPage;
