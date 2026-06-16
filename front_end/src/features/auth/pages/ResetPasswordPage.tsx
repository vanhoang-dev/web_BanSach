import { FormEvent, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';

import { Field, Icon, PageShell, Panel, PrimaryButton } from '@/components/ui/staticUi';
import authService from '@/features/auth/services';

const ResetPasswordPage = () => {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const [form, setForm] = useState({ token: params.get('token') || '', newPassword: '', confirmPassword: '' });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const update = (key: string, value: string) => setForm((current) => ({ ...current, [key]: value }));

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');
    if (form.newPassword !== form.confirmPassword) {
      setError('Mật khẩu xác nhận chưa khớp.');
      return;
    }
    try {
      setLoading(true);
      await authService.resetPassword(form);
      setMessage('Đặt lại mật khẩu thành công.');
      setTimeout(() => navigate('/login'), 800);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Không thể đặt lại mật khẩu.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageShell>
      <div className="flex min-h-screen items-center justify-center px-4 py-12">
        <Panel className="w-full max-w-lg p-8 text-center lg:p-12">
          <div className="mx-auto mb-5 flex h-14 w-14 items-center justify-center rounded-full bg-primary/10 text-primary"><Icon name="lock" className="h-6 w-6" /></div>
          <h1 className="text-3xl font-bold text-primary">Đặt lại mật khẩu</h1>
          <p className="mt-3 text-on-surface-variant">Nhập token trong email và mật khẩu mới của bạn.</p>
          {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          {message ? <div className="mt-5 rounded-lg bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}
          <form className="mt-6 space-y-4 text-left" onSubmit={handleSubmit}>
            <Field label="Token" value={form.token} onChange={(event) => update('token', event.target.value)} required />
            <Field label="Mật khẩu mới" value={form.newPassword} onChange={(event) => update('newPassword', event.target.value)} type="password" required />
            <Field label="Xác nhận mật khẩu" value={form.confirmPassword} onChange={(event) => update('confirmPassword', event.target.value)} type="password" required />
            <PrimaryButton disabled={loading} className="w-full" type="submit">{loading ? 'Đang cập nhật...' : 'Cập nhật mật khẩu'}</PrimaryButton>
          </form>
          <Link to="/login" className="mt-6 inline-block text-sm font-bold text-secondary hover:underline">Quay lại đăng nhập</Link>
        </Panel>
      </div>
    </PageShell>
  );
};

export default ResetPasswordPage;
