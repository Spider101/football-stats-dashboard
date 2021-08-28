import React from 'react';
import { action } from '@storybook/addon-actions';

import Sidebar from '../widgets/Sidebar';
import { Default as MenuGroup } from './MenuItemGroup.stories';
import { Unselected } from './MenuItem.stories';

export default {
    component: Sidebar,
    title: 'Widgets/Globals/Sidebar'
};

const defaultArgs = {
    sideBarItems: [{
        isGroup: true,
        listItem: MenuGroup.args.menuGroup
    }, {
        isGroup: false,
        listItem: Unselected.args
    }],
    onClickHandler: action('close-drawer'),
    isOpen: true
};

const Template = args => <Sidebar { ...args } />;

export const Default = Template.bind({});
Default.args = defaultArgs;

export const ClosedDrawer = Template.bind({});
ClosedDrawer.args = {
    ...defaultArgs,
    onClickHandler: action('open-drawer'),
    isOpen: false
};

export const LongMenuGroupTitle = Template.bind({});
LongMenuGroupTitle.args = {
    ...defaultArgs,
    sideBarItems: [
        ...defaultArgs.sideBarItems,
        {
            isGroup: true,
            listItem: {
                ...MenuGroup.args.menuGroup,
                id: 'id2',
                groupTitle: 'Extremely Long Menu Group Title'
            }
        }
    ],
};