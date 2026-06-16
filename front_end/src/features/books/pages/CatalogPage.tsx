import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

import { BookCard, Container, EmptyState, Icon, Panel, SecondaryButton, SectionHeading } from '@/components/ui/staticUi';
import bookService from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';

const CatalogPage = () => {
  const [searchParams] = useSearchParams();
  const [books, setBooks] = useState<any[]>([]);
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(Number(searchParams.get('category')) || undefined);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    bookService.getCategories().then(setCategories).catch(() => setCategories([]));
  }, []);

  useEffect(() => {
    const keyword = searchParams.get('keyword') || undefined;
    setLoading(true);
    bookService.getBooks(page, 12, keyword, selectedCategory)
      .then((response) => {
        setBooks(response?.data?.content || []);
        setTotalPages(response?.data?.totalPages || 0);
      })
      .catch(() => setBooks([]))
      .finally(() => setLoading(false));
  }, [page, selectedCategory, searchParams]);

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
        title={searchParams.get('keyword') ? `Kết quả cho "${searchParams.get('keyword')}"` : 'Tất cả sách'}
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
          <div className="mb-4 flex items-center justify-between rounded-lg border border-outline-variant bg-surface-container-low px-4 py-3">
            <p className="text-sm font-semibold text-on-surface-variant">{loading ? 'Đang tải...' : `${books.length} sách trong trang này`}</p>
            <select className="rounded-lg border-outline-variant bg-surface text-sm">
              <option>Sắp xếp: Mới nhất</option>
              <option>Giá tăng dần</option>
              <option>Giá giảm dần</option>
            </select>
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
