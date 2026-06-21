import { render, screen } from '@testing-library/react';

import App from '@/app/App';

test('renders app shell', () => {
  render(<App />);
  expect(screen.getAllByText(/Nhà Sách Tri Thức/i).length).toBeGreaterThan(0);
});
