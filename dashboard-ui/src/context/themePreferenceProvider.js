import { createContext, useContext } from 'react';
import PropTypes from 'prop-types';

import useMediaQuery from '@material-ui/core/useMediaQuery';

const ThemePreferenceContext = createContext();

function ThemePreferenceProvider({ children }) {
    // TODO: 03/07/22 this can be run just once on app start up. Don't need the context provider since the value is
    // available through the mui theme.palette.type property after initializing the theme with the preference.
    // Remove this context provider hook
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');

    return (
        <ThemePreferenceContext.Provider value={ prefersDarkMode }>
            { children }
        </ThemePreferenceContext.Provider>
    );
}

ThemePreferenceProvider.propTypes = {
    children: PropTypes.node
};

function useThemePreference() {
    const prefersDarkMode = useContext(ThemePreferenceContext);

    if (prefersDarkMode === undefined) {
        throw new Error('useThemePreference must be used inside ThemePreferenceProvider');
    }

    return prefersDarkMode ? 'dark' : 'light';
}

export { ThemePreferenceProvider, useThemePreference };