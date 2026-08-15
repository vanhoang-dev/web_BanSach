import { FormEvent, useEffect, useState } from 'react';

import { AdminTable, AdminToolbar, Field, Icon, IconButton, PrimaryButton, SecondaryButton, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import AdminPagination from '@/features/admin/components/AdminPagination';
import userAdminService from '@/features/admin/services/userAdminService';

const emptyForm = { fullName: '', email: '', phone: '', address: '', isActive: true };
const pageSize = 10;

const AdminUserManagementPage = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [form, setForm] = useState<any>(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await userAdminService.getAll(page, pageSize);
      setUsers(response.data.content || []);
      setPageInfo({ totalElements: response.data.totalElements, totalPages: response.data.totalPages });
    } catch {
      setError('Không thể tải người dùng.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (!editingId) return;
    try {
      await userAdminService.update(editingId, form);
      setEditingId(null);
      setForm(emptyForm);
      fetchUsers();
    } catch {
      setError('Không thể cập nhật người dùng.');
    }
  };

  const edit = (user: any) => {
    setEditingId(user.userId || user.id);
    setForm({
      fullName: user.fullName || '',
      email: user.email || '',
      phone: user.phone || '',
      address: user.address || '',
      isActive: user.isActive !== false,
    });
  };

  const remove = async (id: number) => {
    if (!window.confirm('Xóa người dùng này?')) return;
    await userAdminService.remove(id);
    fetchUsers();
  };

  const visibleUsers = filter ? users.filter((user) => `${user.fullName || ''} ${user.email || ''} ${user.username || ''}`.toLowerCase().includes(filter.toLowerCase())) : users;

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading eyebrow="Quản trị" title="Quản lý người dùng" description="Xem hồ sơ, cập nhật và xóa tài khoản theo phân hệ quản trị người dùng." />
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Tổng người dùng" value={users.length} icon="users" />
        <StatCard label="Đang hoạt động" value={users.filter((item) => item.isActive !== false).length} icon="user" tone="success" />
        <StatCard label="Quản trị viên" value={users.filter((item) => Array.from(item.roles || []).some((role: any) => String(role).includes('ADMIN'))).length} icon="chart" />
      </div>
      {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      {editingId ? (
        <form onSubmit={submit} className="mt-6 grid gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4 md:grid-cols-3">
          <Field label="Họ tên" value={form.fullName} onChange={(e) => setForm((current: any) => ({ ...current, fullName: e.target.value }))} required />
          <Field label="Email" value={form.email} onChange={(e) => setForm((current: any) => ({ ...current, email: e.target.value }))} type="email" />
          <Field label="Số điện thoại" value={form.phone} onChange={(e) => setForm((current: any) => ({ ...current, phone: e.target.value }))} />
          <Field className="md:col-span-2" label="Địa chỉ" value={form.address} onChange={(e) => setForm((current: any) => ({ ...current, address: e.target.value }))} />
          <label className="flex items-end gap-3 text-sm font-semibold text-on-surface"><input checked={form.isActive} onChange={(e) => setForm((current: any) => ({ ...current, isActive: e.target.checked }))} type="checkbox" /> Hoạt động</label>
          <div className="flex gap-2 md:col-span-3"><PrimaryButton type="submit">Lưu người dùng</PrimaryButton><SecondaryButton onClick={() => { setEditingId(null); setForm(emptyForm); }}>Hủy</SecondaryButton></div>
        </form>
      ) : null}
      <div className="mt-6">
        <AdminToolbar>
          <input value={filter} onChange={(e) => { setFilter(e.target.value); setPage(0); }} className="h-11 w-full rounded-lg border-outline-variant bg-surface px-4 text-sm md:max-w-sm" placeholder="Tìm tên hoặc email..." />
          <span className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${pageInfo.totalElements} người dùng`}</span>
        </AdminToolbar>
        <AdminTable minWidth="860px">
          <thead className="border-b border-outline-variant bg-surface-container-high text-xs uppercase text-on-surface-variant"><tr>{['Tên', 'Email', 'Vai trò', 'Trạng thái', ''].map((head) => <th key={head} className="px-5 py-4">{head}</th>)}</tr></thead>
          <tbody className="divide-y divide-outline-variant">
            {visibleUsers.map((user) => {
              const id = user.userId || user.id;
              return (
                <tr key={id} className="hover:bg-surface-container-low">
                  <td className="px-5 py-4 font-bold text-primary">{user.fullName || user.username || '-'}</td>
                  <td className="px-5 py-4 text-sm text-on-surface-variant">{user.email || '-'}</td>
                  <td className="px-5 py-4 text-sm font-bold text-on-surface">{Array.from(user.roles || ['USER']).join(', ')}</td>
                  <td className="px-5 py-4"><StatusBadge status={user.isActive === false ? 'INACTIVE' : 'ACTIVE'} /></td>
                  <td className="px-5 py-4"><div className="flex justify-end gap-2"><IconButton onClick={() => edit(user)}><Icon name="edit" /></IconButton><IconButton onClick={() => remove(id)}><Icon name="trash" /></IconButton></div></td>
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

export default AdminUserManagementPage;
