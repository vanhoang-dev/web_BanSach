import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { BookCard, Container, EmptyState, Icon, IconButton, PrimaryButton, SectionHeading } from '@/components/ui/staticUi';
import cartService from '@/features/cart/services/cartService';
import wishlistService from '@/features/wishlist/services/wishlistService';

const WishlistPage = () => {
  const [wishlist, setWishlist] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchWishlist = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await wishlistService.getWishlist();
      setWishlist(Array.isArray(data) ? data : data?.items || data?.content || []);
    } catch {
      setError('Không thể tải danh sách yêu thích.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWishlist();
  }, []);

  const getBook = (item: any) => item.book || item;

  const handleAddToCart = async (bookId: number) => {
    try {
      await cartService.addToCart(bookId, 1);
      alert('Đã thêm vào giỏ hàng');
    } catch {
      alert('Không thể thêm vào giỏ hàng');
    }
  };

  const handleRemove = async (bookId: number) => {
    try {
      await wishlistService.removeFromWishlist(bookId);
      setWishlist((items) => items.filter((item) => (getBook(item).id || getBook(item).bookId) !== bookId));
    } catch {
      alert('Không thể xóa khỏi danh sách yêu thích');
    }
  };

  const handleClear = async () => {
    try {
      await wishlistService.clearWishlist();
      setWishlist([]);
    } catch {
      alert('Không thể xóa toàn bộ danh sách yêu thích');
    }
  };

  return (
    <Container className="py-10">
      <SectionHeading
        eyebrow="Tài khoản"
        title="Danh sách yêu thích"
        description="Lưu những cuốn sách bạn quan tâm để mua sau hoặc thêm nhanh vào giỏ hàng."
        action={wishlist.length ? <PrimaryButton onClick={handleClear}><Icon name="trash" /> Xóa tất cả</PrimaryButton> : null}
      />
      {error ? <div className="mb-5 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      {loading ? (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">{Array.from({ length: 4 }).map((_, i) => <div key={i} className="h-96 animate-pulse rounded-xl bg-surface-container" />)}</div>
      ) : wishlist.length === 0 ? (
        <EmptyState title="Danh sách yêu thích đang trống" description="Khám phá danh mục sách và lưu lại những cuốn sách bạn muốn đọc." action={<Link to="/catalog" className="font-bold text-secondary hover:underline">Khám phá sách</Link>} />
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {wishlist.map((item: any) => {
            const book = getBook(item);
            const bookId = book.id || book.bookId;
            return (
              <div key={bookId} className="relative">
                <div className="absolute right-3 top-3 z-10">
                  <IconButton onClick={() => handleRemove(bookId)} aria-label="Xóa khỏi danh sách yêu thích"><Icon name="trash" /></IconButton>
                </div>
                <BookCard
                  id={bookId}
                  title={book.title || book.bookTitle}
                  author={book.author?.name || book.authorName}
                  category={book.category?.name || book.categoryName}
                  price={book.price || book.bookPrice}
                  cover={book.cover || book.coverImage || book.bookCoverImage}
                  onAdd={() => handleAddToCart(bookId)}
                />
              </div>
            );
          })}
        </div>
      )}
    </Container>
  );
};

export default WishlistPage;
