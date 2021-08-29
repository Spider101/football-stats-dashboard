import React from 'react';
import { BrowserRouter as Router, Link } from 'react-router-dom';

import { action } from '@storybook/addon-actions';

import MenuItem from '../components/MenuItem';
import Commute from '@material-ui/icons/Commute';

export default {
    component: MenuItem,
    title: 'Components/Globals/MenuItem'
};

const Template = args => <MenuItem {...args} />;

export const Unselected = Template.bind({});
Unselected.args = {
    text: 'Menu Item Solo',
    selectedItem: -1,
    menuItemIndex: 0,
    icon: <Commute />,
    handleMenuItemClick: action('onSelectMenuItem')
};

export const Selected = Template.bind({});
Selected.args = {
    ...Unselected.args,
    selectedItem: Unselected.args.menuItemIndex
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
    ...Unselected.args,
    componentType: Link,
    routePath: '/dummyRoute'
};