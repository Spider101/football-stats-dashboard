import React from 'react';
import useMediaQuery from '@material-ui/core/useMediaQuery';

const ThemePreferenceContext = React.createContext();

function ThemePreferenceProvider({ children }) {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');

    return (
        <ThemePreferenceContext.Provider value={ prefersDarkMode }>
            { children }
        </ThemePreferenceContext.Provider>
    );
}

function useThemePreference() {
    const prefersDarkMode = React.useContext(ThemePreferenceContext);

    if (prefersDarkMode === undefined) {
        throw new Error('useThemePreference must be used inside ThemePreferenceProvider');
    }

    return prefersDarkMode ? 'dark' : 'light';
}

export { ThemePreferenceProvider, useThemePreference };