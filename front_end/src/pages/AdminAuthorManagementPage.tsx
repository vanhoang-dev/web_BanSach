import { PageShell, Panel, SectionHeading, Icon, PrimaryButton } from './staticUi';

const AdminAuthorManagementPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Quản trị" title="Quản lý tác giả" description="Trang tĩnh cho tác giả với lưới card và nút thao tác." action={<PrimaryButton>{Icon({ name: 'plus' })}Thêm tác giả</PrimaryButton>} />
            <div className="grid gap-gutter md:grid-cols-2 xl:grid-cols-3">
                {['John Maxwell', 'Daniel Kahneman', 'Yuval Noah Harari'].map((name) => (
                    <Panel key={name} className="p-stack-lg flex items-center gap-stack-md">
                        <div className="w-16 h-16 rounded-full bg-gradient-to-br from-primary to-secondary-container" />
                        <div>
                            <h3 className="font-h3 text-h3 text-primary">{name}</h3>
                            <p className="font-caption text-caption text-on-surface-variant">Tác giả nổi bật trong hệ thống</p>
                        </div>
                    </Panel>
                ))}
            </div>
        </div>
    </PageShell>
);

export default AdminAuthorManagementPage;
