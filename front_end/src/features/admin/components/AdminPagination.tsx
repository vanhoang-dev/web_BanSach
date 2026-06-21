import { Icon } from '@/components/ui/staticUi';

type AdminPaginationProps = {
  page: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  onPageChange: (page: number) => void;
};

const getVisiblePages = (page: number, totalPages: number) => {
  const start = Math.max(0, Math.min(page - 2, totalPages - 5));
  const end = Math.min(totalPages, start + 5);
  return Array.from({ length: Math.max(0, end - start) }, (_, index) => start + index);
};

const AdminPagination = ({ page, pageSize, totalElements, totalPages, onPageChange }: AdminPaginationProps) => {
  if (totalElements === 0) return null;

  const firstItem = page * pageSize + 1;
  const lastItem = Math.min((page + 1) * pageSize, totalElements);

  return (
    <div className="mt-4 flex flex-col items-center justify-between gap-3 rounded-lg border border-outline-variant bg-surface px-4 py-3 sm:flex-row">
      <p className="text-sm text-on-surface-variant">
        Hiển thị <span className="font-bold text-on-surface">{firstItem}–{lastItem}</span> trong{' '}
        <span className="font-bold text-on-surface">{totalElements}</span> kết quả
      </p>

      <div className="flex items-center gap-1">
        <button
          type="button"
          aria-label="Trang trước"
          disabled={page === 0}
          onClick={() => onPageChange(page - 1)}
          className="flex h-9 w-9 items-center justify-center rounded-lg border border-outline-variant text-primary transition hover:bg-surface-container disabled:cursor-not-allowed disabled:opacity-40"
        >
          <Icon name="arrow" className="h-4 w-4 rotate-180" />
        </button>

        {getVisiblePages(page, totalPages).map((pageNumber) => (
          <button
            key={pageNumber}
            type="button"
            onClick={() => onPageChange(pageNumber)}
            className={`h-9 min-w-9 rounded-lg px-2 text-sm font-bold transition ${pageNumber === page ? 'bg-primary text-on-primary' : 'border border-outline-variant text-primary hover:bg-surface-container'}`}
          >
            {pageNumber + 1}
          </button>
        ))}

        <button
          type="button"
          aria-label="Trang sau"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
          className="flex h-9 w-9 items-center justify-center rounded-lg border border-outline-variant text-primary transition hover:bg-surface-container disabled:cursor-not-allowed disabled:opacity-40"
        >
          <Icon name="arrow" className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
};

export default AdminPagination;
