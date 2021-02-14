import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';

import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';


import Layout from './Layout';
import { useThemePreference } from './context/themePreferenceProvider';
import { ChartOptionsProvider } from './context/chartOptionsProvider';

function App() {
    const queryClient = new QueryClient();
    const themePreference = useThemePreference();
    const theme = React.useMemo(() => createMuiTheme({
        palette: {
            type: themePreference
        }
    }), [themePreference]);

    return (
        <Router>
            <QueryClientProvider client={ queryClient }>
                <ThemeProvider theme={ theme }>
                    <ChartOptionsProvider>
                        <Layout />
                    </ChartOptionsProvider>
                </ThemeProvider>
                <ReactQueryDevtools initialIsOpen={false} />
            </QueryClientProvider>
        </Router>
    );
}

export default App;
