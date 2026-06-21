package com.example.web_bansach.security.config;

public class Endpoints {
    private Endpoints() {
    }

    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/categories",
            "/api/categories/*",
            "/api/categories/search",
            "/api/authors",
            "/api/authors/*",
            "/api/authors/search",
            "/api/payment/sse/order/*",
            "/user/books",
            "/user/books/*",
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/tai-khoan/dang-ky",
            "/tai-khoan/dang-nhap",
            "/tai-khoan/quen-mat-khau",
            "/tai-khoan/dat-lai-mat-khau",
            "/api/payment/sepay-webhook",
    };

    public static final String[] USER_GET_ENDPOINTS = {
            "/user/me",
            "/user/cart",
            "/user/wishlist",
            "/user/wishlist/count",
            "/user/wishlist/books/*/check",
            "/user/orders",
            "/user/orders/*",
            "/user/reviews/book/*/my-review",
            "/user/reviews/book/*",
            "/user/reviews/book/*/stats",
            "/user/vouchers",
            "/user/vouchers/code/*",
            "/user/vouchers/my",
            "/user/vouchers/claimed",
            "/user/inventory/book/*",
            "/api/payment/status/*",
            "/api/payment/status/order/*",
    };

    public static final String[] USER_POST_ENDPOINTS = {
            "/user/cart/items",
            "/user/wishlist/books/*",
            "/user/orders",
            "/user/orders/buy-now",
            "/user/reviews",
            "/api/payment/initiate",
            "/user/change-password",
            "/user/vouchers/*/claim",
    };

    public static final String[] USER_PUT_ENDPOINTS = {
            "/user/update-profile",
            "/user/cart/items/*",
            "/user/orders/*/cancel",
            "/user/reviews/*",
    };

    public static final String[] USER_DELETE_ENDPOINTS = {
            "/user/cart/items/*",
            "/user/cart/clear",
            "/user/wishlist/books/*",
            "/user/wishlist/clear",
            "/user/reviews/*",
    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/admin/dashboard",
            "/admin/books",
            "/admin/books/*",
            "/admin/authors",
            "/api/admin/categories",
            "/api/admin/categories/*",
            "/admin/inventory",
            "/admin/orders",
            "/admin/orders/*",
            "/admin/reviews",
            "/admin/reviews/*",
            "/admin/vouchers",
            "/admin/vouchers/*",
            "/admin/vouchers/expired",
            "/user_for_admin/all",
            "/user_for_admin/user/*",
    };

    public static final String[] ADMIN_POST_ENDPOINTS = {
            "/admin/books/create-book",
            "/admin/authors",
            "/api/admin/categories",
            "/admin/inventory/*/adjust/*",
            "/admin/vouchers",
    };

    public static final String[] ADMIN_PUT_ENDPOINTS = {
            "/admin/books/update-book/*",
            "/admin/authors/*",
            "/api/admin/categories/*",
            "/api/admin/categories/*/activate",
            "/api/admin/categories/*/deactivate",
            "/admin/inventory/*/set/*",
            "/admin/inventory/*/reconcile/*",
            "/admin/orders/*/status",
            "/admin/orders/*/cancel",
            "/admin/vouchers/*",
            "/user_for_admin/user/*",
    };

    public static final String[] ADMIN_DELETE_ENDPOINTS = {
            "/admin/books/delete-book/*",
            "/admin/authors/*",
            "/api/admin/categories/*",
            "/admin/orders/*",
            "/admin/reviews/*",
            "/admin/vouchers/*",
            "/user_for_admin/user/delete/*",
    };
}
