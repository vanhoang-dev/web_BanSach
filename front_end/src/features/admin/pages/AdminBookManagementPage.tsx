import { FormEvent, useEffect, useState } from 'react';

import { AdminTable, AdminToolbar, Field, formatVnd, Icon, IconButton, PrimaryButton, SecondaryButton, SectionHeading, StatCard, StatusBadge } from '@/components/ui/staticUi';
import AdminPagination from '@/features/admin/components/AdminPagination';
import authorAdminService from '@/features/admin/services/authorAdminService';
import bookAdminService from '@/features/admin/services/bookAdminService';
import categoryAdminService from '@/features/admin/services/categoryAdminService';

const emptyForm: any = {
  title: '',
  isbn: '',
  publisher: '',
  publicationYear: new Date().getFullYear(),
  price: 0,
  description: '',
  authorId: '',
  categoryId: '',
  discountId: '',
  coverImageFile: null,
};
const pageSize = 10;

const AdminBookManagementPage = () => {
  const [books, setBooks] = useState<any[]>([]);
  const [authors, setAuthors] = useState<any[]>([]);
  const [categories, setCategories] = useState<any[]>([]);
  const [form, setForm] = useState<any>(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });

  const fetchData = async () => {
    try {
      setLoading(true);
      const [bookPage, authorPage, categoryPage] = await Promise.all([
        bookAdminService.getAll(page, pageSize),
        authorAdminService.getAll(0, 100).catch(() => ({ data: { content: [] } })),
        categoryAdminService.getAll(0, 100).catch(() => ({ data: { content: [] } })),
      ]);
      setBooks(bookPage.data.content || []);
      setPageInfo({ totalElements: bookPage.data.totalElements, totalPages: bookPage.data.totalPages });
      setAuthors(authorPage.data.content || []);
      setCategories(categoryPage.data.content || []);
    } catch {
      setError('Không thể tải dữ liệu sách.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [page]);

  const update = (key: string, value: any) => setForm((current: any) => ({ ...current, [key]: value }));

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    try {
      if (editingId) await bookAdminService.update(editingId, form);
      else await bookAdminService.create(form);
      setForm(emptyForm);
      setEditingId(null);
      setShowForm(false);
      fetchData();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Không thể lưu sách.');
    }
  };

  const edit = (book: any) => {
    setEditingId(book.id);
    setShowForm(true);
    setForm({
      title: book.title || '',
      isbn: book.isbn || '',
      publisher: book.publisher || '',
      publicationYear: book.publicationYear || new Date().getFullYear(),
      price: book.price || 0,
      description: book.description || '',
      authorId: book.authorId || book.author?.id || '',
      categoryId: book.categoryId || book.category?.id || '',
      discountId: book.discountId || '',
      coverImageFile: null,
    });
  };

  const remove = async (id: number) => {
    if (!window.confirm('Xóa sách này?')) return;
    await bookAdminService.remove(id);
    fetchData();
  };

  const visibleBooks = filter ? books.filter((book) => `${book.title || ''} ${book.isbn || ''} ${book.authorName || ''}`.toLowerCase().includes(filter.toLowerCase())) : books;

  return (
    <div className="mx-auto max-w-7xl">
      <SectionHeading
        eyebrow="Quản trị"
        title="Quản lý sách"
        description="Thêm, sửa, xóa sách, ảnh bìa, giá, tác giả và danh mục theo /admin/books."
        action={<PrimaryButton onClick={() => { setShowForm(true); setEditingId(null); setForm(emptyForm); }}><Icon name="plus" /> Thêm sách</PrimaryButton>}
      />

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Tổng đầu sách" value={books.length} icon="book" />
        <StatCard label="Đang bán" value={books.filter((item) => Number(item.price || 0) > 0).length} icon="chart" tone="success" />
        <StatCard label="Cần bổ sung dữ liệu" value={books.filter((item) => !item.authorName || !item.categoryName).length} icon="inventory" tone="warning" />
      </div>

      {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}

      {showForm ? (
        <form onSubmit={submit} className="mt-6 grid gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4 md:grid-cols-4">
          <Field className="md:col-span-2" label="Tên sách" value={form.title} onChange={(e) => update('title', e.target.value)} required />
          <Field label="ISBN" value={form.isbn} onChange={(e) => update('isbn', e.target.value)} required />
          <Field label="Nhà xuất bản" value={form.publisher} onChange={(e) => update('publisher', e.target.value)} />
          <Field label="Năm XB" type="number" value={form.publicationYear} onChange={(e) => update('publicationYear', Number(e.target.value))} />
          <Field label="Giá" type="number" value={form.price} onChange={(e) => update('price', Number(e.target.value))} required />
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-on-surface">Tác giả</span>
            <select value={form.authorId} onChange={(e) => update('authorId', e.target.value)} className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm" required>
              <option value="">Chọn tác giả</option>
              {authors.map((author) => <option key={author.id} value={author.id}>{author.authorName || author.name}</option>)}
            </select>
          </label>
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-on-surface">Danh mục</span>
            <select value={form.categoryId} onChange={(e) => update('categoryId', e.target.value)} className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm" required>
              <option value="">Chọn danh mục</option>
              {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
            </select>
          </label>
          <Field className="md:col-span-3" label="Mô tả" textarea value={form.description} onChange={(e) => update('description', e.target.value)} />
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-on-surface">Ảnh bìa</span>
            <input type="file" accept="image/*" onChange={(e) => update('coverImageFile', e.target.files?.[0] || null)} className="block w-full rounded-lg border border-outline-variant bg-surface px-3 py-2 text-sm" />
          </label>
          <div className="flex gap-2 md:col-span-4">
            <PrimaryButton type="submit">{editingId ? 'Cập nhật sách' : 'Thêm sách'}</PrimaryButton>
            <SecondaryButton onClick={() => { setShowForm(false); setEditingId(null); setForm(emptyForm); }}>Hủy</SecondaryButton>
          </div>
        </form>
      ) : null}

      <div className="mt-6">
        <AdminToolbar>
          <input value={filter} onChange={(e) => { setFilter(e.target.value); setPage(0); }} className="h-11 w-full rounded-lg border border-outline-variant bg-surface px-4 text-sm outline-none focus:border-primary md:max-w-sm" placeholder="Tìm tên sách, ISBN, tác giả..." />
          <span className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${pageInfo.totalElements} sách`}</span>
        </AdminToolbar>
        <AdminTable minWidth="900px">
          <thead className="border-b border-outline-variant bg-surface-container-high text-xs uppercase text-on-surface-variant"><tr>{['Tên sách', 'Tác giả', 'Danh mục', 'Giá', 'Trạng thái', ''].map((head) => <th key={head} className="px-5 py-4">{head}</th>)}</tr></thead>
          <tbody className="divide-y divide-outline-variant">
            {visibleBooks.map((book) => (
              <tr key={book.id} className="hover:bg-surface-container-low">
                <td className="px-5 py-4 font-bold text-primary">{book.title}</td>
                <td className="px-5 py-4 text-sm text-on-surface-variant">{book.authorName || book.author?.name || '-'}</td>
                <td className="px-5 py-4 text-sm text-on-surface-variant">{book.categoryName || book.category?.name || '-'}</td>
                <td className="px-5 py-4 text-sm font-bold text-primary">{formatVnd(Number(book.price || 0))}</td>
                <td className="px-5 py-4"><StatusBadge status="ACTIVE">Đang bán</StatusBadge></td>
                <td className="px-5 py-4"><div className="flex justify-end gap-2"><IconButton onClick={() => edit(book)}><Icon name="edit" /></IconButton><IconButton onClick={() => remove(book.id)}><Icon name="trash" /></IconButton></div></td>
              </tr>
            ))}
          </tbody>
        </AdminTable>
        <AdminPagination page={page} pageSize={pageSize} totalElements={pageInfo.totalElements} totalPages={pageInfo.totalPages} onPageChange={setPage} />
      </div>
    </div>
  );
};

export default AdminBookManagementPage;
