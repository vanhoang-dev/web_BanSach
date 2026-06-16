import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { AccentButton, Field, Icon } from '@/components/ui/staticUi';
import authService from '@/features/auth/services';

const RegisterPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: '', email: '', phone: '', address: '', password: '', confirmPassword: '' });
  const [accepted, setAccepted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const update = (key: string, value: string) => setForm((current) => ({ ...current, [key]: value }));

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');
    if (form.password !== form.confirmPassword) {
      setError('Mật khẩu xác nhận chưa khớp.');
      return;
    }
    if (!accepted) {
      setError('Bạn cần đồng ý điều khoản trước khi đăng ký.');
      return;
    }
    try {
      setLoading(true);
      await authService.register(form);
      setMessage('Đăng ký thành công. Bạn có thể đăng nhập ngay.');
      setTimeout(() => navigate('/login'), 700);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Không thể đăng ký tài khoản.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grid min-h-screen bg-background lg:grid-cols-[0.9fr_1.1fr]">
      <main className="flex items-center justify-center px-4 py-10">
        <div className="w-full max-w-lg">
          <Link to="/" className="mb-8 flex items-center gap-3 text-lg font-bold text-primary">
            <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-on-primary"><Icon name="book" /></span>
            Nhà Sách Tri Thức
          </Link>
          <h1 className="text-3xl font-bold text-primary">Tạo tài khoản</h1>
          <p className="mt-2 text-sm text-on-surface-variant">Đăng ký để lưu sách yêu thích, đặt hàng và theo dõi lịch sử mua sách.</p>
          {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          {message ? <div className="mt-5 rounded-lg bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}
          <form className="mt-6 grid gap-4" onSubmit={handleSubmit}>
            <Field label="Họ và tên" value={form.fullName} onChange={(e) => update('fullName', e.target.value)} placeholder="Nguyễn Văn A" required />
            <Field label="Email" value={form.email} onChange={(e) => update('email', e.target.value)} type="email" placeholder="email@example.com" required />
            <Field label="Số điện thoại" value={form.phone} onChange={(e) => update('phone', e.target.value)} placeholder="0901234567" />
            <Field label="Địa chỉ" value={form.address} onChange={(e) => update('address', e.target.value)} placeholder="Số nhà, đường, phường/xã..." />
            <Field label="Mật khẩu" value={form.password} onChange={(e) => update('password', e.target.value)} type="password" placeholder="********" required />
            <Field label="Xác nhận mật khẩu" value={form.confirmPassword} onChange={(e) => update('confirmPassword', e.target.value)} type="password" placeholder="********" required />
            <label className="flex items-start gap-3 text-sm text-on-surface-variant">
              <input checked={accepted} onChange={(e) => setAccepted(e.target.checked)} type="checkbox" className="mt-1 rounded border-outline-variant" />
              Tôi đồng ý với điều khoản sử dụng và chính sách bảo mật của Nhà Sách Tri Thức.
            </label>
            <AccentButton disabled={loading} type="submit">{loading ? 'Đang đăng ký...' : 'Đăng ký tài khoản'}</AccentButton>
          </form>
          <p className="mt-6 text-center text-sm text-on-surface-variant">Đã có tài khoản? <Link to="/login" className="font-bold text-secondary hover:underline">Đăng nhập</Link></p>
        </div>
      </main>
      <aside className="hidden bg-surface-container lg:block">
        <img src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=1200&q=80" alt="Không gian thư viện" className="h-full w-full object-cover" />
      </aside>
    </div>
  );
};

export default RegisterPage;
