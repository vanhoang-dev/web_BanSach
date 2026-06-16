import { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { Container, Field, Icon, Panel, PrimaryButton, SecondaryButton, SectionHeading } from '@/components/ui/staticUi';
import authService from '@/features/auth/services';

const ProfilePage = () => {
  const [profile, setProfile] = useState({ fullName: '', email: '', phone: '', address: '' });
  const [password, setPassword] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const data = await authService.getProfile();
        setProfile({
          fullName: data?.fullName || '',
          email: data?.email || '',
          phone: data?.phone || '',
          address: data?.address || '',
        });
      } catch {
        setError('Không thể tải hồ sơ tài khoản.');
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const updateProfile = (key: string, value: string) => setProfile((current) => ({ ...current, [key]: value }));
  const updatePassword = (key: string, value: string) => setPassword((current) => ({ ...current, [key]: value }));

  const handleSaveProfile = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');
    try {
      setSaving(true);
      const data = await authService.updateProfile({
        fullName: profile.fullName,
        phone: profile.phone,
        address: profile.address,
      });
      setProfile((current) => ({ ...current, ...data }));
      setMessage('Đã lưu thông tin hồ sơ.');
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Không thể lưu hồ sơ.');
    } finally {
      setSaving(false);
    }
  };

  const handleChangePassword = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');
    if (password.newPassword !== password.confirmPassword) {
      setError('Mật khẩu xác nhận chưa khớp.');
      return;
    }
    try {
      setSaving(true);
      await authService.changePassword(password);
      setPassword({ currentPassword: '', newPassword: '', confirmPassword: '' });
      setShowPasswordForm(false);
      setMessage('Đã đổi mật khẩu thành công.');
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Không thể đổi mật khẩu.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Tài khoản" title="Hồ sơ của tôi" description="Quản lý thông tin cá nhân, địa chỉ mặc định và bảo mật tài khoản." />
      {error ? <div className="mb-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      {message ? <div className="mb-5 rounded-lg bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div> : null}
      <div className="grid gap-6 lg:grid-cols-[300px_1fr]">
        <Panel className="h-fit p-5">
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-lg bg-primary/5 text-primary"><Icon name="user" /></div>
            <div>
              <p className="text-lg font-bold text-primary">{profile.fullName || 'Khách hàng'}</p>
              <p className="text-sm text-on-surface-variant">{profile.email || 'email đang cập nhật'}</p>
            </div>
          </div>
          <nav className="mt-6 space-y-2">
            {[
              ['Thông tin cá nhân', '/profile'],
              ['Đơn hàng của tôi', '/orders'],
              ['Sách yêu thích', '/wishlist'],
              ['Giỏ hàng', '/cart'],
            ].map(([label, to], index) => (
              <Link key={to} to={to} className={`block rounded-lg px-3 py-3 text-sm font-bold ${index === 0 ? 'bg-primary text-on-primary' : 'text-on-surface-variant hover:bg-surface-container-low'}`}>{label}</Link>
            ))}
          </nav>
        </Panel>

        <div className="space-y-6">
          <Panel className="p-5">
            <h2 className="text-lg font-bold text-primary">Thông tin cá nhân</h2>
            {loading ? (
              <div className="mt-5 h-56 animate-pulse rounded-lg bg-surface-container" />
            ) : (
              <form onSubmit={handleSaveProfile}>
                <div className="mt-5 grid gap-4 md:grid-cols-2">
                  <Field label="Họ và tên" value={profile.fullName} onChange={(e) => updateProfile('fullName', e.target.value)} required />
                  <Field label="Số điện thoại" value={profile.phone} onChange={(e) => updateProfile('phone', e.target.value)} placeholder="0901234567" />
                  <Field label="Email" value={profile.email} type="email" disabled />
                </div>
                <Field className="mt-4" label="Địa chỉ mặc định" value={profile.address} onChange={(e) => updateProfile('address', e.target.value)} placeholder="Số nhà, đường, phường/xã..." textarea />
                <div className="mt-6 flex flex-wrap gap-3">
                  <PrimaryButton disabled={saving} type="submit"><Icon name="edit" /> {saving ? 'Đang lưu...' : 'Lưu thay đổi'}</PrimaryButton>
                  <SecondaryButton onClick={() => setShowPasswordForm((value) => !value)}>{showPasswordForm ? 'Đóng' : 'Đổi mật khẩu'}</SecondaryButton>
                </div>
              </form>
            )}
          </Panel>

          {showPasswordForm ? (
            <Panel className="p-5">
              <h2 className="text-lg font-bold text-primary">Đổi mật khẩu</h2>
              <form className="mt-5 grid gap-4 md:grid-cols-3" onSubmit={handleChangePassword}>
                <Field label="Mật khẩu hiện tại" value={password.currentPassword} onChange={(e) => updatePassword('currentPassword', e.target.value)} type="password" required />
                <Field label="Mật khẩu mới" value={password.newPassword} onChange={(e) => updatePassword('newPassword', e.target.value)} type="password" required />
                <Field label="Xác nhận" value={password.confirmPassword} onChange={(e) => updatePassword('confirmPassword', e.target.value)} type="password" required />
                <PrimaryButton disabled={saving} className="md:col-span-3" type="submit">{saving ? 'Đang cập nhật...' : 'Cập nhật mật khẩu'}</PrimaryButton>
              </form>
            </Panel>
          ) : null}
        </div>
      </div>
    </Container>
  );
};

export default ProfilePage;
