import React from 'react';
import { action } from '@storybook/addon-actions';

import Code from '@material-ui/icons/Code';
import Ballot from '@material-ui/icons/Ballot';
import Assessment from '@material-ui/icons/Assessment';
import MenuItemGroup from '../widgets/MenuItemGroup';
import { Unselected } from './MenuItem.stories';

export default {
    component: MenuItemGroup,
    title: 'Widgets/Globals/MenuItemGroup'
};

const Template = args => <MenuItemGroup { ...args } />;

export const Default = Template.bind({});
Default.args = {
    menuGroup: {
        id: 'id1',
        groupTitle: 'Menu Group Title',
        groupIcon: <Assessment />,
        menuItems: [{
            ...Unselected.args,
            text: 'Menu Item A',
            icon: <Ballot />
        }, {
            ...Unselected.args,
            menuItemIndex: 1,
            text: 'Menu Item B',
            icon: <Code />
        }],
        isCollapsed: false,
    },
    onCollapseMenuItemGroup: action('collapse-menu-group')
};

export const Collapsed = Template.bind({});
Collapsed.args = {
    ...Default.args,
    menuGroup: {
        ...Default.args.menuGroup,
        isCollapsed: true
    }
};
