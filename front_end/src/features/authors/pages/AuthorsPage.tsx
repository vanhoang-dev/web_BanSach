import { Link } from 'react-router-dom';
import { PageShell, Panel, SectionHeading } from '@/components/ui/staticUi';

const CtaLink = ({ to, children, className = '' }) => (
    <Link
        to={to}
        className={`inline-flex items-center justify-center gap-unit rounded-lg bg-primary px-4 py-2 font-label-md text-label-md text-on-primary shadow-sm transition-colors hover:bg-primary-container ${className}`}
    >
        {children}
    </Link>
);

const authors = [
    { name: 'Yuval Noah Harari', books: 8, role: 'Lịch sử - Xã hội' },
    { name: 'Dale Carnegie', books: 5, role: 'Kỹ năng sống' },
    { name: 'James Clear', books: 3, role: 'Thói quen' },
    { name: 'Paulo Coelho', books: 11, role: 'Văn học' },
    { name: 'Daniel Kahneman', books: 4, role: 'Tư duy' },
    { name: 'Eric Ries', books: 2, role: 'Khởi nghiệp' },
];

const AuthorsPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading
                eyebrow="Tác giả"
                title="Tác giả nổi bật"
                description="Trang tác giả tĩnh để menu 'Tác giả' luôn có đích đến hợp lý và người dùng có thể khám phá theo tên tác giả."
            />

            <div className="grid gap-gutter md:grid-cols-2 xl:grid-cols-3">
                {authors.map((author) => (
                    <Panel key={author.name} className="p-stack-md flex flex-col gap-stack-md">
                        <div className="flex items-start gap-stack-md">
                            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-primary to-secondary-container shrink-0" />
                            <div>
                                <h3 className="font-body-lg text-body-lg text-primary font-bold">{author.name}</h3>
                                <p className="font-caption text-caption text-on-surface-variant mt-unit">{author.role}</p>
                            </div>
                        </div>
                        <div className="flex items-center justify-between">
                            <span className="font-label-md text-label-md text-secondary-container font-bold">{author.books} sách</span>
                            <CtaLink to="/catalog">Xem sách</CtaLink>
                        </div>
                    </Panel>
                ))}
            </div>
        </div>
    </PageShell>
);

export default AuthorsPage;
