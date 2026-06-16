import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { AccentButton, BookCard, Container, EmptyState, Icon, Panel, PrimaryButton, SecondaryButton, StatCard, Surface } from '@/components/ui/staticUi';
import bookService, { Book, Category } from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';

const fallbackBooks: Book[] = [
  { id: 1, title: 'Tư duy nhanh và chậm', author: { id: 1, name: 'Daniel Kahneman' }, category: { id: 1, name: 'Tâm lý học' }, price: 189000, cover: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=520&q=80', discount: 15 },
  { id: 2, title: 'Atomic Habits', author: { id: 2, name: 'James Clear' }, category: { id: 2, name: 'Kỹ năng' }, price: 168000, cover: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=520&q=80' },
  { id: 3, title: 'Sapiens', author: { id: 3, name: 'Yuval Noah Harari' }, category: { id: 3, name: 'Lịch sử' }, price: 210000, cover: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=520&q=80' },
  { id: 4, title: 'Nhà giả kim', author: { id: 4, name: 'Paulo Coelho' }, category: { id: 4, name: 'Văn học' }, price: 79000, cover: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=520&q=80', discount: 10 },
];

const fallbackCategories: Category[] = [
  { id: 1, name: 'Kinh doanh', description: 'Quản trị, tài chính, bán hàng' },
  { id: 2, name: 'Kỹ năng sống', description: 'Thói quen, tư duy, giao tiếp' },
  { id: 3, name: 'Văn học', description: 'Tiểu thuyết và tác phẩm kinh điển' },
  { id: 4, name: 'Thiếu nhi', description: 'Sách học tập và khám phá' },
  { id: 5, name: 'Công nghệ', description: 'Lập trình và chuyển đổi số' },
  { id: 6, name: 'Lịch sử', description: 'Thế giới, con người, văn minh' },
];

const HomePage = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    Promise.all([bookService.getFeaturedBooks(8), bookService.getCategories()])
      .then(([bookData, categoryData]) => {
        if (!active) return;
        setBooks(bookData.length ? bookData : fallbackBooks);
        setCategories(categoryData.length ? categoryData : fallbackCategories);
      })
      .catch(() => {
        if (!active) return;
        setBooks(fallbackBooks);
        setCategories(fallbackCategories);
        setError('Đang hiển thị dữ liệu mẫu vì chưa kết nối được máy chủ.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  const addToCart = async (bookId?: number) => {
    if (!bookId) return;
    try {
      await cartService.addToCart(bookId, 1);
      window.alert('Đã thêm sách vào giỏ hàng');
    } catch {
      window.alert('Không thể thêm vào giỏ hàng. Vui lòng đăng nhập hoặc thử lại.');
    }
  };

  const featured = books[0] || fallbackBooks[0];

  return (
    <div>
      {loading ? <div className="h-1 overflow-hidden bg-surface-container"><div className="h-full w-1/3 animate-pulse bg-primary" /></div> : null}
      {error ? <Container className="pt-4"><div className="rounded-lg border border-secondary-container bg-secondary-container/20 px-4 py-3 text-sm font-semibold text-secondary">{error}</div></Container> : null}

      <Container className="py-8">
        <div className="grid gap-6 lg:grid-cols-12 lg:[grid-auto-rows:260px]">
          <Panel className="relative overflow-hidden bg-primary text-on-primary lg:col-span-8 lg:row-span-2">
            <img
              src="https://images.unsplash.com/photo-1526243741027-444d633d7365?auto=format&fit=crop&w=1400&q=80"
              alt="Không gian đọc sách"
              className="absolute inset-0 h-full w-full object-cover opacity-35"
            />
            <div className="relative flex h-full min-h-[420px] flex-col justify-end p-8 md:p-12">
              <span className="mb-4 w-fit rounded-sm bg-secondary px-3 py-1 text-xs font-bold uppercase text-on-secondary">Sự kiện sách mới</span>
              <h1 className="max-w-2xl text-4xl font-bold leading-tight md:text-5xl">Khám phá thế giới qua từng trang sách chọn lọc</h1>
              <p className="mt-5 max-w-xl text-lg leading-8 text-on-primary/90">Mua sách nhanh, theo dõi đơn rõ ràng và nhận các ưu đãi tốt nhất từ Nhà Sách Tri Thức.</p>
              <div className="mt-7 flex flex-wrap gap-3">
                <Link to="/catalog"><AccentButton>Khám phá ngay <Icon name="arrow" /></AccentButton></Link>
                <Link to="/promotions"><SecondaryButton className="border-on-primary/30 bg-on-primary/10 text-on-primary hover:bg-on-primary/20">Xem khuyến mãi</SecondaryButton></Link>
              </div>
            </div>
          </Panel>

          <Panel className="overflow-hidden lg:col-span-4">
            <div className="grid h-full grid-cols-[140px_1fr] bg-surface-container-low">
              <img src={featured.cover} alt={featured.title} className="h-full min-h-60 w-full object-cover" />
              <div className="flex flex-col justify-between p-5">
                <div>
                  <p className="text-xs font-bold uppercase text-secondary">Nổi bật tuần này</p>
                  <h2 className="mt-2 text-xl font-bold text-primary">{featured.title}</h2>
                  <p className="mt-2 text-sm text-on-surface-variant">{featured.author?.name}</p>
                </div>
                <button onClick={() => addToCart(featured.id)} className="mt-4 inline-flex items-center gap-2 text-sm font-bold text-secondary hover:underline">
                  Thêm vào giỏ <Icon name="cart" className="h-4 w-4" />
                </button>
              </div>
            </div>
          </Panel>

          <Panel className="bg-secondary-container p-6 text-on-secondary-container lg:col-span-4">
            <p className="text-sm font-bold uppercase">Voucher hôm nay</p>
            <h2 className="mt-3 text-3xl font-bold">Giảm 25%</h2>
            <p className="mt-2 text-sm leading-6">Áp dụng cho danh mục kỹ năng và kinh doanh. Số lượng có hạn.</p>
            <Link to="/promotions" className="mt-5 inline-flex text-sm font-bold underline">Nhận mã ngay</Link>
          </Panel>
        </div>
      </Container>

      <Container className="py-10">
        <div className="mb-8 flex items-center justify-between gap-4">
          <h2 className="border-l-4 border-secondary pl-4 text-2xl font-bold text-primary">Danh mục nổi bật</h2>
          <Link to="/categories" className="text-sm font-bold text-secondary hover:underline">Tất cả danh mục</Link>
        </div>
        <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-6">
          {categories.slice(0, 6).map((category) => (
            <Link key={category.id} to={`/catalog?category=${category.id}`}>
              <Panel className="h-full p-5 text-center transition hover:-translate-y-1 hover:shadow-md">
                <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/5 text-primary">
                  <Icon name="category" />
                </div>
                <h3 className="font-bold text-primary">{category.name}</h3>
                <p className="mt-2 line-clamp-2 text-xs leading-5 text-on-surface-variant">{category.description}</p>
              </Panel>
            </Link>
          ))}
        </div>
      </Container>

      <Surface className="py-12">
        <Container>
          <div className="mb-8 flex items-center justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-primary">Sách bán chạy</h2>
              <p className="mt-2 text-sm text-on-surface-variant">Danh sách lấy từ API sách công khai của hệ thống.</p>
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

      <Container className="grid gap-5 py-10 md:grid-cols-3">
        <StatCard label="Phân hệ người dùng" value="7" detail="Danh mục sách, giỏ hàng, thanh toán, yêu thích, đơn hàng..." icon="users" />
        <StatCard label="Phân hệ quản trị" value="8" detail="Tổng quan, sách, đơn hàng, tồn kho..." icon="chart" tone="success" />
        <StatCard label="Thanh toán" value="SePay" detail="Có trạng thái thanh toán và webhook đối soát" icon="ticket" tone="warning" />
      </Container>

      <Container className="pb-12">
        <Panel className="grid gap-8 bg-primary-container p-8 text-on-primary md:grid-cols-[1fr_auto] md:items-center">
          <div>
            <p className="text-sm font-bold uppercase text-primary-fixed-dim">Ưu đãi thành viên</p>
            <h2 className="mt-2 text-3xl font-bold">Nhận mã giảm giá và gợi ý sách phù hợp với bạn</h2>
            <p className="mt-3 max-w-2xl text-on-primary-container">Lưu sách yêu thích, theo dõi đơn hàng và nhận voucher theo lịch sử mua sách.</p>
          </div>
          <Link to="/register"><PrimaryButton className="bg-secondary-container text-on-secondary-container hover:brightness-105">Tạo tài khoản</PrimaryButton></Link>
        </Panel>
      </Container>
    </div>
  );
};

export default HomePage;
