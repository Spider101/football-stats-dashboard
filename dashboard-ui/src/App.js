import React from 'react';

import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';

import { BrowserRouter as Router } from 'react-router-dom';

import Layout from './Layout';
import { useThemePreference } from './context/themePreferenceProvider';
import { ChartOptionsProvider } from './context/chartOptionsProvider';

function App() {
    const themePreference = useThemePreference();
    const theme = React.useMemo(() => createMuiTheme({
        palette: {
            type: themePreference
        }
    }), [themePreference]);

    return (
        <Router>
            <ThemeProvider theme={ theme }>
                <ChartOptionsProvider>
                    <Layout />
                </ChartOptionsProvider>
            </ThemeProvider>
        </Router>
    );
}

export default App;
