import { Component, ErrorInfo, ReactNode } from 'react';

import { Container, PrimaryButton } from '@/components/ui/staticUi';

type Props = { children: ReactNode };
type State = { hasError: boolean };

export default class AppErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError(): State {
    return { hasError: true };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    if (import.meta.env.DEV) console.error('Unhandled application error', error, info);
  }

  render() {
    if (!this.state.hasError) return this.props.children;

    return (
      <main className="flex min-h-screen items-center bg-background py-16 text-on-surface">
        <Container>
          <div className="mx-auto max-w-xl rounded-xl border border-outline-variant bg-surface p-8 text-center shadow-sm">
            <p className="text-xs font-bold uppercase text-secondary">Có lỗi xảy ra</p>
            <h1 className="mt-3 text-3xl font-bold text-primary">Ứng dụng tạm thời không thể hiển thị</h1>
            <p className="mt-4 leading-7 text-on-surface-variant">Vui lòng tải lại trang. Nếu lỗi tiếp tục xuất hiện, hãy quay lại sau ít phút.</p>
            <PrimaryButton className="mt-6" onClick={() => window.location.reload()}>Tải lại trang</PrimaryButton>
          </div>
        </Container>
      </main>
    );
  }
}
