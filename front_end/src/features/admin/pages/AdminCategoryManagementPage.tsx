
const AdminCategoryManagementPage = () => {
    const categories = [
        { name: 'Kinh tế', books: 24, status: 'Đang hoạt động' },
        { name: 'Công nghệ', books: 18, status: 'Đang hoạt động' },
        { name: 'Tâm lý', books: 31, status: 'Đang hoạt động' },
        { name: 'Văn học', books: 29, status: 'Đang hoạt động' },
    ];

    return (
        <div className="w-full">
            <div className="mb-section-gap">
                <h1 className="font-h1 text-h1 text-primary mb-unit">Quản lý danh mục</h1>
                <p className="font-body-md text-body-md text-on-surface-variant">Theo dõi và tổ chức các nhóm sách trong hệ thống quản trị.</p>
            </div>

            <div className="bg-surface-container-lowest rounded-xl border border-surface-variant overflow-hidden">
                <div className="p-stack-lg border-b border-surface-variant flex items-center justify-between gap-4">
                    <h2 className="font-h2 text-h2 text-primary">Danh mục hiện có</h2>
                    <button className="inline-flex items-center justify-center rounded-lg bg-primary px-4 py-2 font-label-md text-label-md text-on-primary hover:bg-primary-container transition-colors">
                        Thêm danh mục
                    </button>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-surface-container border-b border-surface-variant">
                            <tr>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Tên danh mục</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Số sách</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Trạng thái</th>
                                <th className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {categories.map((category) => (
                                <tr key={category.name} className="border-b border-surface-variant hover:bg-surface-container-low transition-colors">
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-primary font-bold">{category.name}</td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface">{category.books}</td>
                                    <td className="px-stack-lg py-stack-md font-body-md text-body-md text-on-surface">{category.status}</td>
                                    <td className="px-stack-lg py-stack-md">
                                        <div className="flex gap-stack-sm">
                                            <button className="text-primary font-label-md text-label-md hover:underline">Sửa</button>
                                            <button className="text-error font-label-md text-label-md hover:underline">Xóa</button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminCategoryManagementPage;
