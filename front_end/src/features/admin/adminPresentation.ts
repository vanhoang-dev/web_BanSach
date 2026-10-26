type ApiErrorPayload = {
  message?: string;
  errors?: Record<string, string>;
};

export const getAdminErrorMessage = (error: unknown, fallback: string): string => {
  if (typeof error === 'string') return error;
  if (!error || typeof error !== 'object') return fallback;

  const payload = error as ApiErrorPayload;
  const fieldError = payload.errors ? Object.values(payload.errors)[0] : undefined;
  return fieldError || payload.message || fallback;
};

const roleLabels: Record<string, string> = {
  ROLE_USER: 'Khách hàng',
  USER: 'Khách hàng',
  ROLE_ADMIN: 'Quản trị viên',
  ADMIN: 'Quản trị viên',
};

export const formatRoles = (roles: unknown): string => {
  const values = Array.isArray(roles) ? roles : roles instanceof Set ? Array.from(roles) : roles ? [roles] : [];
  if (values.length === 0) return 'Khách hàng';
  return values.map((role) => {
    const raw = typeof role === 'object' && role !== null && 'name' in role
      ? String((role as { name: unknown }).name)
      : String(role);
    return roleLabels[raw.toUpperCase()] || raw;
  }).join(', ');
};

export const orderStatusLabels: Record<string, string> = {
  PENDING: 'Chờ xác nhận',
  CONFIRMED: 'Đã xác nhận',
  PROCESSING: 'Đang xử lý',
  SHIPPING: 'Đang giao',
  SHIPPED: 'Đang giao',
  DELIVERED: 'Đã giao',
  COMPLETED: 'Hoàn tất',
  CANCELLED: 'Đã hủy',
};
