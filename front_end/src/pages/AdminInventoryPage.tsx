import { PageShell, Panel, SectionHeading } from './staticUi';

const AdminInventoryPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Quản trị" title="Quản lý kho hàng" description="Trang kho hàng tĩnh với thẻ thống kê và bảng tồn kho." />
            <div className="grid gap-gutter md:grid-cols-3 mb-stack-lg">
                {[['Tổng nhập', '12,400'], ['Đang lưu kho', '8,120'], ['Cần bổ sung', '220']].map(([label, value]) => <Panel key={label} className="p-stack-md"><p className="font-caption text-caption text-on-surface-variant">{label}</p><h3 className="font-h2 text-h2 text-primary mt-unit">{value}</h3></Panel>)}
            </div>
            <Panel className="p-stack-lg overflow-hidden">
                <table className="w-full"><thead className="bg-surface-container"><tr>{['Mã', 'Sách', 'Tồn kho', 'Kệ', 'Cảnh báo'].map((head) => <th key={head} className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">{head}</th>)}</tr></thead><tbody>{[['BK-001', 'Nghệ Thuật Lãnh Đạo', 120, 'A1', 'OK'], ['BK-002', 'Sapiens', 18, 'B2', 'Thấp'], ['BK-003', 'Atomic Habits', 4, 'C3', 'Cần nhập']].map((row) => <tr key={row[0]} className="border-t border-surface-variant"><td className="px-stack-lg py-stack-md font-bold text-primary">{row[0]}</td><td className="px-stack-lg py-stack-md">{row[1]}</td><td className="px-stack-lg py-stack-md">{row[2]}</td><td className="px-stack-lg py-stack-md">{row[3]}</td><td className="px-stack-lg py-stack-md">{row[4]}</td></tr>)}</tbody></table>
            </Panel>
        </div>
    </PageShell>
);

export default AdminInventoryPage;
