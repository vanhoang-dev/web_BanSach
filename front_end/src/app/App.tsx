import { AppProvider } from '@/app/providers/AppProvider';
import { RouterProvider } from '@/app/providers/RouterProvider';
import AppErrorBoundary from '@/features/errors/components/AppErrorBoundary';

function App() {
  return (
    <AppProvider>
      <AppErrorBoundary>
        <RouterProvider />
      </AppErrorBoundary>
    </AppProvider>
  );
}

export default App;
