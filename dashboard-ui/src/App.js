import React from 'react';

import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';

import { BrowserRouter as Router } from 'react-router-dom';

import Layout from './Layout';

function App() {
    const darkTheme = createMuiTheme({
        palette: {
            type: 'dark'
        }
    });

    return (
        <Router>
            <ThemeProvider theme={ darkTheme }>
                <Layout />
            </ThemeProvider>
        </Router>
    );
}

export default App;
