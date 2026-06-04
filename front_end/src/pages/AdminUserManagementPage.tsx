import { PageShell, Panel, SectionHeading, Icon, PrimaryButton } from './staticUi';

const AdminUserManagementPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Quản trị" title="Quản lý người dùng" description="Màn hình tĩnh cho danh sách người dùng, tìm kiếm và thao tác nhanh." action={<PrimaryButton>{Icon({ name: 'plus' })}Thêm người dùng</PrimaryButton>} />
            <Panel className="p-stack-lg overflow-hidden">
                <table className="w-full">
                    <thead className="bg-surface-container"><tr>{['Tên', 'Email', 'Vai trò', 'Trạng thái'].map((head) => <th key={head} className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">{head}</th>)}</tr></thead>
                    <tbody>{['Nguyễn Văn A', 'Trần Thị B', 'Lê Văn C'].map((name, index) => (<tr key={name} className="border-t border-surface-variant"><td className="px-stack-lg py-stack-md font-body-md text-primary font-bold">{name}</td><td className="px-stack-lg py-stack-md">user{index + 1}@mail.vn</td><td className="px-stack-lg py-stack-md">{index === 0 ? 'Admin' : 'Khách hàng'}</td><td className="px-stack-lg py-stack-md">Đang hoạt động</td></tr>))}</tbody>
                </table>
            </Panel>
        </div>
    </PageShell>
);

export default AdminUserManagementPage;
