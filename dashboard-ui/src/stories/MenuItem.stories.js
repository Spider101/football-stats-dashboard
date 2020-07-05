import React from 'react';
import { action } from '@storybook/addon-actions';

import MenuItem from '../components/MenuItem';
import Commute from '@material-ui/icons/Commute';

export const actionsData = {
    onSelectMenuItem: action('onSelectMenuItem')
};

export const menuItemData = {
    text: 'Menu Item Solo',
    icon: <Commute />
};

export default {
    component: MenuItem,
    title: 'MenuItem',
    excludeStories: /.*Data$/,
};

export const Default = () => <MenuItem { ...menuItemData }  { ...actionsData }/>;
