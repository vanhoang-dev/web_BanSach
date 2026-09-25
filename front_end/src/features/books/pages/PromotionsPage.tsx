import { useEffect, useState } from 'react';

import { AccentButton, Container, EmptyState, formatVnd, Icon, LinkButton, Panel, SectionHeading, StatusBadge } from '@/components/ui/staticUi';
import voucherService, { Voucher } from '@/features/vouchers/services/voucherService';
import { useAuth } from '@/hooks/useAuth';

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
        action={<LinkButton to="/catalog" variant="secondary">Tiếp tục mua sách <Icon name="arrow" /></LinkButton>}
      />

      <Panel className="mb-8 overflow-hidden border-secondary-container bg-white">
        <div className="grid min-w-0 lg:grid-cols-[minmax(0,1fr)_320px]">
          <div className="min-w-0 p-6 sm:p-8 lg:p-9">
            <p className="text-xs font-bold uppercase text-secondary">Ưu đãi nổi bật</p>
            <h2 className="mt-3 max-w-2xl font-serif text-[28px] font-bold leading-[1.22] text-primary sm:text-3xl lg:text-4xl">Chọn voucher trước, mua sách tiết kiệm hơn</h2>
            <p className="mt-4 max-w-2xl text-sm leading-6 text-on-surface-variant">Voucher được lấy trực tiếp từ hệ thống khi bạn đăng nhập, đảm bảo đúng số lượng, trạng thái và thời hạn sử dụng.</p>
            <div className="mt-7 grid grid-cols-1 gap-3 min-[430px]:flex min-[430px]:flex-wrap">
              <LinkButton to="/catalog" variant="accent" className="w-full min-[430px]:w-auto">Mua ngay <Icon name="arrow" /></LinkButton>
              {!isAuthenticated ? <LinkButton to="/login" variant="secondary" className="w-full border-primary/20 bg-white text-primary hover:bg-secondary-container/10 min-[430px]:w-auto">Đăng nhập nhận voucher</LinkButton> : null}
            </div>
          </div>
          <div className="flex min-h-52 items-center justify-center bg-secondary-container/35 p-7">
            <div className="w-full rounded-lg border-2 border-dashed border-secondary/60 bg-white p-6 text-center shadow-sm">
              <Icon name="ticket" className="mx-auto h-10 w-10 text-primary" />
              <p className="mt-4 text-xl font-bold text-primary">Voucher được cập nhật trực tiếp</p>
              <p className="mt-2 text-sm font-semibold text-on-surface-variant">Chỉ hiển thị ưu đãi có thật từ hệ thống</p>
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
            <Panel key={voucher.id || voucher.code} className="flex h-full flex-col overflow-hidden transition hover:-translate-y-0.5 hover:border-border-strong hover:shadow-sm">
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

          {!loading && vouchers.length === 0 ? (
            <div className="md:col-span-2 xl:col-span-3">
              <EmptyState
                title={isAuthenticated ? 'Hiện chưa có voucher' : 'Đăng nhập để xem voucher'}
                description={isAuthenticated ? 'Các voucher hợp lệ sẽ xuất hiện tại đây khi được phát hành.' : 'Voucher được tải trực tiếp từ tài khoản của bạn, không sử dụng dữ liệu minh họa.'}
                action={!isAuthenticated ? <LinkButton to="/login" variant="primary">Đăng nhập</LinkButton> : undefined}
              />
            </div>
          ) : null}
        </div>
      </div>
    </Container>
  );
};

export default PromotionsPage;
