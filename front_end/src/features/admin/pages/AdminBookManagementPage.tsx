import { PageShell, Panel, SectionHeading, Icon, PrimaryButton, SecondaryButton } from '@/components/ui/staticUi';

const AdminBookManagementPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Quản trị" title="Quản lý sách" description="Bố cục bảng quản trị tĩnh theo các file HTML admin: thống kê, tìm kiếm và bảng dữ liệu." action={<PrimaryButton>{Icon({ name: 'plus' })}Thêm sách</PrimaryButton>} />
            <div className="grid gap-gutter md:grid-cols-3 mb-stack-lg">
                {[['Tổng sách', '2,480'], ['Đang bán', '1,920'], ['Ẩn / xóa', '560']].map(([label, value]) => (
                    <Panel key={label} className="p-stack-md">
                        <p className="font-caption text-caption text-on-surface-variant">{label}</p>
                        <h3 className="font-h2 text-h2 text-primary mt-unit">{value}</h3>
                    </Panel>
                ))}
            </div>
            <Panel className="p-stack-lg overflow-hidden">
                <div className="flex flex-col md:flex-row gap-stack-md md:items-center md:justify-between mb-stack-md">
                    <div className="relative md:w-80">
                        <input className="w-full h-12 rounded-full border border-outline-variant bg-surface-container-low pl-4 pr-10" placeholder="Tìm kiếm sách..." />
                        <span className="absolute right-4 top-3 text-on-surface-variant">{Icon({ name: 'search' })}</span>
                    </div>
                    <SecondaryButton>{Icon({ name: 'chart' })}Xuất báo cáo</SecondaryButton>
                </div>
                <table className="w-full">
                    <thead className="bg-surface-container">
                        <tr>{['Tên sách', 'Tác giả', 'Giá', 'Tồn kho', 'Hành động'].map((head) => <th key={head} className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">{head}</th>)}</tr>
                    </thead>
                    <tbody>
                        {['Nghệ Thuật Lãnh Đạo', 'Sapiens', 'Atomic Habits'].map((title, index) => (
                            <tr key={title} className="border-t border-surface-variant">
                                <td className="px-stack-lg py-stack-md font-body-md text-primary font-bold">{title}</td>
                                <td className="px-stack-lg py-stack-md">Tác giả {index + 1}</td>
                                <td className="px-stack-lg py-stack-md">250.000 ₫</td>
                                <td className="px-stack-lg py-stack-md">{index === 0 ? 'Còn hàng' : 'Hết hàng'}</td>
                                <td className="px-stack-lg py-stack-md flex gap-stack-sm"><button>{Icon({ name: 'edit', className: 'w-4 h-4' })}</button><button>{Icon({ name: 'trash', className: 'w-4 h-4' })}</button></td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </Panel>
        </div>
    </PageShell>
);

export default AdminBookManagementPage;
