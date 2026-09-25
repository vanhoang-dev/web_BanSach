import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

import { BookCard, Container, EmptyState, Icon, IconButton, Panel, SecondaryButton, SectionHeading } from '@/components/ui/staticUi';
import bookService, { Book, BookSort, Category } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';
import { notify } from '@/features/notifications/NotificationProvider';

type SortOption = 'newest' | 'price-asc' | 'price-desc';

const sortConfig: Record<SortOption, BookSort> = {
  newest: { sortBy: 'createdAt', sortDirection: 'desc' },
  'price-asc': { sortBy: 'price', sortDirection: 'asc' },
  'price-desc': { sortBy: 'price', sortDirection: 'desc' },
};

const CatalogPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [books, setBooks] = useState<Book[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterOpen, setFilterOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(Number(searchParams.get('category')) || undefined);
  const [sortOption, setSortOption] = useState<SortOption>((searchParams.get('sort') as SortOption) || 'newest');
  const [page, setPage] = useState(Math.max(0, Number(searchParams.get('page') || 1) - 1));
  const [totalPages, setTotalPages] = useState(0);
  const keyword = searchParams.get('keyword') || undefined;
  const authorId = Number(searchParams.get('authorId') || 0) || undefined;
  const authorName = searchParams.get('authorName') || undefined;

  const currentSort = useMemo(() => sortConfig[sortOption], [sortOption]);

  useEffect(() => {
    bookService.getCategories().then(setCategories).catch(() => setCategories([]));
  }, []);

  useEffect(() => {
    setSelectedCategory(Number(searchParams.get('category')) || undefined);
    const nextSort = searchParams.get('sort') as SortOption | null;
    setSortOption(nextSort && nextSort in sortConfig ? nextSort : 'newest');
    setPage(Math.max(0, Number(searchParams.get('page') || 1) - 1));
  }, [searchParams]);

  useEffect(() => {
    let active = true;
    setLoading(true);
    bookService.getBooks(page, 12, authorId ? undefined : keyword, selectedCategory, currentSort, authorId)
      .then((response) => {
        if (!active) return;
        setBooks(response?.data?.content || []);
        setTotalPages(response?.data?.totalPages || 0);
      })
      .catch(() => {
        if (active) setBooks([]);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [authorId, currentSort, keyword, page, selectedCategory]);

  const updateQuery = (updates: Record<string, string | undefined>) => {
    const next = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (!value) next.delete(key);
      else next.set(key, value);
    });
    setSearchParams(next);
  };

  const selectCategory = (categoryId?: number) => {
    updateQuery({ category: categoryId ? String(categoryId) : undefined, page: undefined });
    setFilterOpen(false);
  };

  const pageTitle = authorName
    ? `Sách của tác giả "${authorName}"`
    : keyword
      ? `Kết quả cho "${keyword}"`
      : 'Tất cả sách';

  const addToCart = async (bookId?: number) => {
    if (!bookId) return;
    try {
      await cartService.addToCart(bookId, 1);
      notify.success('Đã thêm sách vào giỏ hàng');
    } catch {
      notify.error('Không thể thêm sách vào giỏ hàng');
    }
  };

  return (
    <Container className="py-10">
      <SectionHeading
        eyebrow="Cửa hàng"
        title={pageTitle}
        description="Duyệt sách theo danh mục, tìm kiếm theo từ khóa và thêm nhanh vào giỏ hàng."
        action={<SecondaryButton className="lg:hidden" onClick={() => setFilterOpen(true)} aria-expanded={filterOpen} aria-controls="catalog-filters"><Icon name="search" /> Bộ lọc</SecondaryButton>}
      />

      <div className="grid min-w-0 gap-6 lg:grid-cols-[260px_minmax(0,1fr)] xl:grid-cols-[280px_minmax(0,1fr)]">
        {filterOpen ? <button type="button" className="fixed inset-0 z-40 bg-primary/45 lg:hidden" aria-label="Đóng bộ lọc" onClick={() => setFilterOpen(false)} /> : null}
        <Panel id="catalog-filters" className={`fixed inset-x-4 bottom-4 top-24 z-50 h-fit max-h-[calc(100vh-7rem)] overflow-y-auto p-5 lg:sticky lg:inset-auto lg:z-auto lg:block lg:max-h-none lg:overflow-visible ${filterOpen ? 'block' : 'hidden'}`}>
          <div className="flex items-center justify-between gap-4">
          <h2 className="text-base font-bold text-primary">Bộ lọc</h2>
            <IconButton className="lg:hidden" aria-label="Đóng bộ lọc" onClick={() => setFilterOpen(false)}><Icon name="x" /></IconButton>
          </div>
          <div className="mt-4 space-y-2">
            <button
              onClick={() => selectCategory()}
              className={`w-full rounded-lg px-3 py-3 text-left text-sm font-bold transition ${selectedCategory ? 'text-on-surface-variant hover:bg-surface-container-low' : 'bg-primary text-on-primary'}`}
            >
              Tất cả danh mục
            </button>
            {categories.map((category) => (
              <button
                key={category.id}
                onClick={() => selectCategory(category.id)}
                className={`w-full rounded-lg px-3 py-3 text-left text-sm font-bold transition ${selectedCategory === category.id ? 'bg-primary text-on-primary' : 'text-on-surface-variant hover:bg-surface-container-low'}`}
              >
                {category.name}
              </button>
            ))}
          </div>
        </Panel>

        <div className="min-w-0">
          <div className="mb-4 grid min-w-0 gap-3 rounded-lg border border-outline-variant bg-surface-container-low px-4 py-3 sm:flex sm:flex-wrap sm:items-center sm:justify-between">
            <p className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${books.length} sách trong trang này`}</p>
            <label className="flex min-w-0 w-full items-center gap-2 text-sm font-semibold text-on-surface sm:w-auto">
              <span className="sr-only">Sắp xếp sách</span>
              <select
                value={sortOption}
                onChange={(event) => updateQuery({ sort: event.target.value === 'newest' ? undefined : event.target.value, page: undefined })}
                className="min-w-0 w-full rounded-md border-border-strong bg-surface px-3 py-2 text-sm text-on-surface outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/15 sm:w-auto"
              >
                <option value="newest">Sắp xếp: Mới nhất</option>
                <option value="price-asc">Giá tăng dần</option>
                <option value="price-desc">Giá giảm dần</option>
              </select>
            </label>
          </div>

          {loading ? (
            <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
              {Array.from({ length: 6 }).map((_, index) => <div key={index} className="h-96 animate-pulse rounded-xl bg-surface-container" />)}
            </div>
          ) : books.length ? (
            <>
              <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
                {books.map((book) => (
                  <BookCard
                    key={book.id}
                    id={book.id}
                    title={book.title}
                    author={book.author?.name}
                    category={book.category?.name}
                    price={book.price}
                    cover={book.cover}
                    discount={book.discount}
                    onAdd={() => addToCart(book.id)}
                  />
                ))}
              </div>

              <div className="mt-8 flex items-center justify-center gap-3">
                <SecondaryButton disabled={page === 0} onClick={() => updateQuery({ page: page > 1 ? String(page) : undefined })}>Trước</SecondaryButton>
                <span className="text-sm font-bold text-on-surface-variant">Trang {page + 1} / {Math.max(totalPages, 1)}</span>
                <SecondaryButton disabled={page >= totalPages - 1} onClick={() => updateQuery({ page: String(page + 2) })}>Tiếp</SecondaryButton>
              </div>
            </>
          ) : (
            <EmptyState title="Không tìm thấy sách" description="Thử đổi từ khóa tìm kiếm hoặc chọn danh mục khác." />
          )}
        </div>
      </div>
    </Container>
  );
};

export default CatalogPage;
