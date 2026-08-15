import ErrorPageLayout from '@/features/errors/components/ErrorPageLayout';

const ForbiddenPage = () => (
  <ErrorPageLayout content={{
    code: '403',
    eyebrow: 'Không có quyền',
    title: 'Bạn không được phép truy cập trang này',
    description: 'Tài khoản hiện tại chưa có quyền sử dụng khu vực hoặc chức năng bạn vừa mở.',
    tone: 'danger',
    hints: ['Nếu bạn là quản trị viên, hãy đăng nhập bằng tài khoản admin.', 'Bạn vẫn có thể quay lại cửa hàng để tiếp tục mua sắm.'],
    primaryLabel: 'Về trang chủ',
    primaryTo: '/',
    secondaryLabel: 'Đăng nhập tài khoản khác',
    secondaryTo: '/login',
  }} />
);

export default ForbiddenPage;
