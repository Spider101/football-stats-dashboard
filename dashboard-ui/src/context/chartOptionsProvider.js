import React from 'react';
import PropTypes from 'prop-types';

import { useThemePreference } from './themePreferenceProvider';

const ChartOptionsContext = React.createContext();

function ChartOptionsProvider({ children }) {
    const themePreference = useThemePreference();
    const globalChartOptions = {
        theme: { mode: themePreference },
        legend: { show: false },
        stroke: { width: 2 },
        title: { align: 'left', style: { fontFamily: 'Roboto' } }
    };

    return (
        <ChartOptionsContext.Provider value={ globalChartOptions }>
            { children }
        </ChartOptionsContext.Provider>
    );
}

ChartOptionsProvider.propTypes = {
    children: PropTypes.node
};

function useGlobalChartOptions() {
    const globalChartOptions = React.useContext(ChartOptionsContext);

    if (globalChartOptions === undefined) {
        throw new Error('useGlobalChartOptions must be used inside ChartOptionsProvider');
    }

    return globalChartOptions;
}

export { ChartOptionsProvider, useGlobalChartOptions };

