import { PageShell, Panel, Field, Icon, PrimaryButton } from '@/components/ui/staticUi';

const ForgotPasswordPage = () => (
    <PageShell>
        <div className="min-h-[calc(100vh-160px)] flex items-center justify-center px-gutter py-section-gap">
            <Panel className="w-full max-w-lg p-stack-lg lg:p-[48px] text-center">
                <div className="mx-auto w-14 h-14 rounded-full bg-primary/10 text-primary flex items-center justify-center mb-stack-md">{Icon({ name: 'lock', className: 'w-6 h-6' })}</div>
                <h1 className="font-h2 text-h2 text-primary mb-unit">Quên mật khẩu</h1>
                <p className="font-body-md text-body-md text-on-surface-variant mb-stack-lg">Nhập email của bạn để nhận liên kết đặt lại mật khẩu.</p>
                <div className="text-left space-y-stack-md">
                    <Field label="Email" placeholder="name@example.com" type="email" />
                    <PrimaryButton className="w-full">{Icon({ name: 'mail' })}Gửi liên kết</PrimaryButton>
                </div>
            </Panel>
        </div>
    </PageShell>
);

export default ForgotPasswordPage;
