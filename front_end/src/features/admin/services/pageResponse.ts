import { unwrapPage } from '@/services/api/response';

export const withPage = (response: any, mapper = (item: any) => item) => {
  const page = unwrapPage<any>(response);
  return {
    data: {
      ...page,
      content: page.content.map(mapper),
    },
  };
};
