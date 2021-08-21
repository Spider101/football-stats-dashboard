import React from 'react';
import { action } from '@storybook/addon-actions';

import AppBarMenu from '../components/AppBarMenu';
import { AuthContextProvider } from '../context/authProvider';
import { QueryClient, QueryClientProvider } from 'react-query';

const menuData = {
    menu: {
        title: 'Manchester United',
        teamColor: 'red'
    },
    onClickHandler: action('open-menu'),
    isOpen: false
};

export default {
    component: AppBarMenu,
    title: 'Components/Globals/AppBarMenu',
    excludeStories: /.*Data$/,
    decorators: [
        Story => (
            <QueryClientProvider client={new QueryClient()}>
                <AuthContextProvider>
                    <Story />
                </AuthContextProvider>
            </QueryClientProvider>
        )
    ]
};

const Template = args => <AppBarMenu {...args} />;

export const Default = Template.bind({});
Default.args = {
    ...menuData
};

export const MenuOpened = Template.bind({});
MenuOpened.args = {
    ...menuData,
    isOpen: true
};
