import { act, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, test, vi } from 'vitest';

import { LinkButton } from '@/components/ui/staticUi';
import CartPage from '@/features/cart/pages/CartPage';
import cartService from '@/features/cart/services/cartService';
import { NotificationProvider, notify } from '@/features/notifications/NotificationProvider';

vi.mock('@/features/cart/services/cartService', () => ({
  default: {
    getCart: vi.fn(),
    updateCartItem: vi.fn(),
    removeFromCart: vi.fn(),
  },
}));

const cart = {
  items: [{
    id: 10,
    bookId: 7,
    quantity: 2,
    price: 120000,
    subtotal: 240000,
    book: { id: 7, title: 'Một cuốn sách có tiêu đề dài', price: 120000 },
  }],
  totalPrice: 240000,
  totalQuantity: 2,
};

describe('commerce UI safeguards', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(cartService.getCart).mockResolvedValue(cart);
    vi.mocked(cartService.updateCartItem).mockResolvedValue({ ...cart, totalQuantity: 3 });
    vi.mocked(cartService.removeFromCart).mockResolvedValue({ items: [], totalPrice: 0, totalQuantity: 0 });
  });

  test('renders a link CTA without nesting a button', () => {
    render(<MemoryRouter><LinkButton to="/catalog">Mua sách</LinkButton></MemoryRouter>);
    const link = screen.getByRole('link', { name: 'Mua sách' });
    expect(link).toHaveAttribute('href', '/catalog');
    expect(link.querySelector('button')).toBeNull();
  });

  test('announces non-blocking notifications', async () => {
    render(<NotificationProvider />);
    act(() => notify.success('Đã thêm vào giỏ hàng'));
    expect(await screen.findByRole('status')).toHaveTextContent('Đã thêm vào giỏ hàng');
  });

  test('updates cart quantity through an accessible control', async () => {
    render(<MemoryRouter><CartPage /></MemoryRouter>);
    const increase = await screen.findByRole('button', { name: /Tăng số lượng/i });
    fireEvent.click(increase);
    await waitFor(() => expect(cartService.updateCartItem).toHaveBeenCalledWith(7, 3));
  });
});
