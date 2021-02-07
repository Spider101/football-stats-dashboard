import React from 'react';
import { ChartOptionsProvider } from '../src/context/chartOptionsProvider';
import { ThemePreferenceProvider } from '../src/context/themePreferenceProvider';

export const decorators = [(Story) =>
    <ThemePreferenceProvider>
        <ChartOptionsProvider>
            <Story />
        </ChartOptionsProvider>
    </ThemePreferenceProvider>
];