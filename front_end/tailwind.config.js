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
                "surface": "#FFFFFF",
                "surface-dim": "#F2F4F7",
                "surface-bright": "#FFFFFF",
                "surface-container-lowest": "#ffffff",
                "surface-container-low": "#F7F8FA",
                "surface-container": "#F2F4F7",
                "surface-container-high": "#EAECF0",
                "surface-container-highest": "#E4E7EC",
                "on-surface": "#1D2939",
                "on-surface-variant": "#667085",
                "inverse-surface": "#1D2939",
                "inverse-on-surface": "#FFFFFF",

                // Primary
                "primary": "#183B4E",
                "on-primary": "#ffffff",
                "primary-container": "#102E3D",
                "on-primary-container": "#FFFFFF",
                "inverse-primary": "#E8F0F3",
                "primary-fixed": "#E8F0F3",
                "primary-fixed-dim": "#D6E4E9",
                "on-primary-fixed": "#183B4E",
                "on-primary-fixed-variant": "#344054",

                // Secondary
                "secondary": "#E58F29",
                "on-secondary": "#1D2939",
                "secondary-container": "#F4A340",
                "on-secondary-container": "#1D2939",
                "secondary-fixed": "#FFF3E3",
                "secondary-fixed-dim": "#FAD7A5",
                "on-secondary-fixed": "#1D2939",
                "on-secondary-fixed-variant": "#7A4512",

                // Tertiary
                "tertiary": "#667085",
                "on-tertiary": "#ffffff",
                "tertiary-container": "#c9a82c",
                "on-tertiary-container": "#4d3e00",
                "tertiary-fixed": "#ffe17c",
                "tertiary-fixed-dim": "#e6c446",
                "on-tertiary-fixed": "#231b00",
                "on-tertiary-fixed-variant": "#564500",

                // Error
                "error": "#DC2626",
                "on-error": "#ffffff",
                "error-container": "#ffdad6",
                "on-error-container": "#93000a",

                // Other
                "outline": "#98A2B3",
                "outline-variant": "#E4E7EC",
                "border-strong": "#D0D5DD",
                "surface-tint": "#183B4E",
                "background": "#F7F8FA",
                "on-background": "#1D2939",
                "surface-variant": "#F2F4F7",
                "text-muted": "#98A2B3",
                "success": "#16A34A",
            },
            borderRadius: {
                "DEFAULT": "6px",
                "sm": "6px",
                "md": "8px",
                "lg": "10px",
                "xl": "14px",
                "full": "9999px"
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
                "sans": ["Inter", "ui-sans-serif", "system-ui", "sans-serif"],
                "serif": ["Inter", "ui-sans-serif", "system-ui", "sans-serif"],
                "h3": ["Inter", "ui-sans-serif", "system-ui", "sans-serif"],
                "label-md": ["Inter", "ui-sans-serif", "sans-serif"],
                "caption": ["Inter", "ui-sans-serif", "sans-serif"],
                "body-md": ["Inter", "ui-sans-serif", "sans-serif"],
                "body-lg": ["Inter", "ui-sans-serif", "sans-serif"],
                "h1": ["Inter", "ui-sans-serif", "system-ui", "sans-serif"],
                "h2": ["Inter", "ui-sans-serif", "system-ui", "sans-serif"]
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
