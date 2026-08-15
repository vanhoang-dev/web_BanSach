import ErrorPageLayout from '@/features/errors/components/ErrorPageLayout';

const NetworkErrorPage = () => (
  <ErrorPageLayout content={{
    code: 'Mất kết nối',
    eyebrow: 'Không thể kết nối',
    title: 'Không kết nối được tới máy chủ',
    description: 'Trình duyệt chưa nhận được phản hồi từ hệ thống. Hãy kiểm tra mạng hoặc máy chủ backend.',
    tone: 'warning',
    hints: ['Đảm bảo backend đang chạy đúng cổng cấu hình.', 'Kiểm tra lại kết nối mạng rồi tải lại trang.'],
    primaryLabel: 'Tải lại trang',
    allowReload: true,
    secondaryLabel: 'Về trang chủ',
    secondaryTo: '/',
  }} />
);

export default NetworkErrorPage;
