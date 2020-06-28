import React from "react";

import DraftsIcon from '@material-ui/icons/Drafts';
import SendIcon from '@material-ui/icons/Send';
import MenuGroup from "../widgets/MenuGroup";

export const menuGroupData = {
    groupTitle: "Menu Group Title",
    menuItems: [{
        text: "Menu Item A",
        icon: <SendIcon />
    }, {
        text: "Menu Item B",
        icon: <DraftsIcon />
    }],
    isCollapsed: false
};

export default {
    component: MenuGroup,
    title: "MenuGroup",
    excludeStories: /.*Data$/,
};

export const Default = () => <MenuGroup menuGroup={{ ...menuGroupData }} />;

export const Collapsed = () => <MenuGroup menuGroup={{ ...menuGroupData, isCollapsed: true }} />;
