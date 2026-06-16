import type { ButtonHTMLAttributes, InputHTMLAttributes, ReactNode } from 'react';
import { Link } from 'react-router-dom';

type BaseProps = {
  children?: ReactNode;
  className?: string;
};

type SectionHeadingProps = {
  eyebrow?: string;
  title: string;
  description?: string;
  action?: ReactNode;
};

type FieldProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  textarea?: boolean;
};

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & BaseProps;

type StatCardProps = {
  label: string;
  value: ReactNode;
  icon?: string;
  tone?: 'default' | 'success' | 'warning' | 'danger';
  detail?: string;
};

type StatusBadgeProps = {
  status?: string;
  children?: ReactNode;
};

type IconProps = {
  name: string;
  className?: string;
};

type BookCardProps = {
  id?: number;
  title: string;
  author?: string;
  category?: string;
  price?: number;
  cover?: string;
  discount?: number;
  onAdd?: () => void;
};

export const formatVnd = (value?: number) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value ?? 0);

export const PageShell = ({ children, className = '' }: BaseProps) => (
  <div className={`w-full bg-background text-on-surface ${className}`}>{children}</div>
);

export const Container = ({ children, className = '' }: BaseProps) => (
  <div className={`mx-auto w-full max-w-container-max px-4 sm:px-6 lg:px-8 ${className}`}>{children}</div>
);

export const Panel = ({ children, className = '' }: BaseProps) => (
  <div className={`rounded-xl border border-outline-variant bg-surface shadow-sm ${className}`}>{children}</div>
);

export const Surface = ({ children, className = '' }: BaseProps) => (
  <section className={`border-y border-outline-variant bg-surface-container-low ${className}`}>{children}</section>
);

export const SectionHeading = ({ eyebrow, title, description, action }: SectionHeadingProps) => (
  <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
    <div className="max-w-3xl">
      {eyebrow ? <p className="mb-2 text-xs font-bold uppercase text-secondary">{eyebrow}</p> : null}
      <h1 className="border-l-4 border-secondary pl-4 text-3xl font-bold leading-tight text-primary md:text-4xl">{title}</h1>
      {description ? <p className="mt-3 max-w-2xl text-base leading-7 text-on-surface-variant">{description}</p> : null}
    </div>
    {action ? <div className="flex shrink-0 flex-wrap gap-3">{action}</div> : null}
  </div>
);

export const Field = ({ label, textarea = false, className = '', ...props }: FieldProps) => (
  <label className={`block ${className}`}>
    <span className="mb-2 block text-sm font-semibold text-on-surface">{label}</span>
    {textarea ? (
      <textarea
        {...(props as any)}
        className="min-h-28 w-full rounded-lg border-outline-variant bg-surface px-4 py-3 text-sm text-on-surface shadow-sm outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
      />
    ) : (
      <input
        {...props}
        className="h-11 w-full rounded-lg border-outline-variant bg-surface px-4 text-sm text-on-surface shadow-sm outline-none transition placeholder:text-outline focus:border-primary focus:ring-2 focus:ring-primary/20"
      />
    )}
  </label>
);

export const PrimaryButton = ({ children, className = '', type = 'button', ...props }: ButtonProps) => (
  <button
    {...props}
    type={type}
    className={`inline-flex min-h-11 items-center justify-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-bold text-on-primary shadow-sm transition hover:bg-primary-container disabled:cursor-not-allowed disabled:opacity-60 ${className}`}
  >
    {children}
  </button>
);

export const SecondaryButton = ({ children, className = '', type = 'button', ...props }: ButtonProps) => (
  <button
    {...props}
    type={type}
    className={`inline-flex min-h-11 items-center justify-center gap-2 rounded-lg border border-outline-variant bg-surface px-4 py-2 text-sm font-bold text-primary shadow-sm transition hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-60 ${className}`}
  >
    {children}
  </button>
);

export const AccentButton = ({ children, className = '', type = 'button', ...props }: ButtonProps) => (
  <button
    {...props}
    type={type}
    className={`inline-flex min-h-11 items-center justify-center gap-2 rounded-lg bg-secondary-container px-4 py-2 text-sm font-bold text-on-secondary-container shadow-sm transition hover:brightness-105 disabled:cursor-not-allowed disabled:opacity-60 ${className}`}
  >
    {children}
  </button>
);

export const GhostButton = ({ children, className = '', type = 'button', ...props }: ButtonProps) => (
  <button
    {...props}
    type={type}
    className={`inline-flex min-h-10 items-center justify-center gap-2 rounded-lg px-3 py-2 text-sm font-bold text-on-surface-variant transition hover:bg-surface-container hover:text-primary ${className}`}
  >
    {children}
  </button>
);

export const IconButton = ({ children, className = '', type = 'button', ...props }: ButtonProps) => (
  <button
    {...props}
    type={type}
    className={`inline-flex h-10 w-10 items-center justify-center rounded-lg border border-outline-variant bg-surface text-on-surface-variant shadow-sm transition hover:bg-surface-container-low hover:text-primary ${className}`}
  >
    {children}
  </button>
);

export const StatCard = ({ label, value, detail, icon = 'chart', tone = 'default' }: StatCardProps) => {
  const tones = {
    default: 'bg-primary/5 text-primary',
    success: 'bg-emerald-50 text-emerald-700',
    warning: 'bg-secondary-container/20 text-secondary',
    danger: 'bg-error/5 text-error',
  };

  return (
    <Panel className="p-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-on-surface-variant">{label}</p>
          <div className="mt-2 text-2xl font-bold text-primary">{value}</div>
          {detail ? <p className="mt-2 text-sm text-on-surface-variant">{detail}</p> : null}
        </div>
        <div className={`flex h-11 w-11 items-center justify-center rounded-lg ${tones[tone]}`}>
          <Icon name={icon} className="h-5 w-5" />
        </div>
      </div>
    </Panel>
  );
};

export const StatusBadge = ({ status, children }: StatusBadgeProps) => {
  const normalized = status?.toUpperCase();
  const labelMap: Record<string, string> = {
    PENDING: 'Chờ xác nhận',
    PROCESSING: 'Đang xử lý',
    CONFIRMED: 'Đã xác nhận',
    SHIPPING: 'Đang giao',
    SHIPPED: 'Đang giao',
    DELIVERED: 'Đã giao',
    COMPLETED: 'Hoàn tất',
    CANCELLED: 'Đã hủy',
    PAID: 'Đã thanh toán',
    UNPAID: 'Chưa thanh toán',
    ACTIVE: 'Hoạt động',
    INACTIVE: 'Tạm ẩn',
  };
  const toneMap: Record<string, string> = {
    PENDING: 'bg-secondary-container/20 text-secondary ring-secondary-container',
    PROCESSING: 'bg-primary-fixed text-on-primary-fixed ring-primary-fixed-dim',
    CONFIRMED: 'bg-primary-fixed text-on-primary-fixed ring-primary-fixed-dim',
    SHIPPING: 'bg-tertiary-fixed text-on-tertiary-fixed ring-tertiary-fixed-dim',
    SHIPPED: 'bg-tertiary-fixed text-on-tertiary-fixed ring-tertiary-fixed-dim',
    DELIVERED: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
    COMPLETED: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
    CANCELLED: 'bg-error-container text-on-error-container ring-error-container',
    PAID: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
    UNPAID: 'bg-surface-container text-on-surface-variant ring-outline-variant',
    ACTIVE: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
    INACTIVE: 'bg-surface-container text-on-surface-variant ring-outline-variant',
  };

  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-bold ring-1 ring-inset ${toneMap[normalized || ''] || 'bg-surface-container text-on-surface-variant ring-outline-variant'}`}>
      {children || labelMap[normalized || ''] || status || 'Không rõ'}
    </span>
  );
};

export const EmptyState = ({ title, description, action }: { title: string; description?: string; action?: ReactNode }) => (
  <Panel className="flex flex-col items-center justify-center px-6 py-14 text-center">
    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/5 text-primary">
      <Icon name="book" />
    </div>
    <h3 className="text-lg font-bold text-primary">{title}</h3>
    {description ? <p className="mt-2 max-w-md text-sm leading-6 text-on-surface-variant">{description}</p> : null}
    {action ? <div className="mt-5">{action}</div> : null}
  </Panel>
);

export const BookCard = ({ id, title, author, category, price, cover, discount, onAdd }: BookCardProps) => (
  <Panel className="group overflow-hidden transition hover:-translate-y-1 hover:shadow-md">
    <Link to={`/books/${id || 1}`} className="block bg-surface-container-low p-4">
      <img
        src={cover || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=520&q=80'}
        alt={title}
        className="mx-auto aspect-[3/4] h-64 rounded-lg object-cover shadow"
      />
    </Link>
    <div className="p-4">
      <div className="mb-3 flex items-center justify-between gap-3">
        <StatusBadge>{category || 'Sách'}</StatusBadge>
        {discount ? <span className="rounded-full bg-secondary-container px-2 py-1 text-xs font-bold text-on-secondary-container">-{discount}%</span> : null}
      </div>
      <Link to={`/books/${id || 1}`} className="line-clamp-2 min-h-12 text-base font-bold leading-6 text-primary group-hover:text-secondary">
        {title}
      </Link>
      <p className="mt-1 truncate text-sm text-on-surface-variant">{author || 'Đang cập nhật'}</p>
      <div className="mt-4 flex items-center justify-between gap-3">
        <span className="font-bold text-primary">{formatVnd(price)}</span>
        <IconButton onClick={onAdd} aria-label="Thêm vào giỏ">
          <Icon name="cart" />
        </IconButton>
      </div>
    </div>
  </Panel>
);

export const AdminTable = ({ children, minWidth = '760px' }: BaseProps & { minWidth?: string }) => (
  <Panel className="overflow-hidden">
    <div className="overflow-x-auto">
      <table className="w-full text-left" style={{ minWidth }}>{children}</table>
    </div>
  </Panel>
);

export const AdminToolbar = ({ children }: BaseProps) => (
  <div className="mb-4 flex flex-wrap items-center gap-4 rounded-lg border border-outline-variant bg-surface-container-low p-4">{children}</div>
);

export const Icon = ({ name, className = 'h-5 w-5' }: IconProps) => {
  const common = { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24', strokeWidth: 2, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const, 'aria-hidden': true };

  switch (name) {
    case 'search':
      return <svg {...common} className={className}><path d="m21 21-4.35-4.35" /><circle cx="11" cy="11" r="7" /></svg>;
    case 'cart':
      return <svg {...common} className={className}><path d="M6 6h15l-1.5 8h-12z" /><path d="M6 6 5 3H2" /><circle cx="9" cy="20" r="1" /><circle cx="18" cy="20" r="1" /></svg>;
    case 'heart':
      return <svg {...common} className={className}><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8z" /></svg>;
    case 'user':
      return <svg {...common} className={className}><path d="M20 21a8 8 0 0 0-16 0" /><circle cx="12" cy="7" r="4" /></svg>;
    case 'users':
      return <svg {...common} className={className}><path d="M16 21a6 6 0 0 0-12 0" /><circle cx="10" cy="7" r="4" /><path d="M22 21a5 5 0 0 0-5-5" /><path d="M17 3a4 4 0 0 1 0 8" /></svg>;
    case 'book':
      return <svg {...common} className={className}><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" /><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" /></svg>;
    case 'category':
      return <svg {...common} className={className}><rect x="3" y="3" width="7" height="7" rx="1" /><rect x="14" y="3" width="7" height="7" rx="1" /><rect x="3" y="14" width="7" height="7" rx="1" /><rect x="14" y="14" width="7" height="7" rx="1" /></svg>;
    case 'order':
      return <svg {...common} className={className}><path d="M6 2h12v20l-3-2-3 2-3-2-3 2z" /><path d="M9 7h6" /><path d="M9 11h6" /><path d="M9 15h4" /></svg>;
    case 'inventory':
      return <svg {...common} className={className}><path d="M21 8 12 3 3 8l9 5z" /><path d="M3 8v8l9 5 9-5V8" /><path d="M12 13v8" /></svg>;
    case 'ticket':
      return <svg {...common} className={className}><path d="M3 9a3 3 0 0 0 0 6v3h18v-3a3 3 0 0 0 0-6V6H3z" /><path d="M13 6v12" /></svg>;
    case 'plus':
      return <svg {...common} className={className}><path d="M12 5v14" /><path d="M5 12h14" /></svg>;
    case 'edit':
      return <svg {...common} className={className}><path d="M12 20h9" /><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z" /></svg>;
    case 'trash':
      return <svg {...common} className={className}><path d="M3 6h18" /><path d="M8 6V4h8v2" /><path d="m19 6-1 14H6L5 6" /><path d="M10 11v5" /><path d="M14 11v5" /></svg>;
    case 'chart':
      return <svg {...common} className={className}><path d="M4 19h16" /><path d="M7 16V9" /><path d="M12 16V5" /><path d="M17 16v-7" /></svg>;
    case 'mail':
      return <svg {...common} className={className}><rect x="3" y="5" width="18" height="14" rx="2" /><path d="m3 7 9 6 9-6" /></svg>;
    case 'lock':
      return <svg {...common} className={className}><rect x="5" y="10" width="14" height="10" rx="2" /><path d="M8 10V7a4 4 0 1 1 8 0v3" /></svg>;
    case 'menu':
      return <svg {...common} className={className}><path d="M4 6h16" /><path d="M4 12h16" /><path d="M4 18h16" /></svg>;
    case 'x':
      return <svg {...common} className={className}><path d="M18 6 6 18" /><path d="m6 6 12 12" /></svg>;
    case 'arrow':
      return <svg {...common} className={className}><path d="M5 12h14" /><path d="m12 5 7 7-7 7" /></svg>;
    case 'file':
      return <svg {...common} className={className}><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" /><path d="M14 2v6h6" /></svg>;
    case 'bell':
      return <svg {...common} className={className}><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 7h18s-3 0-3-7" /><path d="M13.73 21a2 2 0 0 1-3.46 0" /></svg>;
    default:
      return <svg {...common} className={className}><circle cx="12" cy="12" r="9" /></svg>;
  }
};
