import { FormEvent, useState } from 'react';
import { Link } from 'react-router-dom';

import { Field, Icon, PageShell, Panel, PrimaryButton } from '@/components/ui/staticUi';
import authService from '@/features/auth/services';

const ForgotPasswordPage = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');
    try {
      setLoading(true);
      await authService.forgotPassword({ email });
      setMessage('Nếu email tồn tại, hệ thống sẽ gửi liên kết đặt lại mật khẩu.');
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Không thể gửi yêu cầu đặt lại mật khẩu.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageShell>
      <div className="flex min-h-screen items-center justify-center px-4 py-12">
        <Panel className="w-full max-w-lg p-8 text-center lg:p-12">
          <div className="mx-auto mb-5 flex h-14 w-14 items-center justify-center rounded-full bg-primary/10 text-primary"><Icon name="lock" className="h-6 w-6" /></div>
          <h1 className="text-3xl font-bold text-primary">Quên mật khẩu</h1>
          <p className="mt-3 text-on-surface-variant">Nhập email của bạn để nhận liên kết đặt lại mật khẩu.</p>
          {error ? <div className="mt-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
          {message ? <div className="mt-5 rounded-lg bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}
          <form className="mt-6 space-y-4 text-left" onSubmit={handleSubmit}>
            <Field label="Email" value={email} onChange={(event) => setEmail(event.target.value)} placeholder="name@example.com" type="email" required />
            <PrimaryButton disabled={loading} className="w-full" type="submit"><Icon name="mail" />{loading ? 'Đang gửi...' : 'Gửi liên kết'}</PrimaryButton>
          </form>
          <Link to="/login" className="mt-6 inline-block text-sm font-bold text-secondary hover:underline">Quay lại đăng nhập</Link>
        </Panel>
      </div>
    </PageShell>
  );
};

export default ForgotPasswordPage;
