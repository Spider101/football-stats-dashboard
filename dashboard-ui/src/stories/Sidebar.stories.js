import React from 'react';

import Sidebar from '../widgets/Sidebar';
import { menuGroupData } from './MenuItemGroup.stories';
import { actionsData, menuItemData } from './MenuItem.stories';

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
    title: 'Sidebar',
    excludeStories: /.*Data$/,
};

export const Default = () => <Sidebar sideBarItems={ sideBarData.default } />;

export const LongMenuGroupTitle = () => <Sidebar sideBarItems={ sideBarData.longGroupTitle } />;
