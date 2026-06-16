import { FormEvent, useEffect, useState } from 'react';

import { AdminTable, AdminToolbar, Field, Icon, IconButton, PrimaryButton, SecondaryButton, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import adminService from '@/features/admin/services';

const emptyForm = { name: '', description: '', isActive: true };

const AdminCategoryManagementPage = () => {
  const [categories, setCategories] = useState<any[]>([]);
  const [form, setForm] = useState<any>(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await adminService.getCategories(0, 100);
      setCategories(response.data.content || []);
    } catch {
      setError('Không thể tải danh mục.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    try {
      if (editingId) await adminService.updateCategory(editingId, form);
      else await adminService.addCategory(form);
      setForm(emptyForm);
      setEditingId(null);
      fetchCategories();
    } catch {
      setError('Không thể lưu danh mục.');
    }
  };

  const edit = (category: any) => {
    setEditingId(category.id);
    setForm({ name: category.name || '', description: category.description || '', isActive: category.isActive !== false });
  };

  const remove = async (id: number) => {
    if (!window.confirm('Xóa danh mục này?')) return;
    await adminService.deleteCategory(id);
    fetchCategories();
  };

  const toggle = async (category: any) => {
    if (category.isActive === false) await adminService.activateCategory(category.id);
    else await adminService.deactivateCategory(category.id);
    fetchCategories();
  };

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading eyebrow="Quản trị" title="Quản lý danh mục" description="Tạo, sửa, xóa, kích hoạt và tạm ẩn danh mục theo API quản trị danh mục." />
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Tổng danh mục" value={categories.length} icon="category" />
        <StatCard label="Đang hoạt động" value={categories.filter((item) => item.isActive !== false).length} icon="chart" tone="success" />
        <StatCard label="Tạm ẩn" value={categories.filter((item) => item.isActive === false).length} icon="trash" tone="warning" />
      </div>
      {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      <form onSubmit={submit} className="mt-6 grid gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4 md:grid-cols-[1fr_1fr_auto]">
        <Field label="Tên danh mục" value={form.name} onChange={(e) => setForm((current: any) => ({ ...current, name: e.target.value }))} required />
        <Field label="Mô tả" value={form.description} onChange={(e) => setForm((current: any) => ({ ...current, description: e.target.value }))} />
        <div className="flex items-end gap-2">
          <PrimaryButton type="submit"><Icon name="plus" /> {editingId ? 'Cập nhật' : 'Thêm'}</PrimaryButton>
          {editingId ? <SecondaryButton onClick={() => { setEditingId(null); setForm(emptyForm); }}>Hủy</SecondaryButton> : null}
        </div>
      </form>
      <div className="mt-6">
        <AdminToolbar><span className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${categories.length} danh mục`}</span></AdminToolbar>
        <AdminTable minWidth="680px">
          <thead className="border-b border-outline-variant bg-surface-container-high text-xs uppercase text-on-surface-variant"><tr>{['Tên danh mục', 'Mô tả', 'Trạng thái', ''].map((head) => <th key={head} className="px-5 py-4">{head}</th>)}</tr></thead>
          <tbody className="divide-y divide-outline-variant">
            {categories.map((category) => (
              <tr key={category.id} className="hover:bg-surface-container-low">
                <td className="px-5 py-4 font-bold text-primary">{category.name}</td>
                <td className="px-5 py-4 text-sm text-on-surface-variant">{category.description || '-'}</td>
                <td className="px-5 py-4"><button onClick={() => toggle(category)}><StatusBadge status={category.isActive === false ? 'INACTIVE' : 'ACTIVE'} /></button></td>
                <td className="px-5 py-4"><div className="flex justify-end gap-2"><IconButton onClick={() => edit(category)}><Icon name="edit" /></IconButton><IconButton onClick={() => remove(category.id)}><Icon name="trash" /></IconButton></div></td>
              </tr>
            ))}
          </tbody>
        </AdminTable>
      </div>
    </div>
  );
};

export default AdminCategoryManagementPage;
