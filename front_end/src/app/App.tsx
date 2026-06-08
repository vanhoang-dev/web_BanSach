import '@/assets/styles/app.css';

import { AppProvider } from '@/app/providers/AppProvider';
import { RouterProvider } from '@/app/providers/RouterProvider';

function App() {
  return (
    <AppProvider>
      <RouterProvider />
    </AppProvider>
  );
}

export default App;
