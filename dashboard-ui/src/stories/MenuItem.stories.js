import React from 'react';
import { BrowserRouter as Router, Link } from 'react-router-dom';

import { action } from '@storybook/addon-actions';

import MenuItem from '../components/MenuItem';
import Commute from '@material-ui/icons/Commute';

export const actionsData = {
    onSelectMenuItem: action('onSelectMenuItem')
};

export const selectedMenuItemData = {
    text: 'Menu Item Solo',
    menuItemIndex: 0,
    icon: <Commute />
};

const unselectedMenuItemData = {
    ...selectedMenuItemData,
    menuItemIndex: 1
};

const selectedMenuItemDataWithRouting = {
    ...selectedMenuItemData,
    componentType: Link,
    routePath: '/dummyRoute'
};

export default {
    component: MenuItem,
    title: 'Components/Globals/MenuItem',
    excludeStories: /.*Data$/,
};


export const Selected = () => <MenuItem { ...selectedMenuItemData } />;

export const Unselected = () => <MenuItem { ...unselectedMenuItemData } />;

export const WithReactRouter = () => (
    <Router>
        <MenuItem {...selectedMenuItemDataWithRouting }  />
    </Router>
);
