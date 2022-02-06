import { useMemo } from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';

import { createTheme, ThemeProvider } from '@material-ui/core/styles';

import Layout from './Layout';
import { useThemePreference } from './context/themePreferenceProvider';
import { ChartOptionsProvider } from './context/chartOptionsProvider';
import { AuthContextProvider } from './context/authProvider';
import { ClubContextProvider } from './context/clubProvider';
import { LookupDataContextProvider } from './context/LookupDataProvider';

function App() {
    const queryClient = new QueryClient();
    const themePreference = useThemePreference();
    const theme = useMemo(
        () =>
            createTheme({
                palette: {
                    type: themePreference,
                },
            }),
        [themePreference]
    );

    return (
        <Router>
            <QueryClientProvider client={queryClient}>
                <ThemeProvider theme={theme}>
                    <ChartOptionsProvider>
                        <AuthContextProvider>
                            {/* // TODO: 05/02/22 move this provider inside home page */}
                            <ClubContextProvider>
                                <LookupDataContextProvider>
                                    <Layout />
                                </LookupDataContextProvider>
                            </ClubContextProvider>
                        </AuthContextProvider>
                    </ChartOptionsProvider>
                </ThemeProvider>
                <ReactQueryDevtools initialIsOpen={false} />
            </QueryClientProvider>
        </Router>
    );
}

export default App;
