import { Link } from 'react-router-dom';

import { Container, Icon, PrimaryButton, SecondaryButton } from '@/components/ui/staticUi';

export type ErrorPageContent = {
  code: string;
  eyebrow: string;
  title: string;
  description: string;
  tone: 'primary' | 'warning' | 'danger' | 'neutral';
  hints: string[];
  primaryLabel: string;
  primaryTo?: string;
  secondaryLabel?: string;
  secondaryTo?: string;
  allowReload?: boolean;
};

const toneClasses: Record<ErrorPageContent['tone'], string> = {
  primary: 'bg-primary text-on-primary',
  warning: 'bg-secondary-container text-on-secondary-container',
  danger: 'bg-error-container text-on-error-container',
  neutral: 'bg-surface-container text-primary',
};

const PrimaryAction = ({ content }: { content: ErrorPageContent }) => {
  if (content.allowReload) {
    return (
      <PrimaryButton onClick={() => window.location.reload()}>
        {content.primaryLabel}
        <Icon name="arrow" />
      </PrimaryButton>
    );
  }

  return (
    <Link to={content.primaryTo || '/'}>
      <PrimaryButton>
        {content.primaryLabel}
        <Icon name="arrow" />
      </PrimaryButton>
    </Link>
  );
};

const ErrorPageLayout = ({ content }: { content: ErrorPageContent }) => (
  <Container className="py-16 lg:py-20">
    <section className="grid min-h-[54vh] items-center gap-10 lg:grid-cols-[0.95fr_1.05fr]">
      <div>
        <p className="text-xs font-bold uppercase text-secondary">{content.eyebrow}</p>
        <h1 className="mt-4 max-w-2xl text-4xl font-bold leading-tight text-primary sm:text-5xl">
          {content.title}
        </h1>
        <p className="mt-5 max-w-xl text-base leading-7 text-on-surface-variant">{content.description}</p>

        <div className="mt-8 flex flex-wrap gap-3">
          <PrimaryAction content={content} />
          {content.secondaryTo && content.secondaryLabel ? (
            <Link to={content.secondaryTo}>
              <SecondaryButton>{content.secondaryLabel}</SecondaryButton>
            </Link>
          ) : null}
        </div>
      </div>

      <div className="rounded-xl border border-outline-variant bg-surface p-6 shadow-sm">
        <div className={`flex min-h-52 items-center justify-center rounded-lg ${toneClasses[content.tone]}`}>
          <span className="text-center text-5xl font-bold tracking-normal sm:text-7xl">{content.code}</span>
        </div>
        <div className="mt-6 grid gap-3">
          {content.hints.map((hint) => (
            <div key={hint} className="flex gap-3 rounded-lg bg-surface-container-low px-4 py-3 text-sm text-on-surface-variant">
              <span className="mt-1 h-2 w-2 shrink-0 rounded-full bg-secondary" />
              <span>{hint}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  </Container>
);

export default ErrorPageLayout;
