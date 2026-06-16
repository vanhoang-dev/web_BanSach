import { FormEvent, useEffect, useState } from 'react';

import { Field, formatVnd, Icon, IconButton, Panel, PrimaryButton, SecondaryButton, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import adminService from '@/features/admin/services';

const emptyForm = { code: '', discountPercent: 10, maxDiscount: 50000, quantity: 10, expiredAt: '' };

const AdminVoucherManagementPage = () => {
  const [vouchers, setVouchers] = useState<any[]>([]);
  const [form, setForm] = useState<any>(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchVouchers = async () => {
    try {
      setLoading(true);
      const response = await adminService.getVouchers(0, 100);
      setVouchers(response.data.content || []);
    } catch {
      setError('Không thể tải voucher.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVouchers();
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    try {
      if (editingId) await adminService.updateVoucher(editingId, form);
      else await adminService.addVoucher(form);
      setForm(emptyForm);
      setEditingId(null);
      fetchVouchers();
    } catch {
      setError('Không thể lưu voucher.');
    }
  };

  const edit = (voucher: any) => {
    setEditingId(voucher.id);
    setForm({
      code: voucher.code || '',
      discountPercent: voucher.discountPercent || 10,
      maxDiscount: voucher.maxDiscount || 0,
      quantity: voucher.quantity || 1,
      expiredAt: voucher.expiredAt || '',
    });
  };

  const remove = async (id: number) => {
    if (!window.confirm('Xóa voucher này?')) return;
    await adminService.deleteVoucher(id);
    fetchVouchers();
  };

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading eyebrow="Quản trị" title="Quản lý voucher" description="Tạo, sửa, xóa và xem voucher theo phân hệ /admin/vouchers." />
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Tổng voucher" value={vouchers.length} icon="ticket" />
        <StatCard label="Hợp lệ" value={vouchers.filter((item) => item.isValid !== false && item.isExpired !== true).length} icon="chart" tone="success" />
        <StatCard label="Hết hạn" value={vouchers.filter((item) => item.isExpired).length} icon="order" tone="warning" />
      </div>
      {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      <form onSubmit={submit} className="mt-6 grid gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4 md:grid-cols-6">
        <Field className="md:col-span-2" label="Mã voucher" value={form.code} onChange={(e) => setForm((current: any) => ({ ...current, code: e.target.value.toUpperCase() }))} required />
        <Field label="% giảm" type="number" value={form.discountPercent} onChange={(e) => setForm((current: any) => ({ ...current, discountPercent: Number(e.target.value) }))} required />
        <Field label="Giảm tối đa" type="number" value={form.maxDiscount} onChange={(e) => setForm((current: any) => ({ ...current, maxDiscount: Number(e.target.value) }))} required />
        <Field label="Số lượng" type="number" value={form.quantity} onChange={(e) => setForm((current: any) => ({ ...current, quantity: Number(e.target.value) }))} required />
        <Field label="Hết hạn" type="date" value={form.expiredAt} onChange={(e) => setForm((current: any) => ({ ...current, expiredAt: e.target.value }))} required />
        <div className="flex items-end gap-2 md:col-span-6">
          <PrimaryButton type="submit"><Icon name="plus" /> {editingId ? 'Cập nhật' : 'Tạo voucher'}</PrimaryButton>
          {editingId ? <SecondaryButton onClick={() => { setEditingId(null); setForm(emptyForm); }}>Hủy</SecondaryButton> : null}
        </div>
      </form>
      <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {loading ? Array.from({ length: 3 }).map((_, index) => <div key={index} className="h-48 animate-pulse rounded-xl bg-surface-container" />) : vouchers.map((voucher) => (
          <Panel key={voucher.id} className="p-5">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-xs font-bold uppercase text-secondary">Mã voucher</p>
                <h3 className="mt-2 text-2xl font-bold text-primary">{voucher.code}</h3>
                <p className="mt-2 text-sm text-on-surface-variant">Giảm {voucher.discountPercent}% tối đa {formatVnd(Number(voucher.maxDiscount || 0))}</p>
                <p className="mt-1 text-sm text-on-surface-variant">Đã dùng {voucher.usedQuantity || 0}/{voucher.quantity || 0}, hết hạn {voucher.expiredAt || '-'}</p>
              </div>
              <StatusBadge status={voucher.isExpired ? 'INACTIVE' : 'ACTIVE'}>{voucher.isExpired ? 'Hết hạn' : 'Hợp lệ'}</StatusBadge>
            </div>
            <div className="mt-5 flex gap-2"><IconButton onClick={() => edit(voucher)}><Icon name="edit" /></IconButton><IconButton onClick={() => remove(voucher.id)}><Icon name="trash" /></IconButton></div>
          </Panel>
        ))}
      </div>
    </div>
  );
};

export default AdminVoucherManagementPage;
