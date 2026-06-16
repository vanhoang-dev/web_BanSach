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

const isApiResponse = (value: any) =>
  value && typeof value === 'object' && ('statusCode' in value || 'timestamp' in value || 'message' in value) && 'data' in value;

export const unwrapApiData = <T = unknown>(response: any): T => {
  if (isApiResponse(response)) {
    return response.data as T;
  }
  return response as T;
};

export const unwrapPage = <T = unknown>(response: any): PageData<T> => {
  const raw: any = unwrapApiData(response) || {};
  const content = Array.isArray(raw.content) ? raw.content : [];
  const meta = raw.meta || {};

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
