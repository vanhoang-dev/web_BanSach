import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

import bookCoverPlaceholder from '@/assets/icons/book-cover-placeholder.svg';
import { AccentButton, Container, Field, formatVnd, Icon, IconButton, Panel, PrimaryButton, SecondaryButton, StatusBadge } from '@/components/ui/staticUi';
import bookService, { Book } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';
import inventoryService from '@/features/inventory/services/inventoryService';
import reviewService, { Review } from '@/features/reviews/services/reviewService';
import wishlistService from '@/features/wishlist/services/wishlistService';
import { useAuth } from '@/hooks/useAuth';
import { notify } from '@/features/notifications/NotificationProvider';

const BookDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const bookId = Number(id);
  const [book, setBook] = useState<Book | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [reviewStats, setReviewStats] = useState({ averageRating: 0, reviewCount: 0 });
  const [myReview, setMyReview] = useState<Review | null>(null);
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [isInWishlist, setIsInWishlist] = useState(false);
  const [adding, setAdding] = useState(false);
  const [reviewSaving, setReviewSaving] = useState(false);

  const fetchBook = async () => {
    try {
      setLoading(true);
      setError('');
      const [bookData, reviewPage, stats] = await Promise.all([
        bookService.getBookById(bookId),
        reviewService.getReviewsByBook(bookId, 0, 6).catch(() => ({ data: { content: [] } })),
        reviewService.getReviewStats(bookId).catch(() => ({ averageRating: 0, reviewCount: 0 })),
      ]);
      setBook(bookData);
      setReviews(reviewPage.data.content || []);
      setReviewStats(stats);

      inventoryService.getBookInventory(bookId).then((inventory) => {
        const stock = inventory?.availableQuantity ?? inventory?.quantity ?? inventory?.stock;
        if (stock !== undefined) setBook((current) => current ? { ...current, stock } : current);
      }).catch(() => undefined);

      if (isAuthenticated) {
        const [inWishlist, currentReview] = await Promise.all([
          wishlistService.isInWishlist(bookId).catch(() => false),
          reviewService.getMyReview(bookId).catch(() => null),
        ]);
        setIsInWishlist(inWishlist);
        setMyReview(currentReview);
        if (currentReview) setReviewForm({ rating: currentReview.rating, comment: currentReview.comment || '' });
      }
    } catch {
      setError('Không thể tải chi tiết sách.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (bookId) fetchBook();
  }, [bookId, isAuthenticated]);

  const handleAddToCart = async () => {
    try {
      setAdding(true);
      await cartService.addToCart(bookId, quantity);
      notify.success('Đã thêm vào giỏ hàng');
      setQuantity(1);
    } catch {
      notify.error('Không thể thêm vào giỏ hàng');
    } finally {
      setAdding(false);
    }
  };

  const handleBuyNow = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    navigate(`/checkout?bookId=${bookId}&quantity=${quantity}`);
  };

  const handleToggleWishlist = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    try {
      if (isInWishlist) await wishlistService.removeFromWishlist(bookId);
      else await wishlistService.addToWishlist(bookId);
      setIsInWishlist(!isInWishlist);
    } catch {
      notify.error('Không thể cập nhật danh sách yêu thích');
    }
  };

  const handleSubmitReview = async (event: FormEvent) => {
    event.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    try {
      setReviewSaving(true);
      if (myReview?.id) {
        await reviewService.updateReview(myReview.id, { bookId, rating: reviewForm.rating, comment: reviewForm.comment });
      } else {
        await reviewService.addReview({ bookId, rating: reviewForm.rating, comment: reviewForm.comment });
      }
      await fetchBook();
      notify.success('Đã lưu đánh giá');
    } catch {
      notify.error('Không thể lưu đánh giá');
    } finally {
      setReviewSaving(false);
    }
  };

  const handleDeleteReview = async () => {
    if (!myReview?.id) return;
    try {
      await reviewService.deleteReview(myReview.id);
      setMyReview(null);
      setReviewForm({ rating: 5, comment: '' });
      await fetchBook();
    } catch {
      notify.error('Không thể xóa đánh giá');
    }
  };

  if (loading) {
    return (
      <Container className="py-16">
        <div className="grid min-w-0 gap-8 xl:grid-cols-[420px_minmax(0,1fr)]">
          <div className="h-[560px] animate-pulse rounded-xl bg-surface-container" />
          <div className="space-y-4">
            <div className="h-12 w-3/4 animate-pulse rounded bg-surface-container" />
            <div className="h-6 w-1/2 animate-pulse rounded bg-surface-container" />
            <div className="h-40 animate-pulse rounded bg-surface-container" />
          </div>
        </div>
      </Container>
    );
  }

  if (error || !book) {
    return (
      <Container className="py-16">
        <Panel className="p-10 text-center">
          <p className="mb-4 font-semibold text-error">{error || 'Không tìm thấy sách.'}</p>
          <Link to="/catalog" className="font-bold text-primary hover:underline">Quay lại danh mục sách</Link>
        </Panel>
      </Container>
    );
  }

  return (
    <Container className="py-8 pb-28 lg:py-12 lg:pb-12">
      <nav className="mb-8 flex flex-wrap gap-2 text-sm text-on-surface-variant">
        <Link to="/" className="hover:text-secondary">Trang chủ</Link><span>/</span>
        <Link to="/catalog" className="hover:text-secondary">Sách</Link><span>/</span>
        <span className="font-semibold text-primary">{book.title}</span>
      </nav>

      <div className="grid min-w-0 gap-8 xl:grid-cols-[420px_minmax(0,1fr)]">
        <div className="overflow-hidden rounded-xl border border-outline-variant bg-surface-container-low p-6">
          <img alt={book.title} width="420" height="560" className="mx-auto aspect-[3/4] max-h-[620px] w-full rounded-md bg-white object-contain shadow-sm" src={book.cover || bookCoverPlaceholder} onError={(event) => { event.currentTarget.src = bookCoverPlaceholder; }} />
        </div>

        <div className="grid gap-6 xl:grid-cols-[1fr_340px]">
          <div>
            <div className="mb-4 flex flex-wrap gap-2">
              <StatusBadge>{book.category?.name || 'Sách'}</StatusBadge>
              {book.discount ? <StatusBadge status="PENDING">Giảm {book.discount}%</StatusBadge> : null}
            </div>
            <h1 className="font-serif text-[30px] font-bold leading-[1.25] text-on-surface sm:text-4xl">{book.title}</h1>
            <p className="mt-3 text-lg text-on-surface-variant">Tác giả: <span className="font-bold text-on-surface">{book.author?.name || 'Đang cập nhật'}</span></p>

            <div className="mt-8 grid divide-y divide-outline-variant border-y border-outline-variant sm:grid-cols-3 sm:divide-x sm:divide-y-0">
              <div className="py-4 sm:px-4 sm:first:pl-0"><p className="text-xs font-semibold uppercase tracking-wide text-text-muted">Danh mục</p><p className="mt-2 font-semibold text-primary">{book.category?.name || 'N/A'}</p></div>
              <div className="py-4 sm:px-4"><p className="text-xs font-semibold uppercase tracking-wide text-text-muted">Đánh giá</p><p className="mt-2 font-semibold text-primary">{reviewStats.averageRating ? `${reviewStats.averageRating.toFixed(1)}/5` : 'Chưa có'} ({reviewStats.reviewCount})</p></div>
              <div className="py-4 sm:px-4"><p className="text-xs font-semibold uppercase tracking-wide text-text-muted">Tồn kho</p><p className={`mt-2 font-semibold ${book.stock === 0 ? 'text-error' : 'text-success'}`}>{book.stock !== undefined ? `${book.stock} cuốn` : 'Có sẵn'}</p></div>
            </div>

            <div className="mt-8">
              <h2 className="font-serif text-2xl font-bold text-primary">Mô tả sách</h2>
              <p className="mt-4 text-base leading-8 text-on-surface-variant">{book.description || 'Chưa có mô tả cho sách này.'}</p>
            </div>
          </div>

          <Panel className="h-fit p-5 xl:sticky xl:top-28">
            <p className="text-sm font-bold uppercase text-secondary">Giá bán</p>
            <p className="mt-2 text-3xl font-bold leading-tight text-secondary sm:text-4xl">{formatVnd(book.price)}</p>
            <div className="mt-6">
              <p className="mb-2 text-sm font-semibold text-on-surface">Số lượng</p>
              <div className="flex items-center gap-2">
                <IconButton aria-label="Giảm số lượng" disabled={quantity <= 1} onClick={() => setQuantity(Math.max(1, quantity - 1))}>-</IconButton>
                <span className="flex h-11 min-w-16 items-center justify-center rounded-lg border border-outline-variant bg-surface font-bold" aria-live="polite">{quantity}</span>
                <IconButton aria-label="Tăng số lượng" disabled={book.stock !== undefined && quantity >= book.stock} onClick={() => setQuantity((current) => book.stock === undefined ? current + 1 : Math.min(book.stock, current + 1))}>+</IconButton>
              </div>
            </div>
            <div className="mt-6 grid gap-3">
              <AccentButton disabled={adding || book.stock === 0} onClick={handleAddToCart} className="w-full"><Icon name="cart" /> {adding ? 'Đang thêm...' : 'Thêm vào giỏ hàng'}</AccentButton>
              <PrimaryButton disabled={book.stock === 0} onClick={handleBuyNow} className="w-full">Mua ngay</PrimaryButton>
              <SecondaryButton onClick={handleToggleWishlist} className="w-full"><Icon name="heart" /> {isInWishlist ? 'Đã lưu' : 'Lưu sách'}</SecondaryButton>
            </div>
          </Panel>
        </div>
      </div>

      <div className="mt-10 grid gap-6 lg:grid-cols-[1fr_380px]">
        <Panel className="p-5">
          <h2 className="text-2xl font-bold text-primary">Đánh giá từ độc giả</h2>
          <div className="mt-5 space-y-4">
            {reviews.length === 0 ? <p className="text-sm text-on-surface-variant">Chưa có đánh giá nào cho sách này.</p> : reviews.map((review) => (
              <div key={review.id} className="rounded-lg border border-outline-variant bg-surface-container-low p-4">
                <div className="flex items-center justify-between gap-4">
                  <p className="font-bold text-primary">{review.userName || 'Độc giả'}</p>
                  <span className="text-sm font-bold text-secondary">{review.rating}/5</span>
                </div>
                <p className="mt-2 text-sm leading-6 text-on-surface-variant">{review.comment || 'Không có bình luận.'}</p>
              </div>
            ))}
          </div>
        </Panel>

        <Panel className="p-5">
          <h2 className="text-xl font-bold text-primary">{myReview ? 'Cập nhật đánh giá' : 'Viết đánh giá'}</h2>
          <form className="mt-4 grid gap-4" onSubmit={handleSubmitReview}>
            <label className="block">
              <span className="mb-2 block text-sm font-semibold text-on-surface">Số sao</span>
              <select value={reviewForm.rating} onChange={(event) => setReviewForm((current) => ({ ...current, rating: Number(event.target.value) }))} className="h-11 w-full rounded-lg border-outline-variant bg-surface text-sm">
                {[5, 4, 3, 2, 1].map((rating) => <option key={rating} value={rating}>{rating} sao</option>)}
              </select>
            </label>
            <Field label="Bình luận" textarea value={reviewForm.comment} onChange={(event) => setReviewForm((current) => ({ ...current, comment: event.target.value }))} placeholder="Cảm nhận của bạn về cuốn sách..." />
            <PrimaryButton type="submit" disabled={reviewSaving}>{reviewSaving ? 'Đang lưu...' : myReview ? 'Cập nhật đánh giá' : 'Gửi đánh giá'}</PrimaryButton>
            {myReview ? <SecondaryButton disabled={reviewSaving} onClick={handleDeleteReview}>Xóa đánh giá của tôi</SecondaryButton> : null}
          </form>
        </Panel>
      </div>

      <div className="fixed inset-x-0 bottom-0 z-40 border-t border-outline-variant bg-surface/95 px-4 py-3 backdrop-blur lg:hidden">
        <div className="mx-auto grid max-w-container-max grid-cols-2 gap-3">
          <AccentButton disabled={adding || book.stock === 0} onClick={handleAddToCart}><Icon name="cart" /> {adding ? 'Đang thêm...' : 'Thêm vào giỏ'}</AccentButton>
          <PrimaryButton disabled={book.stock === 0} onClick={handleBuyNow}>Mua ngay</PrimaryButton>
        </div>
      </div>
    </Container>
  );
};

export default BookDetailPage;
