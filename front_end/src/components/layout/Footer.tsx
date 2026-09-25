import { Link } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
import { Container } from '@/components/ui/staticUi';

const Footer = () => (
  <footer className="bg-primary text-on-primary">
    <Container className="py-12">
      <div className="grid gap-8 md:grid-cols-4">
        <div>
          <div className="mb-4 flex items-center gap-3">
            <span className="flex h-11 w-11 items-center justify-center overflow-hidden rounded-md border border-white/10 bg-white">
              <img src={logoWeb} alt="Nhà Sách Tri Thức" className="h-full w-full object-cover" />
            </span>
            <span className="font-serif text-lg font-bold">Nhà Sách Tri Thức</span>
          </div>
          <p className="text-sm leading-6 text-white/65">
            Mang lại giá trị tri thức qua những cuốn sách chọn lọc, dịch vụ rõ ràng và trải nghiệm mua sắm đáng tin cậy.
          </p>
        </div>

        {[
          ['Khám phá', [['Sách mới phát hành', '/new-books'], ['Danh mục', '/categories'], ['Tác giả', '/authors']]],
          ['Tài khoản', [['Giỏ hàng', '/cart'], ['Yêu thích', '/wishlist'], ['Đơn hàng', '/orders']]],
          ['Hỗ trợ', [['Khuyến mãi', '/promotions'], ['Chính sách giao hàng', '/shipping'], ['Điều khoản sử dụng', '/terms']]],
        ].map(([title, links]) => (
          <div key={title as string}>
            <h3 className="mb-4 font-sans text-sm font-semibold text-secondary-container">{title as string}</h3>
            <ul className="space-y-3">
              {(links as string[][]).map(([label, to]) => (
                <li key={to}>
                  <Link to={to} className="text-sm font-medium text-white/65 transition hover:text-white">
                    {label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>

      <div className="mt-10 flex flex-col gap-3 border-t border-white/10 pt-6 text-sm text-white/55 sm:flex-row sm:items-center sm:justify-between">
        <span>© 2026 Nhà Sách Tri Thức. Bảo lưu mọi quyền.</span>
        <span>contact@nhasachtrithuc.vn · 1900 0000</span>
      </div>
    </Container>
  </footer>
);

export default Footer;
