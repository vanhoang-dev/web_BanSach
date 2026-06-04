import { Link } from 'react-router-dom';
import { Icon } from './staticUi';

const InputField = ({ label, icon, type, placeholder, id }) => (
    <div className="flex flex-col gap-unit">
        <label className="font-label-md text-label-md text-primary" htmlFor={id}>
            {label}
        </label>
        <div className="relative">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-outline-variant" aria-hidden="true">
                {Icon({ name: icon })}
            </span>
            <input
                id={id}
                name={id}
                type={type}
                placeholder={placeholder}
                className="w-full pl-10 pr-4 py-3 bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md text-on-surface focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-shadow shadow-[0_4px_12px_rgba(30,27,75,0.05)] focus:shadow-[0_8px_24px_rgba(30,27,75,0.1)]"
            />
        </div>
    </div>
);

const RegisterPage = () => (
    <div className="min-h-screen flex flex-col md:flex-row bg-surface text-on-surface antialiased">
        <main className="w-full md:w-1/2 lg:w-5/12 flex flex-col justify-center px-gutter py-stack-lg md:px-12 lg:px-24 bg-surface z-10 relative shadow-[12px_0_48px_rgba(30,27,75,0.05)]">
            <div className="mb-stack-lg">
                <Link className="inline-block mb-section-gap text-primary" to="/">
                    <span className="font-h3 text-h3 font-black tracking-tight">BookStore</span>
                </Link>
                <h1 className="font-h2 text-h2 text-primary mb-unit">Tạo tài khoản</h1>
                <p className="font-body-md text-body-md text-on-surface-variant">Tham gia nền tảng tri thức số hiện đại ngay hôm nay.</p>
            </div>

            <form className="space-y-stack-md">
                <InputField
                    label="Họ và tên"
                    icon="user"
                    type="text"
                    placeholder="Nguyễn Văn A"
                    id="fullname"
                />

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-stack-md">
                    <InputField
                        label="Email"
                        icon="mail"
                        type="email"
                        placeholder="email@example.com"
                        id="email"
                    />
                    <InputField
                        label="Số điện thoại"
                        icon="phone"
                        type="tel"
                        placeholder="0901234567"
                        id="phone"
                    />
                </div>

                <InputField
                    label="Mật khẩu"
                    icon="lock"
                    type="password"
                    placeholder="••••••••"
                    id="password"
                />

                <InputField
                    label="Xác nhận mật khẩu"
                    icon="shield"
                    type="password"
                    placeholder="••••••••"
                    id="confirm_password"
                />

                <div className="flex items-start gap-3 mt-stack-md">
                    <div className="flex items-center h-5">
                        <input className="w-4 h-4 rounded border-outline-variant text-primary focus:ring-primary bg-surface-container-lowest cursor-pointer mt-1" id="terms" name="terms" type="checkbox" />
                    </div>
                    <label className="font-caption text-caption text-on-surface-variant leading-relaxed" htmlFor="terms">
                        Tôi đồng ý với <Link className="text-primary font-bold hover:underline transition-all" to="/register">Điều khoản sử dụng</Link> và <Link className="text-primary font-bold hover:underline transition-all" to="/register">Chính sách bảo mật</Link> của BookStore.
                    </label>
                </div>

                <div className="pt-stack-sm">
                    <button
                        type="submit"
                        className="w-full flex justify-center items-center gap-2 py-3 px-6 bg-secondary-container hover:bg-[#e66a15] text-surface-container-lowest font-label-md text-label-md rounded-lg transition-colors shadow-[0_4px_12px_rgba(30,27,75,0.05)] hover:shadow-[0_8px_24px_rgba(30,27,75,0.1)]"
                    >
                        <span>Đăng ký tài khoản</span>
                        <span aria-hidden="true" className="text-sm">→</span>
                    </button>
                </div>
            </form>

            <div className="mt-section-gap text-center">
                <p className="font-body-md text-body-md text-on-surface-variant">
                    Đã có tài khoản?
                    <Link className="font-bold text-primary hover:text-secondary-container transition-colors ml-1" to="/login">
                        Đăng nhập
                    </Link>
                </p>
            </div>
        </main>

        <aside className="hidden md:block w-1/2 lg:w-7/12 relative bg-surface-dim overflow-hidden">
            <div className="absolute inset-0 bg-primary/5 z-10 pointer-events-none" aria-hidden="true" />
            <img
                alt="Library Interior"
                className="absolute inset-0 w-full h-full object-cover object-center z-0"
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuB0hYm6Od4KyQSkrdiBYuelFnzdMdr_kMoCAh8WMXry8SpHXkVW4tYWdU31ZWviHycERMr6PZQrRGPKxxUOXhC7EU_m1JqpgClfM32u5vpwBa3zAgtGW7Ob1NDpTwFfBNpKFWCyzXVpeWImLyb0JlsbHxZiHSse1ralHGxynXia6347eeNuE-dMyIB7kHch2kezcCge18xNb10BVP_Ozgh7n-7VyGQhdVLS_M4h4Y5SWzCYEHjuRzD2CmNH3LcQxHKrB33QQ_paO3k"
            />
        </aside>
    </div>
);

export default RegisterPage;
