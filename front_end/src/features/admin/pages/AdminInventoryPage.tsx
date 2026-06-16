import { FormEvent, useEffect, useState } from 'react';

import { AdminTable, AdminToolbar, Field, Icon, PrimaryButton, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import adminService from '@/features/admin/services';

const AdminInventoryPage = () => {
  const [inventory, setInventory] = useState<any[]>([]);
  const [selectedId, setSelectedId] = useState<number | ''>('');
  const [quantity, setQuantity] = useState(0);
  const [delta, setDelta] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchInventory = async () => {
    try {
      setLoading(true);
      const response = await adminService.getInventory(0, 100);
      setInventory(response.data.content || []);
    } catch {
      setError('Không thể tải tồn kho.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, []);

  const applySet = async (event: FormEvent) => {
    event.preventDefault();
    if (!selectedId) return;
    await adminService.updateInventory(Number(selectedId), Number(quantity));
    fetchInventory();
  };

  const applyAdjust = async () => {
    if (!selectedId) return;
    await adminService.adjustInventory(Number(selectedId), Number(delta));
    fetchInventory();
  };

  const applyReconcile = async () => {
    if (!selectedId) return;
    await adminService.reconcileInventory(Number(selectedId), Number(quantity));
    fetchInventory();
  };

  const totalStock = inventory.reduce((sum, item) => sum + Number(item.quantity || item.stock || 0), 0);
  const lowStock = inventory.filter((item) => Number(item.quantity || item.stock || 0) < 10).length;

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading eyebrow="Quản trị" title="Quản lý tồn kho" description="Theo dõi tồn kho, đặt số lượng, điều chỉnh nhập/xuất và đối soát theo /admin/inventory." />
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Tổng đầu tồn" value={inventory.length} icon="inventory" />
        <StatCard label="Tổng số lượng" value={totalStock} icon="book" tone="success" />
        <StatCard label="Cần bổ sung" value={lowStock} icon="order" tone="warning" />
      </div>
      {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      <form onSubmit={applySet} className="mt-6 grid gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4 md:grid-cols-[1fr_180px_180px_auto_auto]">
        <label className="block">
          <span className="mb-2 block text-sm font-semibold text-on-surface">Đầu sách tồn kho</span>
          <select value={selectedId} onChange={(e) => setSelectedId(e.target.value ? Number(e.target.value) : '')} className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm">
            <option value="">Chọn tồn kho</option>
            {inventory.map((item) => <option key={item.id} value={item.id}>{item.bookTitle || item.book?.title || `Tồn kho #${item.id}`}</option>)}
          </select>
        </label>
        <Field label="Số lượng mới" type="number" value={quantity} onChange={(e) => setQuantity(Number(e.target.value))} />
        <Field label="Delta nhập/xuất" type="number" value={delta} onChange={(e) => setDelta(Number(e.target.value))} />
        <div className="flex items-end"><PrimaryButton type="submit">Set</PrimaryButton></div>
        <div className="flex items-end gap-2"><PrimaryButton onClick={applyAdjust}>Điều chỉnh</PrimaryButton><PrimaryButton onClick={applyReconcile}>Đối soát</PrimaryButton></div>
      </form>
      <div className="mt-6">
        <AdminToolbar><span className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${inventory.length} dòng tồn kho`}</span></AdminToolbar>
        <AdminTable minWidth="760px">
          <thead className="border-b border-outline-variant bg-surface-container-high text-xs uppercase text-on-surface-variant"><tr>{['ID', 'Sách', 'Tồn kho', 'Cảnh báo'].map((head) => <th key={head} className="px-5 py-4">{head}</th>)}</tr></thead>
          <tbody className="divide-y divide-outline-variant">
            {inventory.map((item) => {
              const stock = Number(item.quantity || item.stock || 0);
              return (
                <tr key={item.id} className="hover:bg-surface-container-low">
                  <td className="px-5 py-4 font-bold text-primary">#{item.id}</td>
                  <td className="px-5 py-4 text-sm text-on-surface">{item.bookTitle || item.book?.title || item.title || '-'}</td>
                  <td className="px-5 py-4 text-sm font-bold text-primary">{stock}</td>
                  <td className="px-5 py-4"><StatusBadge status={stock < 10 ? 'PENDING' : 'ACTIVE'}>{stock < 10 ? 'Cần nhập' : 'Ổn định'}</StatusBadge></td>
                </tr>
              );
            })}
          </tbody>
        </AdminTable>
      </div>
    </div>
  );
};

export default AdminInventoryPage;
