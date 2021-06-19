import React from 'react';
import { action } from '@storybook/addon-actions';

import AppBarMenu from '../components/AppBarMenu';

const menuData = {
    menu: {
        title: 'Manchester United',
        teamColor: 'red',
    },
    onClickHandler: action('open-menu'),
    isOpen: false
};

export default {
    component: AppBarMenu,
    title: 'Components/Globals/AppBarMenu',
    excludeStories: /.*Data$/,
};

export const Default = () => <AppBarMenu { ...menuData } />;

export const MenuOpened = () => <AppBarMenu { ...{ ...menuData, isOpen: true } } />;
