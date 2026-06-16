import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { Field, Icon, PrimaryButton } from '@/components/ui/staticUi';
import { useAuth } from '@/hooks/useAuth';

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
      <aside className="hidden bg-primary p-10 text-on-primary lg:flex lg:flex-col lg:justify-between">
        <Link to="/" className="flex items-center gap-3 text-lg font-bold"><span className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary-container text-on-secondary-container"><Icon name="book" /></span>Nhà Sách Tri Thức</Link>
        <div>
          <p className="text-xs font-bold uppercase text-secondary-fixed">Welcome back</p>
          <h1 className="mt-4 max-w-xl text-5xl font-bold leading-tight">Quay lại không gian mua sách và quản lý đơn hàng của bạn.</h1>
        </div>
      </aside>
      <main className="flex items-center justify-center px-4 py-10">
        <div className="w-full max-w-md">
          <Link to="/" className="mb-8 flex items-center gap-3 text-lg font-bold text-primary lg:hidden"><span className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-on-primary"><Icon name="book" /></span>Nhà Sách Tri Thức</Link>
          <h2 className="text-3xl font-bold text-primary">Đăng nhập</h2>
          <p className="mt-2 text-sm text-on-surface-variant">Nhập tài khoản để tiếp tục mua sắm.</p>
          {error ? <div className="mt-5 rounded-lg border border-error-container bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          <form onSubmit={submit} className="mt-6 grid gap-4">
            <Field label="Email" type="email" value={email} onChange={(event) => setEmail(event.target.value)} placeholder="email@example.com" />
            <Field label="Mật khẩu" type="password" value={password} onChange={(event) => setPassword(event.target.value)} placeholder="••••••••" />
            <div className="flex items-center justify-between text-sm">
              <label className="flex items-center gap-2 font-semibold text-on-surface-variant"><input type="checkbox" className="rounded border-outline-variant" /> Ghi nhớ</label>
              <Link to="/forgot-password" className="font-bold text-secondary hover:underline">Quên mật khẩu?</Link>
            </div>
            <PrimaryButton type="submit" disabled={loading}>{loading ? 'Đang đăng nhập...' : 'Đăng nhập'}</PrimaryButton>
          </form>
          <p className="mt-6 text-center text-sm text-on-surface-variant">Chưa có tài khoản? <Link to="/register" className="font-bold text-secondary hover:underline">Đăng ký</Link></p>
        </div>
      </main>
    </div>
  );
};

export default LoginPage;
