import React from 'react';
import { BrowserRouter as Router, Link } from 'react-router-dom';

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

const menuItemDataWithRouting = {
    ...menuItemData,
    componentType: Link,
    routePath: '/dummyRoute'
};

const actionsDataWithRouting = {
    onSelectMenuItem: action('onSelectMenuItem for React Router')
};

export default {
    component: MenuItem,
    title: 'Components/Globals/MenuItem',
    excludeStories: /.*Data$/,
};

export const Default = () => <MenuItem { ...menuItemData }  { ...actionsData }/>;

export const WithReactRouter = () => (
    <Router>
        <MenuItem {...menuItemDataWithRouting } { ...actionsDataWithRouting } />
    </Router>
);
