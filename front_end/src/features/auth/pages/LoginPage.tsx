import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
import { Field, Icon, PrimaryButton } from '@/components/ui/staticUi';
import { useAuth } from '@/hooks/useAuth';

const authHeroImage = 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=1400&q=85';

const BrandLink = ({ mobile = false }: { mobile?: boolean }) => (
  <Link to="/" className={`flex items-center gap-3 text-lg font-bold ${mobile ? 'text-primary' : 'text-white'}`}>
    <span className="flex h-12 w-12 items-center justify-center overflow-hidden rounded-lg border border-outline-variant bg-white shadow-sm">
      <img src={logoWeb} alt="Nhà Sách Tri Thức" className="h-full w-full object-cover" />
    </span>
    Nhà Sách Tri Thức
  </Link>
);

const PasswordField = ({
  value,
  onChange,
}: {
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}) => {
  const [visible, setVisible] = useState(false);

  return (
    <label className="block">
      <span className="mb-2 block text-sm font-semibold text-on-surface">Mật khẩu</span>
      <div className="relative">
        <input
          type={visible ? 'text' : 'password'}
          value={value}
          onChange={onChange}
          placeholder="********"
          className="h-11 w-full rounded-lg border border-outline-variant bg-surface px-4 pr-20 text-sm text-on-surface shadow-sm outline-none transition placeholder:text-outline focus:border-primary focus:ring-2 focus:ring-primary/20"
        />
        <button
          type="button"
          onClick={() => setVisible((current) => !current)}
          aria-label={visible ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
          title={visible ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
          className="absolute right-2 top-1/2 inline-flex h-8 w-8 -translate-y-1/2 items-center justify-center rounded-md text-secondary transition hover:bg-secondary-container/30"
        >
          <Icon name={visible ? 'eye-off' : 'eye'} className="h-5 w-5" />
        </button>
      </div>
    </label>
  );
};

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (!email || !password) {
      setError('Vui lòng nhập email và mật khẩu.');
      return;
    }
    try {
      setLoading(true);
      setError('');
      await login(email, password);
      navigate('/');
    } catch (err: any) {
      setError(err?.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grid min-h-screen bg-background lg:grid-cols-2">
      <aside className="relative hidden overflow-hidden p-10 text-white lg:flex lg:flex-col lg:justify-between">
        <img src={authHeroImage} alt="Kệ sách trong thư viện" className="absolute inset-0 h-full w-full object-cover" />
        <div className="absolute inset-0 bg-primary/70" />
        <div className="absolute inset-0 bg-gradient-to-t from-primary via-primary/45 to-primary/20" />

        <div className="relative z-10">
          <BrandLink />
        </div>
        <div className="relative z-10">
          <p className="text-xs font-bold uppercase text-secondary-fixed">Chào mừng trở lại</p>
          <h1 className="mt-4 max-w-xl text-5xl font-bold leading-tight">
            Quay lại không gian mua sách và quản lý đơn hàng của bạn.
          </h1>
        </div>
      </aside>

      <main className="flex items-center justify-center px-4 py-10">
        <div className="w-full max-w-md">
          <div className="mb-8 lg:hidden">
            <BrandLink mobile />
          </div>
          <h2 className="text-3xl font-bold text-primary">Đăng nhập</h2>
          <p className="mt-2 text-sm text-on-surface-variant">Nhập tài khoản để tiếp tục mua sắm.</p>
          {error ? (
            <div className="mt-5 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">
              {error}
            </div>
          ) : null}
          <form onSubmit={submit} className="mt-6 grid gap-4">
            <Field
              label="Email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="email@example.com"
            />
            <PasswordField value={password} onChange={(event) => setPassword(event.target.value)} />
            <div className="flex items-center justify-between text-sm">
              <label className="flex items-center gap-2 font-semibold text-on-surface-variant">
                <input type="checkbox" className="rounded border-outline-variant" /> Ghi nhớ
              </label>
              <Link to="/forgot-password" className="font-bold text-secondary hover:underline">
                Quên mật khẩu?
              </Link>
            </div>
            <PrimaryButton type="submit" disabled={loading}>
              {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
            </PrimaryButton>
          </form>
          <p className="mt-6 text-center text-sm text-on-surface-variant">
            Chưa có tài khoản?{' '}
            <Link to="/register" className="font-bold text-secondary hover:underline">
              Đăng ký
            </Link>
          </p>
        </div>
      </main>
    </div>
  );
};

export default LoginPage;
