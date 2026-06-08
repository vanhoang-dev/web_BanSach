import { ReactNode } from 'react';

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

type FieldProps = {
    label: string;
    placeholder?: string;
    type?: string;
    textarea?: boolean;
};

type IconProps = {
    name: string;
    className?: string;
};

export const PageShell = ({ children, className = '' }: BaseProps) => (
    <div className={`w-full ${className}`}>{children}</div>
);

export const Panel = ({ children, className = '' }: BaseProps) => (
    <div className={`bg-surface-container-lowest rounded-xl border border-surface-variant shadow-[0px_4px_12px_rgba(30,27,75,0.05)] ${className}`}>
        {children}
    </div>
);

export const SectionHeading = ({ eyebrow, title, description, action }: SectionHeadingProps) => (
    <div className="flex flex-col gap-unit md:flex-row md:items-end md:justify-between mb-stack-lg">
        <div>
            {eyebrow ? <p className="font-caption text-caption uppercase tracking-wider text-secondary mb-unit">{eyebrow}</p> : null}
            <h1 className="font-h2 text-h2 text-primary">{title}</h1>
            {description ? <p className="font-body-md text-body-md text-on-surface-variant mt-stack-sm max-w-2xl">{description}</p> : null}
        </div>
        {action ? <div>{action}</div> : null}
    </div>
);

export const Field = ({ label, placeholder, type = 'text', textarea = false }: FieldProps) => (
    <label className="flex flex-col gap-stack-sm">
        <span className="font-label-md text-label-md text-on-surface">{label}</span>
        {textarea ? (
            <textarea
                className="min-h-[120px] rounded-lg border border-outline-variant bg-surface-container-low px-4 py-3 font-body-md text-body-md text-on-surface focus:outline-none focus:ring-1 focus:ring-primary"
                placeholder={placeholder}
            />
        ) : (
            <input
                className="h-12 rounded-lg border border-outline-variant bg-surface-container-low px-4 font-body-md text-body-md text-on-surface focus:outline-none focus:ring-1 focus:ring-primary"
                placeholder={placeholder}
                type={type}
            />
        )}
    </label>
);

export const PrimaryButton = ({ children, className = '' }: BaseProps) => (
    <button className={`inline-flex items-center justify-center gap-unit rounded-lg bg-primary px-5 py-3 font-label-md text-label-md text-on-primary shadow-sm transition-colors hover:bg-primary-container ${className}`}>
        {children}
    </button>
);

export const SecondaryButton = ({ children, className = '' }: BaseProps) => (
    <button className={`inline-flex items-center justify-center gap-unit rounded-lg border border-primary px-5 py-3 font-label-md text-label-md text-primary transition-colors hover:bg-surface-container-low ${className}`}>
        {children}
    </button>
);

export const Icon = ({ name, className = 'w-5 h-5' }: IconProps) => {
    const common = { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24', 'aria-hidden': true };
    switch (name) {
        case 'search':
            return (
                <svg {...common} className={className}>
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
            );
        case 'cart':
            return (
                <svg {...common} className={className}>
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2 8m10 0l2-8m0 0h2m-2 0h-2m0 8h-4m0 0h4" />
                </svg>
            );
        case 'heart':
            return (
                <svg {...common} className={className}>
                    <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.53L12 21.35z" fill="currentColor" stroke="none" />
                </svg>
            );
        case 'user':
            return (
                <svg {...common} className={className}>
                    <circle cx="12" cy="8" r="4" />
                    <path d="M12 14c-6 0-8 3-8 3v3h16v-3s-2-3-8-3z" />
                </svg>
            );
        case 'mail':
            return (
                <svg {...common} className={className}>
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5h18v14H3z" />
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7l9 6 9-6" />
                </svg>
            );
        case 'lock':
            return (
                <svg {...common} className={className}>
                    <rect x="5" y="10" width="14" height="10" rx="2" />
                    <path d="M8 10V7a4 4 0 118 0v3" />
                </svg>
            );
        case 'arrow':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" /></svg>;
        case 'plus':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 5v14M5 12h14" /></svg>;
        case 'edit':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 20h9" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16.5 3.5a2.1 2.1 0 113 3L7 19l-4 1 1-4 12.5-12.5z" /></svg>;
        case 'trash':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3" /></svg>;
        case 'box':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7l9-4 9 4-9 4-9-4z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7v10l9 4 9-4V7" /></svg>;
        case 'truck':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7h11v10H3zM14 10h4l3 3v4h-7z" /><circle cx="7" cy="19" r="1.5" fill="currentColor" stroke="none" /><circle cx="17" cy="19" r="1.5" fill="currentColor" stroke="none" /></svg>;
        case 'qr':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4h6v6H4zM14 4h6v6h-6zM4 14h6v6H4zM14 14h2v2h-2zM18 14h2v2h-2zM14 18h2v2h-2zM18 18h2v2h-2z" /></svg>;
        case 'star':
            return <svg {...common} className={className}><path d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 21 12 17.77 5.82 21 7 14.14 2 9.27l6.91-1.01L12 2z" fill="currentColor" stroke="none" /></svg>;
        case 'chart':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 19h16M7 16V9M12 16V5M17 16v-7" /></svg>;
        case 'users':
            return <svg {...common} className={className}><circle cx="8" cy="8" r="3" /><circle cx="17" cy="9" r="2.5" /><path d="M3 19c0-3 2.5-5 5-5s5 2 5 5" /><path d="M12 19c.4-2 2.1-3.5 4.2-3.5 1.6 0 3.1.8 3.8 2.1" /></svg>;
        case 'package':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 3l8 4-8 4-8-4 8-4z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 7v10l8 4 8-4V7" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 11v10" /></svg>;
        case 'shield':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 3l7 3v5c0 5-3 8-7 10-4-2-7-5-7-10V6l7-3z" /></svg>;
        case 'bell':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 22a2 2 0 002-2h-4a2 2 0 002 2z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 17h12l-1.5-2V10a4.5 4.5 0 00-9 0v5L6 17z" /></svg>;
        case 'settings':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19.14 12.94a7.43 7.43 0 000-1.88l2.03-1.58-1.92-3.32-2.39.96a7.9 7.9 0 00-1.62-.94l-.36-2.54h-3.84l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96-1.92 3.32 2.03 1.58a7.43 7.43 0 000 1.88L4.75 14.5l1.92 3.32 2.39-.96c.49.37 1.03.7 1.62.94l.36 2.54h3.84l.36-2.54c.59-.24 1.13-.57 1.62-.94l2.39.96 1.92-3.32-2.03-1.56z" /><circle cx="12" cy="12" r="3" /></svg>;
        case 'file':
            return <svg {...common} className={className}><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 3h7l5 5v13H7z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 3v5h5" /></svg>;
        default:
            return null;
    }
};
