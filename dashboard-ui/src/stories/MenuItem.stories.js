import React from 'react';
import { BrowserRouter as Router, Link } from 'react-router-dom';

import { action } from '@storybook/addon-actions';

import MenuItem from '../components/MenuItem';
import Commute from '@material-ui/icons/Commute';

export const actionsData = {
    onSelectMenuItem: action('onSelectMenuItem')
};

export default {
    component: MenuItem,
    title: 'Components/Globals/MenuItem',
    excludeStories: /.*Data$/
};

export const selectedMenuItemData = {
    text: 'Menu Item Solo',
    selectedItem: 0,
    menuItemIndex: 0,
    icon: <Commute />
};

const Template = args => <MenuItem {...args} />;

export const Selected = Template.bind({});
Selected.args = selectedMenuItemData;

export const Unselected = Template.bind({});
Unselected.args = {
    ...selectedMenuItemData,
    selectedItem: 1,
    menuItemIndex: 0
};

export const WithReactRouter = Template.bind({});
WithReactRouter.decorators = [
    Story => (
        <Router>
            <Story />
        </Router>
    )
];
WithReactRouter.args = {
    ...selectedMenuItemData,
    componentType: Link,
    routePath: '/dummyRoute'
};