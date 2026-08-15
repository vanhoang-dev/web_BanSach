import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { AccentButton, Container, formatVnd, Icon, Panel, SecondaryButton, SectionHeading, StatusBadge } from '@/components/ui/staticUi';
import voucherService, { Voucher } from '@/features/vouchers/services/voucherService';
import { useAuth } from '@/hooks/useAuth';

const fallbackPromotions = [
  { title: 'Giảm giá sách mới', desc: 'Khám phá các đầu sách mới đang có giá ưu đãi trong danh mục sách.' },
  { title: 'Combo theo chủ đề', desc: 'Gom sách kinh doanh, kỹ năng và văn học theo nhu cầu đọc.' },
  { title: 'Voucher thành viên', desc: 'Đăng nhập để xem các mã voucher hợp lệ từ hệ thống.' },
];

const PromotionsPage = () => {
  const { isAuthenticated } = useAuth();
  const [vouchers, setVouchers] = useState<Voucher[]>([]);
  const [loading, setLoading] = useState(isAuthenticated);
  const [error, setError] = useState('');
  const [claimedCodes, setClaimedCodes] = useState<Set<string>>(new Set());
  const [claimingId, setClaimingId] = useState<number | null>(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const fetchVouchers = async () => {
      if (!isAuthenticated) {
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        const [response, ownedResponse] = await Promise.all([
          voucherService.getVouchers(0, 12),
          voucherService.getClaimedVouchers(0, 100),
        ]);
        setVouchers(response.data.content || []);
        setClaimedCodes(new Set((ownedResponse.data.content || []).map((voucher: Voucher) => voucher.code)));
      } catch {
        setError('Không thể tải voucher của tài khoản.');
      } finally {
        setLoading(false);
      }
    };
    fetchVouchers();
  }, [isAuthenticated]);

  const claimVoucher = async (voucher: Voucher) => {
    if (!voucher.id || claimedCodes.has(voucher.code)) return;
    try {
      setClaimingId(voucher.id);
      setError('');
      await voucherService.claimVoucher(voucher.id);
      setClaimedCodes((current) => new Set(current).add(voucher.code));
      setMessage(`Đã lưu voucher ${voucher.code} vào tài khoản.`);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Không thể lấy voucher.');
    } finally {
      setClaimingId(null);
    }
  };

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Khuyến mãi" title="Ưu đãi đang diễn ra" description="Nhận voucher hợp lệ và khám phá các chương trình ưu đãi đang được áp dụng." />
      <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <Panel className="relative overflow-hidden border-secondary/40 bg-white p-8 text-primary shadow-md md:p-10">
          <div className="absolute right-0 top-0 h-full w-1/2 bg-[#ff8f2f]" />
          <div className="absolute inset-y-0 left-0 w-1/2 bg-[#fff7ef]" />
          <div className="relative">
            <p className="text-xs font-bold uppercase text-secondary">Ưu đãi nổi bật</p>
            <h2 className="mt-3 max-w-2xl text-4xl font-bold leading-tight text-primary">Ưu đãi dành cho độc giả Nhà Sách Tri Thức</h2>
            <p className="mt-4 max-w-2xl text-on-surface-variant">Voucher được lấy trực tiếp từ hệ thống khi bạn đăng nhập, đảm bảo đúng số lượng và thời hạn sử dụng.</p>
            <div className="mt-7 flex flex-wrap gap-3">
              <Link to="/catalog"><AccentButton>Mua ngay <Icon name="arrow" /></AccentButton></Link>
              {!isAuthenticated ? <Link to="/login"><SecondaryButton className="border-primary/20 bg-white text-primary hover:bg-[#fff7ef]">Đăng nhập nhận voucher</SecondaryButton></Link> : null}
            </div>
          </div>
        </Panel>

        <div className="space-y-4">
          {loading ? Array.from({ length: 3 }).map((_, index) => <div key={index} className="h-36 animate-pulse rounded-xl bg-surface-container" />) : null}
          {error ? <div className="rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          {message ? <div className="rounded-lg bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}
          {!loading && vouchers.length > 0 ? vouchers.map((voucher) => (
            <Panel key={voucher.id || voucher.code} className="p-5">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="text-xs font-bold uppercase text-secondary">Mã voucher</p>
                  <h3 className="mt-2 text-2xl font-bold text-primary">{voucher.code}</h3>
                  <p className="mt-2 text-sm text-on-surface-variant">Giảm {voucher.discountPercent}% tối đa {formatVnd(voucher.maxDiscount)}</p>
                  <p className="mt-1 text-sm text-on-surface-variant">Còn lại {Math.max(0, voucher.quantity - (voucher.usedQuantity || 0))} mã, hết hạn {voucher.expiredAt || 'đang cập nhật'}</p>
                </div>
              <StatusBadge status={voucher.isValid ? 'ACTIVE' : 'INACTIVE'}>{voucher.isValid ? 'Hợp lệ' : 'Không hợp lệ'}</StatusBadge>
            </div>
            <AccentButton
              className="mt-4 w-full"
              disabled={!voucher.id || claimedCodes.has(voucher.code) || claimingId === voucher.id}
              onClick={() => claimVoucher(voucher)}
            >
              {claimedCodes.has(voucher.code) ? 'Đã lấy voucher' : claimingId === voucher.id ? 'Đang lưu...' : 'Lấy voucher'}
            </AccentButton>
          </Panel>
          )) : null}
          {!loading && vouchers.length === 0 ? fallbackPromotions.map((promo) => (
            <Panel key={promo.title} className="p-5">
              <h3 className="text-lg font-bold text-primary">{promo.title}</h3>
              <p className="mt-2 text-sm leading-6 text-on-surface-variant">{promo.desc}</p>
            </Panel>
          )) : null}
        </div>
      </div>
    </Container>
  );
};

export default PromotionsPage;
