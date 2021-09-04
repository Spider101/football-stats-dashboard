import React from 'react';
import { BrowserRouter as Router, Link } from 'react-router-dom';

import { action } from '@storybook/addon-actions';

import MenuItem from '../components/MenuItem';
import Commute from '@material-ui/icons/Commute';

export default {
    component: MenuItem,
    title: 'Components/Globals/MenuItem',
    argTypes: {
        componentType: { control: '' },
        icon: { control: '' }
    },
    parameters: {
        docs: {
            description: {
                component: 'UI Component for the smallest functional block used in composing the side bar menu.'
                + 'It allows navigation to various views within the application. It has two states - _Selected_ and'
                + ' _Unselected_. The former is utilized when the user navigates to a page/view matching the MenuItem.'
                + ' The latter is the default state when the MenuItem does not match the current page/view.'
            }
        }
    }
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