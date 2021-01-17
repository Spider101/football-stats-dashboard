import React from 'react';

import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';

import { BrowserRouter as Router } from 'react-router-dom';

import Layout from './Layout';
import useMediaQuery from '@material-ui/core/useMediaQuery';

function App() {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const theme = React.useMemo(() => createMuiTheme({
        palette: {
            type: prefersDarkMode ? 'dark' : 'light'
        }
    }), [prefersDarkMode]);

    return (
        <Router>
            <ThemeProvider theme={ theme }>
                <Layout />
            </ThemeProvider>
        </Router>
    );
}

export default App;
