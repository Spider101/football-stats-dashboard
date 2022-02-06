import { ChartOptionsProvider } from '../src/context/chartOptionsProvider';
import { ThemePreferenceProvider } from '../src/context/themePreferenceProvider';
import { initialize, mswDecorator } from 'msw-storybook-addon';

// initialize msw
initialize({ onUnhandledRequest: 'bypass' });

export const decorators = [mswDecorator, Story =>
    <ThemePreferenceProvider>
        <ChartOptionsProvider>
            <Story />
        </ChartOptionsProvider>
    </ThemePreferenceProvider>
];