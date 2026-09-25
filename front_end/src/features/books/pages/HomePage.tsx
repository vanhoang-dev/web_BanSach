import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import bookCoverPlaceholder from '@/assets/icons/book-cover-placeholder.svg';
import { BookCard, Container, EmptyState, Icon, LinkButton, Panel, Surface } from '@/components/ui/staticUi';
import authorService, { AuthorItem } from '@/features/authors/services/authorService';
import bookService, { Book, Category } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';
import { notify } from '@/features/notifications/NotificationProvider';

const heroSlides = [
  {
    src: 'https://images.unsplash.com/photo-1526243741027-444d633d7365?auto=format&fit=crop&w=1600&q=90',
    alt: 'Kệ sách chọn lọc trong một hiệu sách',
  },
  {
    src: 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=1600&q=90',
    alt: 'Không gian thư viện với những kệ sách lớn',
  },
  {
    src: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=1600&q=90',
    alt: 'Những cuốn sách được sắp xếp trong thư viện',
  },
];
const HomePage = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [authors, setAuthors] = useState<AuthorItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [heroSlide, setHeroSlide] = useState(0);
  const [heroPaused, setHeroPaused] = useState(false);

  useEffect(() => {
    let active = true;

    Promise.all([bookService.getFeaturedBooks(8), bookService.getCategories(), authorService.getAuthors(0, 3)])
      .then(([bookData, categoryData, authorData]) => {
        if (!active) return;
        setBooks(bookData);
        setCategories(categoryData);
        setAuthors(authorData.data.content || []);
      })
      .catch(() => {
        if (!active) return;
        setBooks([]);
        setCategories([]);
        setAuthors([]);
        setError('Không thể tải dữ liệu từ máy chủ. Vui lòng thử lại sau.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    const reduceMotion = typeof window.matchMedia === 'function'
      && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reduceMotion || heroPaused) return;

    const timer = window.setInterval(() => {
      setHeroSlide((current) => (current + 1) % heroSlides.length);
    }, 5500);

    return () => window.clearInterval(timer);
  }, [heroPaused]);

  const addToCart = async (bookId?: number) => {
    if (!bookId) return;
    try {
      await cartService.addToCart(bookId, 1);
      notify.success('Đã thêm sách vào giỏ hàng');
    } catch {
      notify.error('Không thể thêm vào giỏ hàng. Vui lòng đăng nhập hoặc thử lại.');
    }
  };

  const featured = books[0];

  return (
    <div>
      {loading ? <div className="h-1 overflow-hidden bg-surface-container"><div className="h-full w-1/3 animate-pulse bg-primary" /></div> : null}
      {error ? <Container className="pt-4"><div className="rounded-lg border border-secondary-container bg-secondary-container/20 px-4 py-3 text-sm font-semibold text-secondary">{error}</div></Container> : null}

      <Container className="py-8">
        <div className="grid min-w-0 grid-cols-1 gap-6 xl:grid-cols-[minmax(0,1fr)_360px]">
          <section
            className="grid min-w-0 overflow-hidden rounded-lg bg-primary text-on-primary md:min-h-[500px] md:grid-cols-[minmax(0,1.08fr)_minmax(320px,.92fr)] xl:min-h-[520px]"
            aria-roledescription="carousel"
            aria-label="Bộ sưu tập sách nổi bật"
            onMouseEnter={() => setHeroPaused(true)}
            onMouseLeave={() => setHeroPaused(false)}
            onFocusCapture={() => setHeroPaused(true)}
            onBlurCapture={() => setHeroPaused(false)}
          >
            <div className="flex min-w-0 flex-col justify-center px-6 py-9 sm:p-10 lg:p-12">
              <p className="mb-4 text-xs font-semibold uppercase tracking-[0.18em] text-secondary-container">Tủ sách chọn lọc</p>
              <h1 className="max-w-[620px] font-serif text-[30px] font-bold leading-[1.25] text-white sm:text-4xl sm:leading-[1.24] lg:text-[42px] xl:text-[44px]">Những cuốn sách đáng đọc cho hành trình của bạn</h1>
              <p className="mt-5 max-w-xl text-base leading-7 text-white/75 lg:text-lg">Khám phá sách hay theo chủ đề, mua sắm minh bạch và theo dõi đơn hàng dễ dàng tại Nhà Sách Tri Thức.</p>
              <div className="mt-7 grid grid-cols-1 gap-3 min-[430px]:grid-cols-2 sm:flex sm:flex-wrap">
                <LinkButton to="/catalog" variant="accent" className="w-full sm:w-auto">Khám phá ngay <Icon name="arrow" /></LinkButton>
                <LinkButton to="/promotions" variant="secondary" className="w-full border-white/30 bg-transparent text-white hover:bg-white/10 sm:w-auto">Xem khuyến mãi</LinkButton>
              </div>
            </div>
            <div className="relative min-h-64 overflow-hidden bg-primary-container md:min-h-full">
              <div
                className="flex h-full transition-transform duration-700 ease-out motion-reduce:transition-none"
                style={{ transform: `translateX(-${heroSlide * 100}%)` }}
              >
                {heroSlides.map((slide, index) => (
                  <img
                    key={slide.src}
                    src={slide.src}
                    alt={slide.alt}
                    loading={index === 0 ? 'eager' : 'lazy'}
                    className="h-64 min-w-full object-cover md:h-full"
                  />
                ))}
              </div>
              <div className="absolute inset-x-0 bottom-0 flex items-center justify-between bg-gradient-to-t from-primary/70 to-transparent px-4 pb-4 pt-10">
                <div className="flex gap-2" role="tablist" aria-label="Chọn ảnh giới thiệu">
                  {heroSlides.map((slide, index) => (
                    <button
                      key={slide.src}
                      type="button"
                      onClick={() => setHeroSlide(index)}
                      className={`h-2.5 rounded-full transition-all ${heroSlide === index ? 'w-7 bg-secondary-container' : 'w-2.5 bg-white/70 hover:bg-white'}`}
                      aria-label={`Hiển thị ảnh ${index + 1}`}
                      aria-selected={heroSlide === index}
                      role="tab"
                    />
                  ))}
                </div>
                <div className="flex gap-2">
                  <button type="button" onClick={() => setHeroSlide((current) => (current - 1 + heroSlides.length) % heroSlides.length)} className="flex h-10 w-10 items-center justify-center rounded-md border border-white/40 bg-primary/70 text-white transition hover:bg-primary" aria-label="Ảnh trước">
                    <span aria-hidden="true">←</span>
                  </button>
                  <button type="button" onClick={() => setHeroSlide((current) => (current + 1) % heroSlides.length)} className="flex h-10 w-10 items-center justify-center rounded-md border border-white/40 bg-primary/70 text-white transition hover:bg-primary" aria-label="Ảnh tiếp theo">
                    <span aria-hidden="true">→</span>
                  </button>
                </div>
              </div>
            </div>
          </section>

          <aside className="grid min-w-0 grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-1">
            {featured ? <Panel className="min-h-[248px] overflow-hidden">
              <div className="grid h-full min-w-0 grid-cols-[128px_minmax(0,1fr)] bg-white sm:grid-cols-[140px_minmax(0,1fr)]">
              <div className="flex items-center bg-surface-container-low p-4">
                <img
                  src={featured.cover || bookCoverPlaceholder}
                  alt={featured.title}
                  onError={(event) => { event.currentTarget.src = bookCoverPlaceholder; }}
                  className="aspect-[3/4] w-full rounded-md bg-white object-contain shadow-sm"
                />
              </div>
              <div className="flex min-w-0 flex-col justify-between p-4 sm:p-5">
                <div>
                  <p className="text-xs font-semibold uppercase tracking-wide text-secondary">Nổi bật tuần này</p>
                  <h2 className="mt-2 font-serif text-lg font-bold leading-7 text-primary sm:text-xl">{featured.title}</h2>
                  <p className="mt-2 text-sm text-on-surface-variant">{featured.author?.name}</p>
                </div>
                <button onClick={() => addToCart(featured.id)} className="mt-4 inline-flex min-h-11 items-center gap-2 text-sm font-semibold text-primary hover:text-secondary">
                  Thêm vào giỏ <Icon name="cart" className="h-4 w-4" />
                </button>
              </div>
            </div>
            </Panel> : <EmptyState title="Chưa có sách nổi bật" description="Sách nổi bật sẽ xuất hiện khi hệ thống có dữ liệu." />}

            <Panel className="min-h-[248px] border-secondary-container !bg-secondary-fixed p-6 text-on-secondary-container">
            <p className="text-xs font-semibold uppercase tracking-wide">Ưu đãi từ hệ thống</p>
            <h2 className="mt-3 font-serif text-2xl font-bold">Khám phá voucher đang áp dụng</h2>
            <p className="mt-2 text-sm leading-6">Đăng nhập để xem chính xác mức giảm, số lượng và thời hạn của từng voucher.</p>
            <Link to="/promotions" className="mt-5 inline-flex min-h-11 items-center text-sm font-semibold text-primary hover:underline">Xem ưu đãi <Icon name="arrow" className="ml-2 h-4 w-4" /></Link>
            </Panel>
          </aside>
        </div>

        <div className="mt-6 grid gap-px overflow-hidden rounded-lg border border-outline-variant bg-outline-variant sm:grid-cols-3">
          {['Sách chọn lọc, thông tin rõ ràng', 'Thanh toán an toàn, theo dõi dễ dàng', 'Hỗ trợ tận tâm trong mỗi đơn hàng'].map((item) => (
            <div key={item} className="bg-surface px-5 py-4 text-center text-sm font-medium text-on-surface-variant">{item}</div>
          ))}
        </div>
      </Container>

      <Container className="py-10">
        <div className="mb-8 flex flex-wrap items-start justify-between gap-4">
          <h2 className="min-w-0 font-serif text-2xl font-bold leading-snug text-primary">Khám phá theo danh mục</h2>
          <Link to="/categories" className="text-sm font-bold text-secondary hover:underline">Tất cả danh mục</Link>
        </div>
        {categories.length ? <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-6">
          {categories.slice(0, 6).map((category) => (
            <Link key={category.id} to={`/catalog?category=${category.id}`}>
              <Panel className="h-full p-5 text-center transition hover:-translate-y-0.5 hover:border-border-strong hover:shadow-sm">
                <div className="mx-auto mb-4 flex h-11 w-11 items-center justify-center rounded-md bg-primary-fixed text-primary">
                  <Icon name="category" />
                </div>
                <h3 className="font-bold text-primary">{category.name}</h3>
                <p className="mt-2 line-clamp-2 text-xs leading-5 text-on-surface-variant">{category.description}</p>
              </Panel>
            </Link>
          ))}
        </div> : <EmptyState title="Chưa có danh mục" description="Danh mục sách sẽ hiển thị khi hệ thống có dữ liệu." />}
      </Container>

      <Surface className="py-12">
        <Container>
          <div className="mb-8 flex flex-wrap items-start justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-primary">Sách bán chạy</h2>
              <p className="mt-2 text-sm text-on-surface-variant">Những tựa sách được nhiều độc giả quan tâm và lựa chọn.</p>
            </div>
            <Link to="/catalog" className="text-sm font-bold text-secondary hover:underline">Xem tất cả</Link>
          </div>
          {books.length ? (
            <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
              {books.slice(0, 8).map((book) => (
                <BookCard
                  key={book.id || book.title}
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
          ) : (
            <EmptyState title="Chưa có sách" description="Khi máy chủ trả dữ liệu, danh sách sản phẩm sẽ hiển thị tại đây." />
          )}
        </Container>
      </Surface>

      <Container className="py-12">
        <div className="mb-8 flex flex-wrap items-start justify-between gap-4">
          <div>
            <p className="mb-2 text-xs font-bold uppercase text-secondary">Gợi ý từ nhà sách</p>
            <h2 className="font-serif text-2xl font-bold text-primary">Tác giả nổi bật</h2>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-on-surface-variant">Khám phá các cây bút được nhiều độc giả quan tâm và xem nhanh những đầu sách đang có trong hệ thống.</p>
          </div>
          <Link to="/authors" className="shrink-0 text-sm font-bold text-secondary hover:underline">Tất cả tác giả</Link>
        </div>

        {authors.length ? <div className="grid gap-5 md:grid-cols-3">
          {authors.slice(0, 3).map((author, index) => (
            <Panel key={author.id || author.authorName} className="h-full p-6 transition hover:-translate-y-0.5 hover:border-border-strong hover:shadow-sm">
              <div className="mb-5 flex items-center justify-between gap-4">
                <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full bg-primary/5 text-primary">
                  <Icon name="user" className="h-6 w-6" />
                </div>
                <span className="text-xs font-semibold uppercase tracking-wide text-text-muted">Gợi ý {index + 1}</span>
              </div>
              <h3 className="text-xl font-bold text-primary">{author.authorName}</h3>
              <p className="mt-3 line-clamp-3 min-h-[72px] text-sm leading-6 text-on-surface-variant">
                {author.biography || 'Tiểu sử tác giả đang được cập nhật.'}
              </p>
              <Link
                to={`/catalog?authorId=${author.id}&authorName=${encodeURIComponent(author.authorName)}`}
                className="mt-5 inline-flex items-center gap-2 text-sm font-bold text-secondary hover:underline"
              >
                Xem sách <Icon name="arrow" className="h-4 w-4" />
              </Link>
            </Panel>
          ))}
        </div> : <EmptyState title="Chưa có tác giả" description="Danh sách tác giả sẽ hiển thị khi hệ thống có dữ liệu." />}
      </Container>
    </div>
  );
};

export default HomePage;
