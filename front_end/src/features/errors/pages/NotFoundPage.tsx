import ErrorPageLayout from '@/features/errors/components/ErrorPageLayout';

const NotFoundPage = () => (
  <ErrorPageLayout content={{
    code: '404',
    eyebrow: 'Không tìm thấy',
    title: 'Trang bạn tìm không tồn tại',
    description: 'Đường dẫn có thể đã thay đổi, bị xóa hoặc bạn nhập chưa đúng địa chỉ.',
    tone: 'neutral',
    hints: ['Kiểm tra lại đường dẫn trên thanh địa chỉ.', 'Dùng tìm kiếm hoặc quay về danh mục sách để tiếp tục.'],
    primaryLabel: 'Khám phá sách',
    primaryTo: '/catalog',
    secondaryLabel: 'Về trang chủ',
    secondaryTo: '/',
  }} />
);

export default NotFoundPage;
