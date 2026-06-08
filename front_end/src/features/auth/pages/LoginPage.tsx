import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { Icon } from '@/components/ui/staticUi';
import { useAuth } from '@/hooks/useAuth';

const InputField = ({ label, icon, type, placeholder, id, value, onChange, disabled = false }: any) => (
    <div className="flex flex-col gap-unit">
        <label className="font-label-md text-label-md text-on-surface" htmlFor={id}>
            {label}
        </label>
        <div className="relative">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-outline-variant" aria-hidden="true">
                {Icon({ name: icon })}
            </span>
            <input
                id={id}
                type={type}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                disabled={disabled}
                className="w-full bg-surface-container-lowest border border-outline-variant rounded-lg py-3 pl-10 pr-4 font-body-md text-body-md text-on-surface focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
            />
        </div>
    </div>
);

const SocialButton = ({ label, children }: any) => (
    <button
        type="button"
        className="flex items-center justify-center gap-2 border border-outline-variant rounded-lg py-2.5 bg-surface-container-lowest hover:bg-surface-container transition-colors shadow-sm"
    >
        {children}
        <span className="font-label-md text-label-md text-on-surface">{label}</span>
    </button>
);

const LoginPage = () => {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email || !password) {
            setError('Vui lòng nhập email và mật khẩu');
            return;
        }
        try {
            setLoading(true);
            setError('');
            await login(email, password);
            navigate('/');
        } catch (err: any) {
            setError(err.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex flex-col lg:flex-row bg-surface text-on-surface antialiased">
            <aside className="hidden lg:block lg:w-1/2 relative bg-surface-container-high overflow-hidden">
                <div
                    className="absolute inset-0 bg-cover bg-center"
                    style={{ backgroundImage: "url('https://lh3.googleusercontent.com/aida-public/AB6AXuD42a9hsERoys49CY-Xu1aGOgZe9OgaxUvUMt-C4E0kjO9hAwMYwah587BcxRONlmsKKML2EraEfWA7_u0GkpGpNXM1avywghauajVFJuBs2x2KYqTbovKlWovKLlEZPkK8_7BwMGZbipEzUgl7iGuqCx6g3quBdzUs1UgZOkVyfWsc4yTNfUE7NSJlTnOJe_P8J-x0uIe81nJdRiVWjgchafEa4ICNazLLEpvzbQKE988IZkWDYAKEp4v-w0PGm7HIZdRGmshkkrM')" }}
                    aria-hidden="true"
                />
                <div className="absolute inset-0 bg-primary/10" aria-hidden="true" />
            </aside>

            <main className="w-full lg:w-1/2 flex flex-col justify-center items-center px-gutter py-section-gap overflow-y-auto">
                <div className="w-full max-w-md">
                    <div className="text-center mb-stack-lg">
                        <Link to="/" className="inline-block text-primary">
                            <span className="font-h2 text-h2 font-black tracking-tight">BookStore</span>
                        </Link>
                        <p className="font-body-md text-body-md text-on-surface-variant mt-unit">Nền tảng tri thức số hiện đại</p>
                    </div>

                    <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_12px_rgba(30,27,75,0.05)] p-8">
                        <div className="mb-stack-md text-center">
                            <h1 className="font-h3 text-h3 text-on-surface">Đăng nhập</h1>
                            <p className="font-body-md text-body-md text-on-surface-variant mt-unit">Vui lòng nhập thông tin để tiếp tục</p>
                        </div>

                        {error && (
                            <div className="bg-error/10 border border-error rounded-lg p-3 mb-stack-md">
                                <p className="font-body-md text-error">{error}</p>
                            </div>
                        )}

                        <form className="flex flex-col gap-stack-md" onSubmit={handleLogin}>
                            <InputField
                                label="Email"
                                icon="mail"
                                type="email"
                                placeholder="Nhập địa chỉ email"
                                id="email"
                                value={email}
                                onChange={(e: any) => setEmail(e.target.value)}
                                disabled={loading}
                            />
                            <InputField
                                label="Mật khẩu"
                                icon="lock"
                                type="password"
                                placeholder="Nhập mật khẩu"
                                id="password"
                                value={password}
                                onChange={(e: any) => setPassword(e.target.value)}
                                disabled={loading}
                            />

                            <div className="flex justify-between items-center mt-unit gap-4">
                                <label className="flex items-center gap-2 cursor-pointer group">
                                    <input
                                        className="w-4 h-4 rounded border-outline-variant text-primary focus:ring-primary focus:ring-offset-0 bg-surface-container-lowest cursor-pointer"
                                        type="checkbox"
                                        disabled={loading}
                                    />
                                    <span className="font-body-md text-body-md text-on-surface-variant group-hover:text-primary transition-colors">
                                        Ghi nhớ đăng nhập
                                    </span>
                                </label>
                                <Link
                                    to="/forgot-password"
                                    className="font-label-md text-label-md text-secondary hover:underline transition-all"
                                >
                                    Quên mật khẩu?
                                </Link>
                            </div>

                            <button
                                type="submit"
                                disabled={loading}
                                className="w-full bg-secondary text-on-secondary rounded-lg font-label-md text-label-md py-3 mt-stack-sm hover:bg-secondary-container hover:text-on-secondary-container transition-colors shadow-[0px_4px_12px_rgba(30,27,75,0.05)] hover:shadow-[0px_8px_24px_rgba(30,27,75,0.1)] flex justify-center items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                                {!loading && <span aria-hidden="true" className="text-sm">→</span>}
                            </button>
                        </form>

                        <div className="flex items-center gap-4 my-stack-md">
                            <div className="h-px bg-outline-variant flex-1" />
                            <span className="font-caption text-caption text-outline">Hoặc</span>
                            <div className="h-px bg-outline-variant flex-1" />
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            <SocialButton label="Google">
                                <img
                                    alt="Google"
                                    className="w-5 h-5"
                                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuAmDevPqjf2rkn2TreImtA4AetcQ0-82HhIGCblTZ1ra881eW3dHNogbIM5uo3hVrf0VunkN5fzpEC6sJyabtZOrG2ziZoH4vpGZXeaSmZAYPWXJugM71Q7_tlkM68dBnpKyUIQi0lAQiV2eIh36S39yWuLBJu2MLvEa1-Kl3bu_ikf1hRqLtUA8Vx4-bmbDucaKjN90JRipbYDHjRN2pdYw9Qp5Ot5hJqafzmR--2vx5cJOZ6Uk8lDr8pzJSbhZ4e0Up0Mj4QnPPg"
                                />
                            </SocialButton>
                            <SocialButton label="Facebook">
                                <img
                                    alt="Facebook"
                                    className="w-5 h-5"
                                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuBJ4SnuT5rCyg6h2lBGMCZTHVSTDpHXFr9ULXy9b8xrYpUn6kLsDJ5iZuisils0Ee38Gk3YZYRu4vTOnPUtxmecllMDH_ZgYLo43j2vKyCMQZPgaA01cnRU7elf3rPk6SHqtUjZzOx-7jpaUxlILsKWtmyPAIteOb-cxyzl-sKeLT_4t6VKGQh-untxsYnHJejGE8m0lJt17CEY56TBzYh_gvv-4IbD_y_FF_ni2_aI6UYIEe8Az4TkW03dCj2y7Xy1se_qECFQrjOM"
                                />
                            </SocialButton>
                        </div>
                    </div>

                    <div className="text-center mt-stack-lg">
                        <p className="font-body-md text-body-md text-on-surface-variant">
                            Chưa có tài khoản?
                            <Link to="/register" className="font-label-md text-label-md text-primary hover:text-secondary hover:underline transition-colors ml-1">
                                Đăng ký ngay
                            </Link>
                        </p>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default LoginPage;
