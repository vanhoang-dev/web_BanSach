type AdminSectionHeaderProps = {
  title: string;
  description?: string;
};

const AdminSectionHeader = ({ title, description }: AdminSectionHeaderProps) => (
  <div className="mb-3 mt-7 flex min-w-0 flex-col gap-1 sm:flex-row sm:items-end sm:justify-between">
    <div className="min-w-0">
      <h2 className="text-lg font-bold leading-6 text-[#141414]">{title}</h2>
      {description ? <p className="mt-1 text-sm leading-5 text-[#777772]">{description}</p> : null}
    </div>
  </div>
);

export default AdminSectionHeader;
