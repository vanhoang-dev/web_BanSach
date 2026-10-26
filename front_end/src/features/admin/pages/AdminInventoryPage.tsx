import { FormEvent, useEffect, useState } from 'react';

import { AdminTable, AdminToolbar, Field, PrimaryButton, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import AdminPagination from '@/features/admin/components/AdminPagination';
import inventoryAdminService from '@/features/admin/services/inventoryAdminService';
import { notify } from '@/features/notifications/NotificationProvider';

const pageSize = 10;

const AdminInventoryPage = () => {
  const [inventory, setInventory] = useState<any[]>([]);
  const [selectedId, setSelectedId] = useState<number | ''>('');
  const [quantity, setQuantity] = useState(0);
  const [delta, setDelta] = useState(0);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });

  const fetchInventory = async () => {
    try {
      setLoading(true);
      const response = await inventoryAdminService.getAll(page, pageSize);
      setInventory(response.data.content || []);
      setPageInfo({ totalElements: response.data.totalElements, totalPages: response.data.totalPages });
    } catch {
      notify.error('Không thể tải tồn kho. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, [page]);

  const applySet = async (event: FormEvent) => {
    event.preventDefault();
    if (!selectedId) return;
    try { await inventoryAdminService.setQuantity(Number(selectedId), Number(quantity)); await fetchInventory(); notify.success('Đã cập nhật số lượng tồn kho.'); }
    catch { notify.error('Không thể cập nhật số lượng tồn kho.'); }
  };

  const applyAdjust = async () => {
    if (!selectedId) return;
    try { await inventoryAdminService.adjust(Number(selectedId), Number(delta)); await fetchInventory(); notify.success('Đã điều chỉnh tồn kho.'); }
    catch { notify.error('Không thể điều chỉnh tồn kho.'); }
  };

  const applyReconcile = async () => {
    if (!selectedId) return;
    try { await inventoryAdminService.reconcile(Number(selectedId), Number(quantity)); await fetchInventory(); notify.success('Đã đối soát tồn kho.'); }
    catch { notify.error('Không thể đối soát tồn kho.'); }
  };

  const totalStock = inventory.reduce((sum, item) => sum + Number(item.quantity || item.stock || 0), 0);
  const lowStock = inventory.filter((item) => Number(item.quantity || item.stock || 0) < 10).length;

  return (
    <div className="w-full min-w-0">
      <SectionHeading eyebrow="Quản trị" title="Quản lý tồn kho" description="Theo dõi tồn kho, đặt số lượng, điều chỉnh nhập/xuất và đối soát theo /admin/inventory." />
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Tổng đầu tồn" value={pageInfo.totalElements} icon="inventory" />
        <StatCard label="Tổng số lượng" value={totalStock} icon="book" tone="success" />
        <StatCard label="Cần bổ sung" value={lowStock} icon="order" tone="warning" />
      </div>
      <form onSubmit={applySet} className="mt-6 grid min-w-0 gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4 sm:grid-cols-2 xl:grid-cols-[minmax(0,1fr)_180px_180px_auto_auto]">
        <label className="block">
          <span className="mb-2 block text-sm font-semibold text-on-surface">Đầu sách tồn kho</span>
          <select value={selectedId} onChange={(e) => setSelectedId(e.target.value ? Number(e.target.value) : '')} className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm">
            <option value="">Chọn tồn kho</option>
            {inventory.map((item) => <option key={item.id} value={item.id}>{item.bookTitle || item.book?.title || `Tồn kho #${item.id}`}</option>)}
          </select>
        </label>
        <Field label="Số lượng mới" type="number" value={quantity} onChange={(e) => setQuantity(Number(e.target.value))} />
        <Field label="Số lượng nhập/xuất" type="number" value={delta} onChange={(e) => setDelta(Number(e.target.value))} />
        <div className="flex items-end"><PrimaryButton type="submit">Đặt số lượng</PrimaryButton></div>
        <div className="flex items-end gap-2"><PrimaryButton onClick={applyAdjust}>Điều chỉnh</PrimaryButton><PrimaryButton onClick={applyReconcile}>Đối soát</PrimaryButton></div>
      </form>
      <div className="mt-6">
        <AdminToolbar><span className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${pageInfo.totalElements} dòng tồn kho`}</span></AdminToolbar>
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
        <AdminPagination page={page} pageSize={pageSize} totalElements={pageInfo.totalElements} totalPages={pageInfo.totalPages} onPageChange={setPage} />
      </div>
    </div>
  );
};

export default AdminInventoryPage;
