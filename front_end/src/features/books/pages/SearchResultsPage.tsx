import { FormEvent, useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';

import { BookCard, Container, EmptyState, Icon, LinkButton, Panel, SectionHeading } from '@/components/ui/staticUi';
import bookService, { Book } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';
import { notify } from '@/features/notifications/NotificationProvider';

const SearchResultsPage = () => {
  const [params, setParams] = useSearchParams();
  const [keyword, setKeyword] = useState(params.get('keyword') || params.get('q') || '');
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchBooks = async (search: string) => {
    if (!search.trim()) {
      setBooks([]);
      return;
    }
    try {
      setLoading(true);
      setError('');
      const response = await bookService.searchBooks(search.trim(), 0, 18);
      setBooks(response.data.content || []);
    } catch {
      setError('Không thể tìm kiếm sách từ máy chủ.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const q = params.get('keyword') || params.get('q') || '';
    setKeyword(q);
    fetchBooks(q);
  }, [params]);

  const handleSearch = (event: FormEvent) => {
    event.preventDefault();
    setParams(keyword ? { keyword } : {});
  };

  const handleAddToCart = async (bookId?: number) => {
    if (!bookId) return;
    try {
      await cartService.addToCart(bookId, 1);
      notify.success('Đã thêm vào giỏ hàng');
    } catch {
      notify.error('Không thể thêm vào giỏ hàng');
    }
  };

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Tìm kiếm" title="Kết quả tìm kiếm" description="Tìm sách theo tên sách, tác giả hoặc từ khóa hệ thống đang hỗ trợ." />
      <Panel className="mb-6 p-4">
        <form className="relative" onSubmit={handleSearch}>
          <input value={keyword} onChange={(event) => setKeyword(event.target.value)} className="h-12 w-full rounded-md border border-border-strong bg-surface pl-4 pr-14 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/15" placeholder="Tìm kiếm sách, tác giả..." />
          <button type="submit" aria-label="Tìm kiếm" className="absolute right-1 top-1 flex h-10 w-10 items-center justify-center rounded-md bg-primary text-on-primary hover:bg-primary-container"><Icon name="search" /></button>
        </form>
      </Panel>

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        <div>
          {error ? <div className="mb-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          {loading ? (
            <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">{Array.from({ length: 6 }).map((_, index) => <div key={index} className="h-96 animate-pulse rounded-xl bg-surface-container" />)}</div>
          ) : books.length === 0 ? (
            <EmptyState title="Chưa có kết quả" description="Nhập từ khóa hoặc thử một cụm từ ngắn hơn." />
          ) : (
            <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
              {books.map((book) => (
                <BookCard key={book.id} id={book.id} title={book.title} author={book.author?.name} category={book.category?.name} price={book.price} cover={book.cover} discount={book.discount} onAdd={() => handleAddToCart(book.id)} />
              ))}
            </div>
          )}
        </div>

        <Panel className="h-fit p-5">
          <h3 className="text-xl font-bold text-primary">Gợi ý khám phá</h3>
          <p className="mt-2 text-sm leading-6 text-on-surface-variant">Nếu chưa tìm thấy sách cần mua, hãy xem danh mục sách hoặc đi theo từng danh mục.</p>
          <div className="mt-5 space-y-2">
            {['Kinh doanh', 'Kỹ năng', 'Văn học'].map((item) => (
              <Link key={item} to={`/catalog?keyword=${encodeURIComponent(item)}`} className="flex w-full items-center justify-between rounded-lg bg-surface-container-low px-4 py-3 text-sm font-bold text-on-surface">
                {item}
                <Icon name="arrow" className="h-4 w-4" />
              </Link>
            ))}
          </div>
          <LinkButton to="/catalog" className="mt-5 w-full">Xem danh mục sách</LinkButton>
        </Panel>
      </div>
    </Container>
  );
};

export default SearchResultsPage;
