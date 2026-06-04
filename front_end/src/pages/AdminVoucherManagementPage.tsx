import { PageShell, Panel, SectionHeading, Icon, PrimaryButton } from './staticUi';

const AdminVoucherManagementPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Quản trị" title="Quản lý voucher" description="Danh sách voucher tĩnh theo đúng kiểu dashboard quản trị." action={<PrimaryButton>{Icon({ name: 'plus' })}Tạo voucher</PrimaryButton>} />
            <div className="grid gap-gutter md:grid-cols-2 xl:grid-cols-3">
                {['SALE50', 'FREESHIP', 'WELCOME20'].map((code) => (
                    <Panel key={code} className="p-stack-lg">
                        <p className="font-caption text-caption text-on-surface-variant">Mã</p>
                        <h3 className="font-h2 text-h2 text-primary">{code}</h3>
                        <p className="mt-unit text-body-md">Giảm giá dành cho đơn hàng đủ điều kiện.</p>
                    </Panel>
                ))}
            </div>
        </div>
    </PageShell>
);

export default AdminVoucherManagementPage;
