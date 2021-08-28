import React from 'react';
import { action } from '@storybook/addon-actions';

import Sidebar from '../widgets/Sidebar';
import { menuGroupData } from './MenuItemGroup.stories';
import { actionsData, selectedMenuItemData as menuItemData } from './MenuItem.stories';

const defaultSideBarData = [{
    isGroup: true,
    listItem: { ...menuGroupData }
}, {
    isGroup: false,
    listItem: { ...menuItemData, ...actionsData }
}];

const sideBarData = {
    default: defaultSideBarData,
    longGroupTitle: [
        ...defaultSideBarData,
        {
            isGroup: true,
            listItem: {
                ...menuGroupData,
                id: 'id2',
                groupTitle: 'Extremely Long Menu Group Title'
            }
        }
    ]
};

export default {
    component: Sidebar,
    title: 'Widgets/Globals/Sidebar',
    excludeStories: /.*Data$/,
};

const Template = args => <Sidebar { ...args } />;

export const Default = Template.bind({});
Default.args = {
    sideBarItems: sideBarData.default,
    onClickHandler: action('open-drawer'),
    isOpen: true
};

export const ClosedDrawer = Template.bind({});
ClosedDrawer.args = {
    sideBarItems: sideBarData.default,
    onClickHandler: action('open-drawer'),
    isOpen: false
};

export const LongMenuGroupTitle = Template.bind({});
LongMenuGroupTitle.args = {
    sideBarItems: sideBarData.longGroupTitle,
    onClickHandler: action('open-drawer'),
    isOpen: true
};