import { PageShell, Panel, Field, SectionHeading, Icon, PrimaryButton, SecondaryButton } from './staticUi';

const ProfilePage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Tài khoản" title="Hồ sơ của tôi" description="Trang hồ sơ tĩnh theo phong cách trang thiết kế, có menu bên trái và nội dung chỉnh sửa bên phải." />
            <div className="grid gap-gutter lg:grid-cols-4">
                <Panel className="p-stack-lg">
                    <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary to-secondary-container mb-stack-md" />
                    <p className="font-h3 text-h3 text-primary">Nguyễn Văn A</p>
                    <p className="font-body-md text-body-md text-on-surface-variant mb-stack-md">member@bookstore.vn</p>
                    <div className="space-y-stack-sm">
                        {['Thông tin cá nhân', 'Đơn hàng của tôi', 'Yêu thích', 'Địa chỉ'].map((item, index) => (
                            <div key={item} className={`rounded-lg px-4 py-3 ${index === 0 ? 'bg-primary text-on-primary' : 'bg-surface-container-low text-on-surface'}`}>{item}</div>
                        ))}
                    </div>
                </Panel>

                <Panel className="p-stack-lg lg:col-span-3">
                    <h3 className="font-h3 text-h3 text-primary mb-stack-md">Chỉnh sửa thông tin</h3>
                    <div className="grid gap-stack-md md:grid-cols-2">
                        <Field label="Họ và tên" placeholder="Nguyễn Văn A" />
                        <Field label="Số điện thoại" placeholder="0901234567" />
                        <Field label="Email" placeholder="name@example.com" type="email" />
                        <Field label="Ngày sinh" placeholder="01/01/1995" type="date" />
                    </div>
                    <div className="mt-stack-md">
                        <Field label="Địa chỉ mặc định" placeholder="Số nhà, đường, phường/xã..." textarea />
                    </div>
                    <div className="mt-stack-lg flex flex-wrap gap-stack-md">
                        <PrimaryButton>{Icon({ name: 'edit' })}Lưu thay đổi</PrimaryButton>
                        <SecondaryButton>Đổi mật khẩu</SecondaryButton>
                    </div>
                </Panel>
            </div>
        </div>
    </PageShell>
);

export default ProfilePage;
