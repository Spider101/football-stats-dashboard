import React from 'react';
import { BrowserRouter as Router, Link } from 'react-router-dom';

import { action } from '@storybook/addon-actions';

import MenuItem from '../components/MenuItem';
import Commute from '@material-ui/icons/Commute';

export default {
    component: MenuItem,
    title: 'Components/Globals/MenuItem'
};

const selectedMenuItemArgs = {
    text: 'Menu Item Solo',
    selectedItem: 0,
    menuItemIndex: 0,
    icon: <Commute />,
    handleMenuItemClick: action('onSelectMenuItem')

};

const Template = args => <MenuItem {...args} />;

export const Selected = Template.bind({});
Selected.args = selectedMenuItemArgs;

export const Unselected = Template.bind({});
Unselected.args = {
    ...selectedMenuItemArgs,
    selectedItem: -1,
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
    ...selectedMenuItemArgs,
    componentType: Link,
    routePath: '/dummyRoute'
};