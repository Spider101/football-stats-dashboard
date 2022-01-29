import PropTypes from 'prop-types';
import { StylesProvider } from '@material-ui/core';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from 'react-query';

const snapshotFriendlyClassNameGenerator = (rule, styleSheet) => `${styleSheet.options.classNamePrefix}-${rule.key}`;

const SnapshotFriendlyStylesProvider = ({ children }) => (
    <StylesProvider generateClassName={snapshotFriendlyClassNameGenerator}>{children}</StylesProvider>
);
SnapshotFriendlyStylesProvider.propTypes = {
    children: PropTypes.node
};

export const snapshotFriendlyRender = (ui, options = {}) =>
    render(ui, {
        wrapper: SnapshotFriendlyStylesProvider,
        ...options
    });

export const createQueryWrapper = children => {
    // creates a new QueryClient instance for each test
    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                retry: false
            }
        }
    });
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
};