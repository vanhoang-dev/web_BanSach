// Design System - Modern Literary Interface
export const COLORS = {
    // Surface
    surface: '#fcf8fd',
    'surface-dim': '#dcd9de',
    'surface-bright': '#fcf8fd',
    'surface-container-lowest': '#ffffff',
    'surface-container-low': '#f6f2f7',
    'surface-container': '#f1ecf2',
    'surface-container-high': '#ebe7ec',
    'surface-container-highest': '#e5e1e6',
    'on-surface': '#1c1b1f',
    'on-surface-variant': '#47464f',
    'inverse-surface': '#313034',
    'inverse-on-surface': '#f3eff4',

    // Primary
    primary: '#070235',
    'on-primary': '#ffffff',
    'primary-container': '#1e1b4b',
    'on-primary-container': '#8683ba',
    'inverse-primary': '#c4c1fb',
    'primary-fixed': '#e3dfff',
    'primary-fixed-dim': '#c4c1fb',
    'on-primary-fixed': '#181445',
    'on-primary-fixed-variant': '#444173',

    // Secondary
    secondary: '#9d4300',
    'on-secondary': '#ffffff',
    'secondary-container': '#fd761a',
    'on-secondary-container': '#5c2400',
    'secondary-fixed': '#ffdbca',
    'secondary-fixed-dim': '#ffb690',
    'on-secondary-fixed': '#341100',
    'on-secondary-fixed-variant': '#783200',

    // Tertiary
    tertiary: '#160700',
    'on-tertiary': '#ffffff',
    'tertiary-container': '#371a00',
    'on-tertiary-container': '#ae7f59',
    'tertiary-fixed': '#ffdcc2',
    'tertiary-fixed-dim': '#f1bc91',
    'on-tertiary-fixed': '#2e1500',
    'on-tertiary-fixed-variant': '#633e1e',

    // Error
    error: '#ba1a1a',
    'on-error': '#ffffff',
    'error-container': '#ffdad6',
    'on-error-container': '#93000a',

    // Other
    outline: '#787680',
    'outline-variant': '#c8c5d0',
    'surface-tint': '#5b598c',
    background: '#fcf8fd',
    'on-background': '#1c1b1f',
    'surface-variant': '#e5e1e6',
};

export const TYPOGRAPHY = {
    h1: {
        fontFamily: 'Manrope',
        fontSize: '40px',
        fontWeight: '800',
        lineHeight: '1.2',
        letterSpacing: '-0.02em',
    },
    h2: {
        fontFamily: 'Manrope',
        fontSize: '32px',
        fontWeight: '700',
        lineHeight: '1.3',
        letterSpacing: '-0.01em',
    },
    h3: {
        fontFamily: 'Manrope',
        fontSize: '24px',
        fontWeight: '700',
        lineHeight: '1.4',
    },
    'body-lg': {
        fontFamily: 'Manrope',
        fontSize: '18px',
        fontWeight: '400',
        lineHeight: '1.6',
    },
    'body-md': {
        fontFamily: 'Manrope',
        fontSize: '16px',
        fontWeight: '400',
        lineHeight: '1.6',
    },
    'label-md': {
        fontFamily: 'Manrope',
        fontSize: '14px',
        fontWeight: '600',
        lineHeight: '1.2',
    },
    caption: {
        fontFamily: 'Manrope',
        fontSize: '12px',
        fontWeight: '500',
        lineHeight: '1.4',
    },
};

export const SPACING = {
    unit: '4px',
    'stack-sm': '8px',
    'stack-md': '16px',
    'stack-lg': '32px',
    'section-gap': '64px',
    gutter: '24px',
    'margin-mobile': '16px',
    'container-max': '1280px',
};

export const BORDER_RADIUS = {
    sm: '0.25rem',
    DEFAULT: '0.5rem',
    md: '0.75rem',
    lg: '1rem',
    xl: '1.5rem',
    full: '9999px',
};

export const BREAKPOINTS = {
    xs: '0px',
    sm: '640px',
    md: '768px',
    lg: '1024px',
    xl: '1280px',
    '2xl': '1536px',
};
