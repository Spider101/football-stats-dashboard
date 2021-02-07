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

const menuGroupActionsData = {
    onCollapseMenuItemGroup: action('collapse-menu-group')
};

export default {
    component: MenuItemGroup,
    title: 'Widgets/Globals/MenuItemGroup',
    excludeStories: /.*Data$/,
};

export const Default = () => <MenuItemGroup menuGroup={ menuGroupData } { ...menuGroupActionsData }/>;

export const Collapsed =
    () => <MenuItemGroup menuGroup={{ ...menuGroupData, isCollapsed: true }} { ...menuGroupActionsData } />;
