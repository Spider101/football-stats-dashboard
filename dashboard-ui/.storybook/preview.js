import { initialize, mswDecorator } from 'msw-storybook-addon';

// initialize msw
initialize({ onUnhandledRequest: 'bypass' });

export const decorators = [mswDecorator];