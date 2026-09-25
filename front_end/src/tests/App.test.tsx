import { render, screen } from '@testing-library/react';

import App from '@/app/App';

test('renders app shell', async () => {
  render(<App />);
  expect((await screen.findAllByText(/Nhà Sách Tri Thức/i)).length).toBeGreaterThan(0);
});
