import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

import { BookCard, Container, EmptyState, Icon, Panel, SecondaryButton, SectionHeading } from '@/components/ui/staticUi';
import bookService, { BookSort } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';

type SortOption = 'newest' | 'price-asc' | 'price-desc';

const sortConfig: Record<SortOption, BookSort> = {
  newest: { sortBy: 'createdAt', sortDirection: 'desc' },
  'price-asc': { sortBy: 'price', sortDirection: 'asc' },
  'price-desc': { sortBy: 'price', sortDirection: 'desc' },
};

const CatalogPage = () => {
  const [searchParams] = useSearchParams();
  const [books, setBooks] = useState<any[]>([]);
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(Number(searchParams.get('category')) || undefined);
  const [sortOption, setSortOption] = useState<SortOption>('newest');
  const [page, setPage] = useState(0);
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
    setPage(0);
  }, [searchParams]);

  useEffect(() => {
    setLoading(true);
    bookService.getBooks(page, 12, authorId ? undefined : keyword, selectedCategory, currentSort, authorId)
      .then((response) => {
        setBooks(response?.data?.content || []);
        setTotalPages(response?.data?.totalPages || 0);
      })
      .catch(() => setBooks([]))
      .finally(() => setLoading(false));
  }, [authorId, currentSort, keyword, page, selectedCategory]);

  const pageTitle = authorName
    ? `Sách của tác giả "${authorName}"`
    : keyword
      ? `Kết quả cho "${keyword}"`
      : 'Tất cả sách';

  const addToCart = async (bookId?: number) => {
    if (!bookId) return;
    try {
      await cartService.addToCart(bookId, 1);
      window.alert('Đã thêm sách vào giỏ hàng');
    } catch {
      window.alert('Không thể thêm sách vào giỏ hàng');
    }
  };

  return (
    <Container className="py-10">
      <SectionHeading
        eyebrow="Cửa hàng"
        title={pageTitle}
        description="Duyệt sách theo danh mục, tìm kiếm theo từ khóa và thêm nhanh vào giỏ hàng."
        action={<SecondaryButton><Icon name="search" /> Lọc nâng cao</SecondaryButton>}
      />

      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        <Panel className="h-fit p-5 lg:sticky lg:top-28">
          <h2 className="text-base font-bold text-primary">Bộ lọc</h2>
          <div className="mt-4 space-y-2">
            <button
              onClick={() => { setSelectedCategory(undefined); setPage(0); }}
              className={`w-full rounded-lg px-3 py-3 text-left text-sm font-bold transition ${selectedCategory ? 'text-on-surface-variant hover:bg-surface-container-low' : 'bg-primary text-on-primary'}`}
            >
              Tất cả danh mục
            </button>
            {categories.map((category) => (
              <button
                key={category.id}
                onClick={() => { setSelectedCategory(category.id); setPage(0); }}
                className={`w-full rounded-lg px-3 py-3 text-left text-sm font-bold transition ${selectedCategory === category.id ? 'bg-primary text-on-primary' : 'text-on-surface-variant hover:bg-surface-container-low'}`}
              >
                {category.name}
              </button>
            ))}
          </div>
        </Panel>

        <div>
          <div className="mb-4 flex flex-wrap items-center justify-between gap-3 rounded-lg border border-outline-variant bg-surface-container-low px-4 py-3">
            <p className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${books.length} sách trong trang này`}</p>
            <label className="flex items-center gap-2 text-sm font-semibold text-on-surface">
              <span className="sr-only">Sắp xếp sách</span>
              <select
                value={sortOption}
                onChange={(event) => { setSortOption(event.target.value as SortOption); setPage(0); }}
                className="rounded-lg border-outline-variant bg-surface px-3 py-2 text-sm text-on-surface shadow-sm outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
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
                <SecondaryButton disabled={page === 0} onClick={() => setPage((value) => Math.max(0, value - 1))}>Trước</SecondaryButton>
                <span className="text-sm font-bold text-on-surface-variant">Trang {page + 1} / {Math.max(totalPages, 1)}</span>
                <SecondaryButton disabled={page >= totalPages - 1} onClick={() => setPage((value) => value + 1)}>Tiếp</SecondaryButton>
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
