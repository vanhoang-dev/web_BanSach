import { Link } from 'react-router-dom';
import { PageShell, Panel, SectionHeading } from './staticUi';

const CtaLink = ({ to, children, className = '' }) => (
    <Link
        to={to}
        className={`inline-flex items-center justify-center gap-unit rounded-lg bg-primary px-4 py-2 font-label-md text-label-md text-on-primary shadow-sm transition-colors hover:bg-primary-container ${className}`}
    >
        {children}
    </Link>
);

const categories = [
    { title: 'Kinh tế - Tài chính', count: 24, tone: 'from-primary to-secondary' },
    { title: 'Công nghệ', count: 18, tone: 'from-secondary to-secondary-container' },
    { title: 'Tâm lý - Kỹ năng', count: 31, tone: 'from-tertiary to-tertiary-container' },
    { title: 'Văn học', count: 29, tone: 'from-primary-container to-primary' },
    { title: 'Khoa học', count: 16, tone: 'from-surface-tint to-primary' },
    { title: 'Thiếu nhi', count: 12, tone: 'from-secondary-fixed-dim to-secondary' },
];

const CategoriesPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading
                eyebrow="Danh mục"
                title="Khám phá các nhóm sách"
                description="Trang danh mục tĩnh để người dùng bấm từ menu 'Danh mục' và xem các nhóm chủ đề nổi bật."
            />

            <div className="grid gap-gutter sm:grid-cols-2 xl:grid-cols-3">
                {categories.map((category) => (
                    <Panel key={category.title} className="overflow-hidden">
                        <div className={`h-40 bg-gradient-to-br ${category.tone} p-stack-md flex flex-col justify-between text-on-primary`}>
                            <span className="font-caption text-caption uppercase tracking-wider">BookStore</span>
                            <div>
                                <h3 className="font-h3 text-h3">{category.title}</h3>
                                <p className="font-body-md text-body-md text-on-primary/80">{category.count} đầu sách nổi bật</p>
                            </div>
                        </div>
                        <div className="p-stack-md flex items-center justify-between gap-4">
                            <p className="font-body-md text-body-md text-on-surface-variant">Chọn để xem các sách thuộc chủ đề này.</p>
                            <CtaLink to="/catalog">Xem</CtaLink>
                        </div>
                    </Panel>
                ))}
            </div>
        </div>
    </PageShell>
);

export default CategoriesPage;
