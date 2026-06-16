import { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { Container, EmptyState, Icon, Panel, PrimaryButton, SectionHeading } from '@/components/ui/staticUi';
import categoryService, { CategoryItem } from '@/features/categories/services/categoryService';

const CategoriesPage = () => {
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchCategories = async (search = '') => {
    try {
      setLoading(true);
      setError('');
      const response = search
        ? await categoryService.searchCategories(search, 0, 24)
        : await categoryService.getCategories(0, 24);
      setCategories(response.data.content || []);
    } catch {
      setError('Không thể tải danh mục từ máy chủ.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleSearch = (event: FormEvent) => {
    event.preventDefault();
    fetchCategories(keyword.trim());
  };

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Danh mục" title="Khám phá các nhóm sách" description="Chọn một chủ đề để đi nhanh tới danh mục sách phù hợp với nhu cầu đọc của bạn." />
      <Panel className="mb-6 p-4">
        <form className="flex flex-col gap-3 sm:flex-row" onSubmit={handleSearch}>
          <input value={keyword} onChange={(event) => setKeyword(event.target.value)} className="h-11 flex-1 rounded-lg border border-outline-variant bg-surface px-4 text-sm outline-none focus:border-primary" placeholder="Tìm danh mục..." />
          <PrimaryButton type="submit"><Icon name="search" /> Tìm kiếm</PrimaryButton>
        </form>
      </Panel>
      {error ? <div className="mb-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      {loading ? (
        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">{Array.from({ length: 6 }).map((_, index) => <div key={index} className="h-64 animate-pulse rounded-xl bg-surface-container" />)}</div>
      ) : categories.length === 0 ? (
        <EmptyState title="Chưa có danh mục" description="Thử từ khóa khác hoặc quay lại danh mục sách." />
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
          {categories.map((category) => (
            <Link key={category.id} to={`/catalog?category=${category.id}`}>
              <Panel className="h-full overflow-hidden transition hover:-translate-y-1 hover:shadow-md">
                <div className="bg-primary p-6 text-on-primary">
                  <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-secondary-container text-on-secondary-container"><Icon name="category" /></div>
                  <h3 className="mt-6 text-2xl font-bold">{category.name}</h3>
                  <p className="mt-2 text-on-primary-container">{category.isActive === false ? 'Đang tạm ẩn' : 'Đang hiển thị'}</p>
                </div>
                <div className="p-5">
                  <p className="text-sm leading-6 text-on-surface-variant">{category.description || 'Danh mục sách đang được cập nhật mô tả.'}</p>
                  <span className="mt-4 inline-flex items-center gap-2 text-sm font-bold text-secondary">Xem sách <Icon name="arrow" className="h-4 w-4" /></span>
                </div>
              </Panel>
            </Link>
          ))}
        </div>
      )}
    </Container>
  );
};

export default CategoriesPage;
