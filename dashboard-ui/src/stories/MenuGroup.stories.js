import React from "react";
import { action } from '@storybook/addon-actions';

import DraftsIcon from "@material-ui/icons/Drafts";
import SendIcon from "@material-ui/icons/Send";
import MenuGroup from "../widgets/MenuGroup";
import { actionsData } from "./MenuItem.stories";

export const menuGroupData = {
    id: "id1",
    groupTitle: "Menu Group Title",
    menuItems: [{
        ...actionsData,
        text: "Menu Item A",
        icon: <SendIcon />
    }, {
        ...actionsData,
        text: "Menu Item B",
        icon: <DraftsIcon />
    }],
    isCollapsed: false
};

const menuGroupActionsData = {
    onCollapseMenuItemGroup: action("onCollapseMenuItemGroup")
};

export default {
    component: MenuGroup,
    title: "MenuGroup",
    excludeStories: /.*Data$/,
};

export const Default = () => <MenuGroup menuGroup={ menuGroupData } { ...menuGroupActionsData }/>;

export const Collapsed = () => <MenuGroup menuGroup={{ ...menuGroupData, isCollapsed: true }} { ...menuGroupActionsData } />;
