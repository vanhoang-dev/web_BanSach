import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { AccentButton, Container, formatVnd, Icon, Panel, SecondaryButton, SectionHeading, StatusBadge } from '@/components/ui/staticUi';
import voucherService, { Voucher } from '@/features/vouchers/services/voucherService';
import { useAuth } from '@/hooks/useAuth';

const fallbackPromotions = [
  { code: 'SACHMOI25', title: 'Giảm giá sách mới', discountPercent: 25, maxDiscount: 50000, desc: 'Khám phá các đầu sách mới đang có giá ưu đãi trong danh mục sách.' },
  { code: 'COMBO15', title: 'Combo theo chủ đề', discountPercent: 15, maxDiscount: 40000, desc: 'Gom sách kinh doanh, kỹ năng và văn học theo nhu cầu đọc.' },
  { code: 'THANHVIEN10', title: 'Voucher thành viên', discountPercent: 10, maxDiscount: 30000, desc: 'Đăng nhập để xem và lưu các mã voucher hợp lệ từ hệ thống.' },
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
      <SectionHeading
        eyebrow="Khuyến mãi"
        title="Danh sách khuyến mãi"
        description="Lưu voucher phù hợp trước khi thanh toán và khám phá các ưu đãi đang áp dụng tại Nhà Sách Tri Thức."
        action={<Link to="/catalog"><SecondaryButton>Tiếp tục mua sách <Icon name="arrow" /></SecondaryButton></Link>}
      />

      <Panel className="mb-8 overflow-hidden border-secondary/30 bg-white shadow-md">
        <div className="grid md:grid-cols-[1fr_320px]">
          <div className="p-7 md:p-9">
            <p className="text-xs font-bold uppercase text-secondary">Ưu đãi nổi bật</p>
            <h2 className="mt-3 max-w-2xl text-3xl font-bold leading-tight text-primary md:text-4xl">Chọn voucher trước, mua sách tiết kiệm hơn</h2>
            <p className="mt-4 max-w-2xl text-sm leading-6 text-on-surface-variant">Voucher được lấy trực tiếp từ hệ thống khi bạn đăng nhập, đảm bảo đúng số lượng, trạng thái và thời hạn sử dụng.</p>
            <div className="mt-7 flex flex-wrap gap-3">
              <Link to="/catalog"><AccentButton>Mua ngay <Icon name="arrow" /></AccentButton></Link>
              {!isAuthenticated ? <Link to="/login"><SecondaryButton className="border-primary/20 bg-white text-primary hover:bg-[#fff7ef]">Đăng nhập nhận voucher</SecondaryButton></Link> : null}
            </div>
          </div>
          <div className="flex min-h-52 items-center justify-center bg-secondary-container/35 p-7">
            <div className="w-full rounded-lg border-2 border-dashed border-secondary/60 bg-white p-6 text-center shadow-sm">
              <p className="text-xs font-bold uppercase text-secondary">Voucher hot</p>
              <p className="mt-3 text-5xl font-bold text-primary">25%</p>
              <p className="mt-2 text-sm font-semibold text-on-surface-variant">Ưu đãi sách mới và combo chọn lọc</p>
            </div>
          </div>
        </div>
        </Panel>

      <div className="space-y-5">
        {error ? <div className="rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
        {message ? <div className="rounded-lg bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}

        {loading ? (
          <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
            {Array.from({ length: 6 }).map((_, index) => <div key={index} className="h-64 animate-pulse rounded-xl bg-surface-container" />)}
          </div>
        ) : null}

        <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          {!loading && vouchers.length > 0 ? vouchers.map((voucher) => (
            <Panel key={voucher.id || voucher.code} className="flex h-full flex-col overflow-hidden transition hover:-translate-y-1 hover:shadow-md">
              <div className="border-b border-dashed border-outline-variant bg-secondary-container/20 p-5">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <p className="text-xs font-bold uppercase text-secondary">Mã voucher</p>
                    <h3 className="mt-2 text-2xl font-bold text-primary">{voucher.code}</h3>
                  </div>
                  <StatusBadge status={voucher.isValid ? 'ACTIVE' : 'INACTIVE'}>{voucher.isValid ? 'Hợp lệ' : 'Không hợp lệ'}</StatusBadge>
                </div>
              </div>
              <div className="flex flex-1 flex-col p-5">
                <p className="text-4xl font-bold text-primary">-{voucher.discountPercent}%</p>
                <p className="mt-2 text-sm text-on-surface-variant">Giảm tối đa {formatVnd(voucher.maxDiscount)}</p>
                <div className="mt-5 space-y-2 text-sm text-on-surface-variant">
                  <p>Còn lại {Math.max(0, voucher.quantity - (voucher.usedQuantity || 0))} mã</p>
                  <p>Hết hạn {voucher.expiredAt || 'đang cập nhật'}</p>
                </div>
                <AccentButton
                  className="mt-auto w-full"
                  disabled={!voucher.id || claimedCodes.has(voucher.code) || claimingId === voucher.id}
                  onClick={() => claimVoucher(voucher)}
                >
                  {claimedCodes.has(voucher.code) ? 'Đã lấy voucher' : claimingId === voucher.id ? 'Đang lưu...' : 'Lấy voucher'}
                </AccentButton>
              </div>
            </Panel>
          )) : null}

          {!loading && vouchers.length === 0 ? fallbackPromotions.map((promo) => (
            <Panel key={promo.code} className="flex h-full flex-col overflow-hidden transition hover:-translate-y-1 hover:shadow-md">
              <div className="border-b border-dashed border-outline-variant bg-secondary-container/20 p-5">
                <div>
                  <p className="text-xs font-bold uppercase text-secondary">Mã voucher</p>
                  <h3 className="mt-2 text-2xl font-bold text-primary">{promo.code}</h3>
                </div>
              </div>
              <div className="flex flex-1 flex-col p-5">
                <p className="text-4xl font-bold text-primary">-{promo.discountPercent}%</p>
                <p className="mt-2 text-sm text-on-surface-variant">Giảm tối đa {formatVnd(promo.maxDiscount)}</p>
                <h3 className="mt-5 text-lg font-bold text-primary">{promo.title}</h3>
                <p className="mt-2 text-sm leading-6 text-on-surface-variant">{promo.desc}</p>
                {isAuthenticated ? (
                  <SecondaryButton className="mt-auto w-full" disabled>Đang chờ voucher</SecondaryButton>
                ) : (
                  <Link to="/login" className="mt-auto">
                    <AccentButton className="w-full">Đăng nhập nhận voucher</AccentButton>
                  </Link>
                )}
              </div>
            </Panel>
          )) : null}
        </div>
      </div>
    </Container>
  );
};

export default PromotionsPage;
