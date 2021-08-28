import React from 'react';
import { action } from '@storybook/addon-actions';

import Code from '@material-ui/icons/Code';
import Ballot from '@material-ui/icons/Ballot';
import Assessment from '@material-ui/icons/Assessment';
import MenuItemGroup from '../widgets/MenuItemGroup';
import { actionsData } from './MenuItem.stories';

export const menuGroupData = {
    id: 'id1',
    groupTitle: 'Menu Group Title',
    groupIcon: <Assessment />,
    menuItems: [{
        ...actionsData,
        selectedItem: -1,
        menuItemIndex: 0,
        text: 'Menu Item A',
        icon: <Ballot />
    }, {
        ...actionsData,
        selectedItem: -1,
        menuItemIndex: 1,
        text: 'Menu Item B',
        icon: <Code />
    }],
    isCollapsed: false
};

export default {
    component: MenuItemGroup,
    title: 'Widgets/Globals/MenuItemGroup',
    excludeStories: /.*Data$/
};

const Template = args => <MenuItemGroup { ...args } />;

export const Default = Template.bind({});
Default.args = {
    menuGroup: menuGroupData,
    onCollapseMenuItemGroup: action('collapse-menu-group')
};

export const Collapsed = Template.bind({});
Collapsed.args = {
    menuGroup: {
        ...menuGroupData,
        isCollapsed: true
    },
    onCollapseMenuItemGroup: action('collapse-menu-group')
};
