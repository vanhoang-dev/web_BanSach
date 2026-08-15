import ErrorPageLayout from '@/features/errors/components/ErrorPageLayout';

const ServerErrorPage = () => (
  <ErrorPageLayout content={{
    code: '500',
    eyebrow: 'Lỗi hệ thống',
    title: 'Hệ thống đang gặp sự cố',
    description: 'Máy chủ chưa thể xử lý yêu cầu lúc này. Bạn có thể thử lại sau ít phút.',
    tone: 'danger',
    hints: ['Đơn hàng và dữ liệu tài khoản của bạn vẫn được bảo toàn.', 'Nếu lỗi lặp lại, hãy quay lại sau hoặc liên hệ quản trị viên.'],
    primaryLabel: 'Thử lại',
    allowReload: true,
    secondaryLabel: 'Về trang chủ',
    secondaryTo: '/',
  }} />
);

export default ServerErrorPage;
