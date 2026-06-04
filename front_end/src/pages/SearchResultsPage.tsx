import { PageShell, Panel, SectionHeading, Icon, PrimaryButton } from './staticUi';

const SearchResultsPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading
                eyebrow="Tìm kiếm"
                title="Kết quả tìm kiếm"
                description="Giao diện tĩnh cho trang tìm kiếm, có thể dùng cho cả trạng thái có kết quả và không có kết quả."
            />

            <Panel className="p-stack-md mb-stack-lg">
                <div className="relative">
                    <input className="w-full h-12 rounded-full border border-outline-variant bg-surface-container-low pl-4 pr-10 font-body-md text-body-md" placeholder="Tìm kiếm sách, tác giả..." />
                    <span className="absolute right-4 top-3 text-on-surface-variant">{Icon({ name: 'search' })}</span>
                </div>
            </Panel>

            <div className="grid gap-gutter lg:grid-cols-3">
                <div className="lg:col-span-2 grid gap-stack-md">
                    {['Tư Duy Nhanh Và Chậm', 'Sapiens', 'Nhà Giả Kim'].map((title) => (
                        <Panel key={title} className="p-stack-md flex gap-stack-md">
                            <div className="w-24 h-32 rounded-lg bg-gradient-to-br from-primary to-secondary-container shrink-0" />
                            <div className="flex-1">
                                <h3 className="font-body-lg text-body-lg text-primary font-bold">{title}</h3>
                                <p className="font-caption text-caption text-on-surface-variant mt-unit">Tác giả nổi bật • Nhà xuất bản uy tín</p>
                                <div className="mt-stack-md flex items-center justify-between">
                                    <span className="font-label-md text-label-md text-secondary-container font-bold">185.000 ₫</span>
                                    <PrimaryButton className="px-4 py-2">{Icon({ name: 'cart' })}Thêm</PrimaryButton>
                                </div>
                            </div>
                        </Panel>
                    ))}
                </div>

                <Panel className="p-stack-lg">
                    <h3 className="font-h3 text-h3 text-primary mb-stack-md">Không tìm thấy?</h3>
                    <p className="font-body-md text-body-md text-on-surface-variant mb-stack-md">Thử từ khóa khác hoặc xem các danh mục nổi bật bên dưới.</p>
                    <div className="space-y-stack-sm">
                        {['Kinh doanh', 'Kỹ năng', 'Học thuật'].map((item) => (
                            <div key={item} className="flex items-center justify-between rounded-lg bg-surface-container-low px-4 py-3">
                                <span className="font-label-md text-label-md text-on-surface">{item}</span>
                                {Icon({ name: 'arrow', className: 'w-4 h-4' })}
                            </div>
                        ))}
                    </div>
                </Panel>
            </div>
        </div>
    </PageShell>
);

export default SearchResultsPage;
