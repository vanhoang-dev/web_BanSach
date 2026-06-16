/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./index.html",
        "./src/**/*.{js,jsx,ts,tsx}",
    ],
    darkMode: "class",
    theme: {
        extend: {
            colors: {
                // Surface
                "surface": "#f7fafc",
                "surface-dim": "#d7dadc",
                "surface-bright": "#f7fafc",
                "surface-container-lowest": "#ffffff",
                "surface-container-low": "#f1f4f6",
                "surface-container": "#ebeef0",
                "surface-container-high": "#e5e9eb",
                "surface-container-highest": "#e0e3e5",
                "on-surface": "#181c1e",
                "on-surface-variant": "#43474e",
                "inverse-surface": "#2d3133",
                "inverse-on-surface": "#eef1f3",

                // Primary
                "primary": "#002045",
                "on-primary": "#ffffff",
                "primary-container": "#1a365d",
                "on-primary-container": "#86a0cd",
                "inverse-primary": "#adc7f7",
                "primary-fixed": "#d6e3ff",
                "primary-fixed-dim": "#adc7f7",
                "on-primary-fixed": "#001b3c",
                "on-primary-fixed-variant": "#2d476f",

                // Secondary
                "secondary": "#944b00",
                "on-secondary": "#ffffff",
                "secondary-container": "#fe9743",
                "on-secondary-container": "#6b3500",
                "secondary-fixed": "#ffdcc5",
                "secondary-fixed-dim": "#ffb783",
                "on-secondary-fixed": "#301400",
                "on-secondary-fixed-variant": "#703700",

                // Tertiary
                "tertiary": "#715c00",
                "on-tertiary": "#ffffff",
                "tertiary-container": "#c9a82c",
                "on-tertiary-container": "#4d3e00",
                "tertiary-fixed": "#ffe17c",
                "tertiary-fixed-dim": "#e6c446",
                "on-tertiary-fixed": "#231b00",
                "on-tertiary-fixed-variant": "#564500",

                // Error
                "error": "#ba1a1a",
                "on-error": "#ffffff",
                "error-container": "#ffdad6",
                "on-error-container": "#93000a",

                // Other
                "outline": "#74777f",
                "outline-variant": "#c4c6cf",
                "surface-tint": "#455f88",
                "background": "#f7fafc",
                "on-background": "#181c1e",
                "surface-variant": "#e0e3e5",
            },
            borderRadius: {
                "DEFAULT": "0.125rem",
                "lg": "0.25rem",
                "xl": "0.5rem",
                "full": "0.75rem"
            },
            spacing: {
                "unit": "4px",
                "stack-sm": "8px",
                "stack-md": "16px",
                "stack-lg": "32px",
                "section-gap": "64px",
                "gutter": "24px",
                "margin-mobile": "16px",
                "container-max": "1280px"
            },
            fontFamily: {
                "h3": ["Manrope"],
                "label-md": ["Manrope"],
                "caption": ["Manrope"],
                "body-md": ["Manrope"],
                "body-lg": ["Manrope"],
                "h1": ["Manrope"],
                "h2": ["Manrope"]
            },
            fontSize: {
                "h3": ["24px", { "lineHeight": "1.4", "fontWeight": "700" }],
                "label-md": ["14px", { "lineHeight": "1.2", "fontWeight": "600" }],
                "caption": ["12px", { "lineHeight": "1.4", "fontWeight": "500" }],
                "body-md": ["16px", { "lineHeight": "1.6", "fontWeight": "400" }],
                "body-lg": ["18px", { "lineHeight": "1.6", "fontWeight": "400" }],
                "h1": ["40px", { "lineHeight": "1.2", "letterSpacing": "0", "fontWeight": "800" }],
                "h2": ["32px", { "lineHeight": "1.3", "letterSpacing": "0", "fontWeight": "700" }]
            }
        },
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
};
