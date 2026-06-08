import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { PageShell, Panel, SectionHeading, Icon, SecondaryButton } from '@/components/ui/staticUi';
import bookService from '@/features/books/services/bookService';
import cartService from '@/features/cart/services/cartService';

const CatalogPage = () => {
    const [books, setBooks] = useState<any[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const cats = await bookService.getCategories();
                setCategories(Array.isArray(cats) ? cats : []);
            } catch (err) {
                console.error('Error loading categories:', err);
            }
        };
        fetchCategories();
    }, []);

    useEffect(() => {
        const fetchBooks = async () => {
            try {
                setLoading(true);
                setError('');
                const categoryId = selectedCategories.length > 0 ? selectedCategories[0] : undefined;
                const response = await bookService.getBooks(page, 12, undefined, categoryId);
                setBooks(response?.data?.content || []);
                setTotalPages(response?.data?.totalPages || 0);
            } catch (err: any) {
                setError('Không thể tải sách');
                setBooks([]);
            } finally {
                setLoading(false);
            }
        };
        fetchBooks();
    }, [page, selectedCategories]);

    const handleCategoryToggle = (categoryId: number) => {
        setSelectedCategories(prev =>
            prev.includes(categoryId)
                ? prev.filter(id => id !== categoryId)
                : [...prev.slice(0, 0), categoryId]
        );
        setPage(0);
    };

    const handleAddToCart = async (bookId: number) => {
        try {
            await cartService.addToCart(bookId, 1);
            alert('Đã thêm vào giỏ hàng');
        } catch (err) {
            alert('Lỗi khi thêm vào giỏ hàng');
        }
    };

    return (
        <PageShell>
            <div className="max-w-container-max mx-auto px-gutter py-section-gap">
                <SectionHeading
                    eyebrow="Danh mục"
                    title="Tất cả sách"
                    description="Khám phá bộ sưu tập sách phong phú với nhiều thể loại, tác giả và giá cả phù hợp"
                    action={<SecondaryButton>{Icon({ name: 'search' })}Lọc nâng cao</SecondaryButton>}
                />

                <div className="grid grid-cols-1 lg:grid-cols-4 gap-gutter">
                    <Panel className="p-stack-lg lg:sticky lg:top-24 h-fit">
                        <h3 className="font-h3 text-h3 text-primary mb-stack-md">Bộ lọc</h3>
                        <div className="space-y-stack-md">
                            {categories.map((category) => (
                                <label key={category.id} className="flex items-center justify-between rounded-lg bg-surface-container-low px-4 py-3 cursor-pointer hover:bg-surface-container transition-colors">
                                    <span className="font-body-md text-body-md text-on-surface">{category.name}</span>
                                    <input
                                        type="checkbox"
                                        checked={selectedCategories.includes(category.id)}
                                        onChange={() => handleCategoryToggle(category.id)}
                                        className="rounded border-outline-variant text-primary focus:ring-primary"
                                    />
                                </label>
                            ))}
                        </div>
                    </Panel>

                    <div className="lg:col-span-3">
                        {loading ? (
                            <div className="flex items-center justify-center py-20">
                                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
                            </div>
                        ) : error ? (
                            <div className="text-center py-10">
                                <p className="text-error font-body-lg">{error}</p>
                            </div>
                        ) : books.length === 0 ? (
                            <div className="text-center py-10">
                                <p className="text-on-surface-variant font-body-lg">Không tìm thấy sách nào</p>
                            </div>
                        ) : (
                            <>
                                <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-gutter">
                                    {books.map((book) => (
                                        <Panel key={book.id} className="overflow-hidden flex flex-col">
                                            <div className="h-64 bg-surface-container flex items-center justify-center">
                                                <img
                                                    alt={book.title}
                                                    className="h-full object-contain rounded"
                                                    src={book.cover || 'https://via.placeholder.com/200x300'}
                                                />
                                            </div>
                                            <div className="p-stack-md flex-grow flex flex-col justify-between">
                                                <div>
                                                    <Link to={`/books/${book.id}`}>
                                                        <h4 className="font-body-lg text-body-lg text-primary font-bold hover:underline">{book.title}</h4>
                                                    </Link>
                                                    <p className="font-caption text-caption text-on-surface-variant mt-unit">
                                                        {book.author?.name || 'N/A'}
                                                    </p>
                                                </div>
                                                <div className="flex items-center justify-between mt-stack-md">
                                                    <span className="font-label-md text-label-md text-secondary-container font-bold">
                                                        {book.price?.toLocaleString('vi-VN')} ₫
                                                    </span>
                                                    <button
                                                        onClick={() => handleAddToCart(book.id)}
                                                        className="bg-primary text-on-primary p-2 rounded-lg hover:bg-primary-container transition-colors"
                                                    >
                                                        {Icon({ name: 'cart', className: 'w-5 h-5' })}
                                                    </button>
                                                </div>
                                            </div>
                                        </Panel>
                                    ))}
                                </div>

                                <div className="mt-stack-lg flex justify-center gap-unit">
                                    <button
                                        disabled={page === 0}
                                        onClick={() => setPage(Math.max(0, page - 1))}
                                        className="px-4 py-2 border border-outline-variant rounded-lg hover:bg-surface-container disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        Trước
                                    </button>
                                    <span className="px-4 py-2">Trang {page + 1} / {totalPages}</span>
                                    <button
                                        disabled={page >= totalPages - 1}
                                        onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                                        className="px-4 py-2 border border-outline-variant rounded-lg hover:bg-surface-container disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        Tiếp
                                    </button>
                                </div>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </PageShell>
    );
};

export default CatalogPage;
