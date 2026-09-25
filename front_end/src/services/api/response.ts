export type ApiResponse<T = unknown> = {
  statusCode?: number;
  message?: string;
  data?: T;
  timestamp?: string;
  path?: string;
  errors?: unknown;
};

export type PageMeta = {
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
};

export type PageData<T> = {
  content: T[];
  meta: PageMeta;
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

const isRecord = (value: unknown): value is Record<string, unknown> =>
  typeof value === 'object' && value !== null;

const isApiResponse = (value: unknown): value is ApiResponse<unknown> =>
  isRecord(value) && ('statusCode' in value || 'timestamp' in value || 'message' in value) && 'data' in value;

export const unwrapApiData = <T = unknown>(response: unknown): T => {
  if (isApiResponse(response)) {
    return response.data as T;
  }
  return response as T;
};

export const unwrapPage = <T = unknown>(response: unknown): PageData<T> => {
  const unwrapped = unwrapApiData<unknown>(response);
  const raw = isRecord(unwrapped) ? unwrapped : {};
  const content = Array.isArray(raw.content) ? raw.content as T[] : [];
  const meta = isRecord(raw.meta) ? raw.meta : {};

  return {
    content,
    meta: {
      pageNumber: Number(meta.pageNumber ?? raw.number ?? 0),
      pageSize: Number(meta.pageSize ?? raw.size ?? content.length),
      totalElements: Number(meta.totalElements ?? raw.totalElements ?? content.length),
      totalPages: Number(meta.totalPages ?? raw.totalPages ?? 0),
    },
    totalElements: Number(meta.totalElements ?? raw.totalElements ?? content.length),
    totalPages: Number(meta.totalPages ?? raw.totalPages ?? 0),
    number: Number(meta.pageNumber ?? raw.number ?? 0),
    size: Number(meta.pageSize ?? raw.size ?? content.length),
  };
};
