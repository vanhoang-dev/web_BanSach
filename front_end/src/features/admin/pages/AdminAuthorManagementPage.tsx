import { FormEvent, useEffect, useState } from 'react';

import { Field, Icon, IconButton, Panel, PrimaryButton, SecondaryButton, SectionHeading, StatCard } from '@/components/ui/staticUi';
import adminService from '@/features/admin/services';

const emptyForm = { authorName: '', biography: '' };

const AdminAuthorManagementPage = () => {
  const [authors, setAuthors] = useState<any[]>([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchAuthors = async () => {
    try {
      setLoading(true);
      const response = await adminService.getAuthors(0, 100);
      setAuthors(response.data.content || []);
    } catch {
      setError('Không thể tải tác giả.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAuthors();
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    try {
      if (editingId) await adminService.updateAuthor(editingId, form);
      else await adminService.addAuthor(form);
      setForm(emptyForm);
      setEditingId(null);
      fetchAuthors();
    } catch {
      setError('Không thể lưu tác giả.');
    }
  };

  const edit = (author: any) => {
    setEditingId(author.id);
    setForm({ authorName: author.authorName || author.name || '', biography: author.biography || '' });
  };

  const remove = async (id: number) => {
    if (!window.confirm('Xóa tác giả này?')) return;
    await adminService.deleteAuthor(id);
    fetchAuthors();
  };

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading eyebrow="Quản trị" title="Quản lý tác giả" description="Tạo, cập nhật, xóa và tra cứu tác giả theo phân hệ /admin/authors." />
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Tổng tác giả" value={authors.length} icon="user" />
        <StatCard label="Đang hiển thị" value={authors.length} icon="book" tone="success" />
        <StatCard label="Trạng thái" value={loading ? 'Đang tải' : 'Sẵn sàng'} icon="chart" />
      </div>
      {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      <form onSubmit={submit} className="mt-6 grid gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4 md:grid-cols-[1fr_1.4fr_auto]">
        <Field label="Tên tac gia" value={form.authorName} onChange={(e) => setForm((current) => ({ ...current, authorName: e.target.value }))} required />
        <Field label="Tieu su" value={form.biography} onChange={(e) => setForm((current) => ({ ...current, biography: e.target.value }))} />
        <div className="flex items-end gap-2">
          <PrimaryButton type="submit"><Icon name="plus" /> {editingId ? 'Cập nhật' : 'Thêm'}</PrimaryButton>
          {editingId ? <SecondaryButton onClick={() => { setEditingId(null); setForm(emptyForm); }}>Hủy</SecondaryButton> : null}
        </div>
      </form>
      <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {authors.map((author) => (
          <Panel key={author.id} className="p-5">
            <div className="flex items-start gap-4">
              <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-lg bg-primary/5 text-primary"><Icon name="user" /></div>
              <div className="min-w-0 flex-1">
                <h3 className="text-lg font-bold text-primary">{author.authorName || author.name}</h3>
                <p className="mt-3 line-clamp-3 text-sm leading-6 text-on-surface-variant">{author.biography || 'Chưa có tiểu sử.'}</p>
              </div>
              <div className="flex gap-2"><IconButton onClick={() => edit(author)}><Icon name="edit" /></IconButton><IconButton onClick={() => remove(author.id)}><Icon name="trash" /></IconButton></div>
            </div>
          </Panel>
        ))}
      </div>
    </div>
  );
};

export default AdminAuthorManagementPage;
