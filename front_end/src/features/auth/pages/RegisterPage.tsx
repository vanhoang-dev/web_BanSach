import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';

import logoWeb from '@/assets/icons/logoweb.png';
import { AccentButton, Field, Icon, SecondaryButton } from '@/components/ui/staticUi';
import { isStrongPassword, PASSWORD_REQUIREMENTS_MESSAGE } from '@/features/auth/passwordValidation';
import authService from '@/features/auth/services';

type PasswordFieldProps = {
  label: string;
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
};

const getErrorMessage = (error: any, fallback: string) =>
  error?.message || error?.response?.data?.message || fallback;

const PasswordField = ({ label, value, onChange }: PasswordFieldProps) => {
  const [visible, setVisible] = useState(false);
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-semibold text-on-surface">{label}</span>
      <div className="relative">
        <input type={visible ? 'text' : 'password'} value={value} onChange={onChange} placeholder="••••••••"
          autoComplete="new-password" required
          className="h-11 w-full rounded-md border border-border-strong bg-surface px-4 pr-12 text-sm text-on-surface outline-none transition placeholder:text-text-muted focus:border-primary focus:ring-2 focus:ring-primary/15" />
        <button type="button" onClick={() => setVisible((current) => !current)}
          aria-label={visible ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'} title={visible ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
          className="absolute right-1.5 top-1/2 inline-flex h-8 w-8 -translate-y-1/2 items-center justify-center rounded-md text-on-surface-variant transition hover:bg-surface-container">
          <Icon name={visible ? 'eye-off' : 'eye'} className="h-5 w-5" />
        </button>
      </div>
    </label>
  );
};

const RegisterPage = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const pendingEmail = window.sessionStorage.getItem('pendingVerificationEmail') || '';
  const [step, setStep] = useState<'register' | 'verify'>(searchParams.get('verify') === '1' && pendingEmail ? 'verify' : 'register');
  const [form, setForm] = useState({ fullName: '', email: pendingEmail, phone: '', address: '', password: '', confirmPassword: '' });
  const [verificationCode, setVerificationCode] = useState('');
  const [accepted, setAccepted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [resendSeconds, setResendSeconds] = useState(0);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (resendSeconds <= 0) return;
    const timer = window.setInterval(() => setResendSeconds((current) => Math.max(0, current - 1)), 1000);
    return () => window.clearInterval(timer);
  }, [resendSeconds]);

  const update = (key: string, value: string) => setForm((current) => ({ ...current, [key]: value }));

  const handleRegister = async (event: FormEvent) => {
    event.preventDefault();
    setError(''); setMessage('');
    if (form.password !== form.confirmPassword) return setError('Mật khẩu xác nhận chưa khớp.');
    if (!isStrongPassword(form.password)) return setError(PASSWORD_REQUIREMENTS_MESSAGE);
    if (!accepted) return setError('Bạn cần đồng ý điều khoản trước khi đăng ký.');
    try {
      setLoading(true);
      await authService.register(form);
      setStep('verify'); setResendSeconds(60);
      window.sessionStorage.setItem('pendingVerificationEmail', form.email);
      setSearchParams({ verify: '1' }, { replace: true });
      setMessage('Mã xác nhận gồm 6 chữ số đã được gửi tới email của bạn.');
    } catch (requestError: any) {
      setError(getErrorMessage(requestError, 'Không thể đăng ký tài khoản.'));
    } finally { setLoading(false); }
  };

  const handleVerify = async (event: FormEvent) => {
    event.preventDefault();
    setError(''); setMessage('');
    if (!/^\d{6}$/.test(verificationCode)) return setError('Vui lòng nhập đủ 6 chữ số trong email.');
    try {
      setLoading(true);
      await authService.verifyAccount({ email: form.email, code: verificationCode });
      window.sessionStorage.removeItem('pendingVerificationEmail');
      setMessage('Tài khoản đã được xác nhận. Đang chuyển đến trang đăng nhập...');
      window.setTimeout(() => navigate('/login', { replace: true }), 1200);
    } catch (requestError: any) {
      setError(getErrorMessage(requestError, 'Không thể xác nhận tài khoản.'));
    } finally { setLoading(false); }
  };

  const handleResend = async () => {
    setError(''); setMessage('');
    try {
      setLoading(true);
      await authService.resendVerificationCode(form.email);
      setVerificationCode(''); setResendSeconds(60);
      setMessage('Một mã xác nhận mới đã được gửi đến email của bạn.');
    } catch (requestError: any) {
      setError(getErrorMessage(requestError, 'Không thể gửi lại mã xác nhận.'));
    } finally { setLoading(false); }
  };

  const continueWithExistingCode = () => {
    if (!/^\S+@\S+\.\S+$/.test(form.email)) {
      setError('Nhập email đã đăng ký để tiếp tục xác nhận.');
      return;
    }
    window.sessionStorage.setItem('pendingVerificationEmail', form.email);
    setSearchParams({ verify: '1' }, { replace: true });
    setStep('verify'); setError(''); setMessage('');
  };

  return (
    <div className="grid min-h-screen bg-background lg:grid-cols-[0.9fr_1.1fr]">
      <main className="flex items-center justify-center px-4 py-10 sm:px-6">
        <div className="w-full max-w-lg">
          <Link to="/" className="mb-8 flex items-center gap-3 text-lg font-bold text-primary">
            <span className="flex h-12 w-12 items-center justify-center overflow-hidden rounded-lg border border-outline-variant bg-white">
              <img src={logoWeb} alt="Nhà Sách Tri Thức" className="h-full w-full object-cover" />
            </span>
            Nhà Sách Tri Thức
          </Link>

          <p className="mb-3 text-xs font-semibold uppercase tracking-[0.18em] text-secondary">
            {step === 'register' ? 'Tài khoản mới' : 'Bước cuối'}
          </p>
          <h1 className="font-serif text-3xl font-bold leading-tight text-primary">
            {step === 'register' ? 'Tạo tài khoản' : 'Xác nhận email'}
          </h1>
          <p className="mt-2 text-sm leading-6 text-on-surface-variant">
            {step === 'register' ? 'Đăng ký để lưu sách yêu thích, đặt hàng và theo dõi lịch sử mua sách.' : (
              <>Nhập mã 6 chữ số đã gửi đến <strong className="font-semibold text-on-surface">{form.email}</strong>. Mã có hiệu lực trong 10 phút.</>
            )}
          </p>

          <div aria-live="polite">
            {error ? <div className="mt-5 rounded-md border border-error/20 bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
            {message ? <div className="mt-5 rounded-md border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}
          </div>

          {step === 'register' ? (
            <form className="mt-6 grid gap-4" onSubmit={handleRegister}>
              <Field label="Họ và tên" value={form.fullName} onChange={(e) => update('fullName', e.target.value)} placeholder="Nguyễn Văn A" autoComplete="name" required />
              <Field label="Email" value={form.email} onChange={(e) => update('email', e.target.value)} type="email" placeholder="email@example.com" autoComplete="email" required />
              <Field label="Số điện thoại" value={form.phone} onChange={(e) => update('phone', e.target.value)} placeholder="0901234567" autoComplete="tel" />
              <Field label="Địa chỉ" value={form.address} onChange={(e) => update('address', e.target.value)} placeholder="Số nhà, đường, phường/xã..." autoComplete="street-address" />
              <PasswordField label="Mật khẩu" value={form.password} onChange={(e) => update('password', e.target.value)} />
              <PasswordField label="Xác nhận mật khẩu" value={form.confirmPassword} onChange={(e) => update('confirmPassword', e.target.value)} />
              <p className="-mt-2 text-xs font-medium leading-5 text-on-surface-variant">{PASSWORD_REQUIREMENTS_MESSAGE}</p>
              <label className="flex items-start gap-3 text-sm leading-6 text-on-surface-variant">
                <input checked={accepted} onChange={(e) => setAccepted(e.target.checked)} type="checkbox" className="mt-1 rounded border-outline-variant" />
                Tôi đồng ý với điều khoản sử dụng và chính sách bảo mật của Nhà Sách Tri Thức.
              </label>
              <AccentButton disabled={loading} type="submit">{loading ? 'Đang gửi mã xác nhận...' : 'Đăng ký tài khoản'}</AccentButton>
              <SecondaryButton disabled={loading} type="button" onClick={continueWithExistingCode}>Tôi đã có mã xác nhận</SecondaryButton>
            </form>
          ) : (
            <form className="mt-7" onSubmit={handleVerify}>
              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-on-surface">Mã xác nhận</span>
                <input value={verificationCode} onChange={(e) => setVerificationCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                  inputMode="numeric" autoComplete="one-time-code" autoFocus aria-describedby="verification-help" placeholder="000000"
                  className="h-14 w-full rounded-md border border-border-strong bg-surface px-4 text-center text-2xl font-bold tracking-[0.35em] text-primary outline-none transition placeholder:text-text-muted focus:border-primary focus:ring-2 focus:ring-primary/15" />
              </label>
              <p id="verification-help" className="mt-2 text-xs leading-5 text-on-surface-variant">Vì lý do bảo mật, mã chỉ được nhập sai tối đa 5 lần.</p>
              <AccentButton disabled={loading || verificationCode.length !== 6} type="submit" className="mt-5 w-full">
                {loading ? 'Đang xác nhận...' : 'Xác nhận tài khoản'}
              </AccentButton>
              <div className="mt-3">
                <SecondaryButton disabled={loading || resendSeconds > 0} type="button" onClick={handleResend} className="w-full">
                  {resendSeconds > 0 ? `Gửi lại sau ${resendSeconds}s` : 'Gửi lại mã'}
                </SecondaryButton>
              </div>
            </form>
          )}

          <p className="mt-6 text-center text-sm text-on-surface-variant">Đã có tài khoản?{' '}
            <Link to="/login" className="font-bold text-secondary hover:underline">Đăng nhập</Link>
          </p>
        </div>
      </main>
      <aside className="hidden bg-surface-container lg:block">
        <img src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=1200&q=80"
          alt="Không gian thư viện" className="h-full w-full object-cover" />
      </aside>
    </div>
  );
};

export default RegisterPage;
