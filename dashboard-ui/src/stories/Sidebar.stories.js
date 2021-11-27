import React from 'react';
import { action } from '@storybook/addon-actions';

import Sidebar from '../widgets/Sidebar';
import { Default as MenuGroup } from './MenuItemGroup.stories';
import { Unselected } from './MenuItem.stories';
import { MemoryRouter } from 'react-router';

export default {
    component: Sidebar,
    title: 'Widgets/Globals/Sidebar',
    parameters: {
        docs: {
            description: {
                component: 'Widget for composing multiple `MenuItem` and `MenuItemGroup` components together to'
                + ' represent the complete navigational entity for the application.'
            }
        }
    },
    decorators: [
        Story => (
            <MemoryRouter initialEntries={['/']}>
                <Story />
            </MemoryRouter>
        )
    ]
};

const Template = args => <Sidebar { ...args } />;

export const Default = Template.bind({});
Default.args = {
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

export const WithDisabledItems = Template.bind({});
WithDisabledItems.args = {
    ...Default.args,
    sideBarItems: [
        ...Default.args.sideBarItems,
        {
            isGroup: false,
            listItem: {
                ...Unselected.args,
                text: 'Disabled Menu Item',
                disabledPaths: ['/']
            }
        }
    ]
};

export const ClosedDrawer = Template.bind({});
ClosedDrawer.args = {
    ...Default.args,
    onClickHandler: action('open-drawer'),
    isOpen: false
};

export const LongMenuGroupTitle = Template.bind({});
LongMenuGroupTitle.args = {
    ...Default.args,
    sideBarItems: [
        ...Default.args.sideBarItems,
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