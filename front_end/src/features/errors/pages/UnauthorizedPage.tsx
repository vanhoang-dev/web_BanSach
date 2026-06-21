import ErrorPageLayout from '@/features/errors/components/ErrorPageLayout';

const UnauthorizedPage = () => (
  <ErrorPageLayout content={{
    code: '401',
    eyebrow: 'Yêu cầu đăng nhập',
    title: 'Bạn cần đăng nhập để tiếp tục',
    description: 'Phiên truy cập không hợp lệ hoặc đã hết hạn. Hãy đăng nhập lại để sử dụng các chức năng cá nhân.',
    tone: 'warning',
    hints: ['Kiểm tra lại tài khoản đăng nhập.', 'Sau khi đăng nhập, bạn có thể tiếp tục mua sách và theo dõi đơn hàng.'],
    primaryLabel: 'Đăng nhập',
    primaryTo: '/login',
    secondaryLabel: 'Về trang chủ',
    secondaryTo: '/',
  }} />
);

export default UnauthorizedPage;
