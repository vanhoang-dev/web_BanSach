import { PageShell, Panel, SectionHeading } from './staticUi';

const AdminRefundPage = () => (
    <PageShell>
        <div className="max-w-container-max mx-auto px-gutter py-section-gap">
            <SectionHeading eyebrow="Quản trị" title="Hoàn tiền / xử lý yêu cầu" description="Màn hình quản trị tĩnh cho danh sách yêu cầu hoàn tiền." />
            <Panel className="overflow-hidden">
                <table className="w-full"><thead className="bg-surface-container"><tr>{['Mã yêu cầu', 'Khách hàng', 'Số tiền', 'Trạng thái'].map((head) => <th key={head} className="px-stack-lg py-stack-md text-left font-label-md text-label-md text-on-surface-variant">{head}</th>)}</tr></thead><tbody>{[['RF-001', 'Nguyễn Văn A', '250.000 ₫', 'Chờ duyệt'], ['RF-002', 'Trần Thị B', '180.000 ₫', 'Đã hoàn tiền']].map((row) => <tr key={row[0]} className="border-t border-surface-variant"><td className="px-stack-lg py-stack-md font-bold text-primary">{row[0]}</td><td className="px-stack-lg py-stack-md">{row[1]}</td><td className="px-stack-lg py-stack-md">{row[2]}</td><td className="px-stack-lg py-stack-md">{row[3]}</td></tr>)}</tbody></table>
            </Panel>
        </div>
    </PageShell>
);

export default AdminRefundPage;
