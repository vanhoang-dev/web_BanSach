import { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { Container, EmptyState, Icon, Panel, PrimaryButton, SectionHeading } from '@/components/ui/staticUi';
import authorService, { AuthorItem } from '@/features/authors/services/authorService';

const AuthorsPage = () => {
  const [authors, setAuthors] = useState<AuthorItem[]>([]);
  const [keyword, setKeyword] = useState('');
  const [activeKeyword, setActiveKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const pageSize = 9;

  const fetchAuthors = async (search: string, pageNumber: number) => {
    try {
      setLoading(true);
      setError('');
      const response = search
        ? await authorService.searchAuthors(search, pageNumber, pageSize)
        : await authorService.getAuthors(pageNumber, pageSize);
      setAuthors(response.data.content || []);
      setPageInfo({
        totalElements: Number(response.data.totalElements || 0),
        totalPages: Number(response.data.totalPages || 0),
      });
    } catch {
      setError('Không thể tải danh sách tác giả.');
      setAuthors([]);
      setPageInfo({ totalElements: 0, totalPages: 0 });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAuthors(activeKeyword, page);
  }, [activeKeyword, page]);

  const handleSearch = (event: FormEvent) => {
    event.preventDefault();
    setPage(0);
    setActiveKeyword(keyword.trim());
  };

  const visiblePages = Array.from({ length: pageInfo.totalPages }, (_, index) => index)
    .filter((pageNumber) => pageNumber === 0
      || pageNumber === pageInfo.totalPages - 1
      || Math.abs(pageNumber - page) <= 1);

  return (
    <Container className="py-10">
      <SectionHeading eyebrow="Tác giả" title="Tác giả nổi bật" description="Khám phá sách theo những tác giả đã có trong hệ thống." />
      <Panel className="mb-6 p-4">
        <form className="flex flex-col gap-3 sm:flex-row" onSubmit={handleSearch}>
          <input value={keyword} onChange={(event) => setKeyword(event.target.value)} className="h-11 flex-1 rounded-lg border border-outline-variant bg-surface px-4 text-sm outline-none focus:border-primary" placeholder="Tìm tác giả..." />
          <PrimaryButton type="submit"><Icon name="search" /> Tìm kiếm</PrimaryButton>
        </form>
      </Panel>
      {!loading && !error ? (
        <p className="mb-5 text-sm font-semibold text-on-surface-variant">
          {pageInfo.totalElements} tác giả{activeKeyword ? ` phù hợp với “${activeKeyword}”` : ''}
        </p>
      ) : null}
      {error ? <div className="mb-5 rounded-lg bg-error-container px-4 py-3 text-sm font-semibold text-on-error-container">{error}</div> : null}
      {loading ? (
        <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">{Array.from({ length: 6 }).map((_, index) => <div key={index} className="h-44 animate-pulse rounded-xl bg-surface-container" />)}</div>
      ) : authors.length === 0 ? (
        <EmptyState title="Chưa có tác giả" description="Thử từ khóa khác hoặc quay lại danh mục sách." />
      ) : (
        <>
          <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
            {authors.map((author) => (
              <Panel key={author.id} className="p-5 transition hover:-translate-y-1 hover:shadow-md">
                <div className="flex items-start gap-4">
                  <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-full bg-primary/5 text-primary"><Icon name="user" /></div>
                  <div className="min-w-0 flex-1">
                    <h3 className="text-lg font-bold text-primary">{author.authorName}</h3>
                    <p className="mt-2 line-clamp-3 text-sm leading-6 text-on-surface-variant">{author.biography || 'Tiểu sử tác giả đang được cập nhật.'}</p>
                    <div className="mt-5 flex items-center justify-between">
                      <span className="text-sm font-bold text-secondary">Tác giả</span>
                      <Link to={`/catalog?authorId=${author.id}&authorName=${encodeURIComponent(author.authorName)}`} className="text-sm font-bold text-primary hover:underline">Xem sách</Link>
                    </div>
                  </div>
                </div>
              </Panel>
            ))}
          </div>

          {pageInfo.totalPages > 1 ? (
            <nav className="mt-8 flex flex-wrap items-center justify-center gap-2" aria-label="Phân trang tác giả">
              <button type="button" disabled={page === 0} onClick={() => setPage((current) => Math.max(0, current - 1))} className="rounded-lg border border-outline-variant bg-surface px-4 py-2 text-sm font-bold text-primary disabled:cursor-not-allowed disabled:opacity-40">Trước</button>
              {visiblePages.map((pageNumber, index) => (
                <span key={pageNumber} className="contents">
                  {index > 0 && pageNumber - visiblePages[index - 1] > 1 ? <span className="px-1 text-on-surface-variant">…</span> : null}
                  <button type="button" aria-current={pageNumber === page ? 'page' : undefined} onClick={() => setPage(pageNumber)} className={`h-10 min-w-10 rounded-lg px-3 text-sm font-bold ${pageNumber === page ? 'bg-primary text-on-primary' : 'border border-outline-variant bg-surface text-primary hover:bg-surface-container-low'}`}>{pageNumber + 1}</button>
                </span>
              ))}
              <button type="button" disabled={page >= pageInfo.totalPages - 1} onClick={() => setPage((current) => Math.min(pageInfo.totalPages - 1, current + 1))} className="rounded-lg border border-outline-variant bg-surface px-4 py-2 text-sm font-bold text-primary disabled:cursor-not-allowed disabled:opacity-40">Tiếp</button>
            </nav>
          ) : null}
        </>
      )}
    </Container>
  );
};

export default AuthorsPage;
