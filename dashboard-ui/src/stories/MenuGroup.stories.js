import React from "react";

import DraftsIcon from "@material-ui/icons/Drafts";
import SendIcon from "@material-ui/icons/Send";
import MenuGroup from "../widgets/MenuGroup";
import { taskData } from "./MenuItem.stories";

export const menuGroupData = {
    groupTitle: "Menu Group Title",
    menuItems: [{
        ...taskData,
        text: "Menu Item A",
        icon: <SendIcon />
    }, {
        ...taskData,
        text: "Menu Item B",
        icon: <DraftsIcon />,
        onSelectTask: taskData.onSelectTask
    }],
    isCollapsed: false
};

export default {
    component: MenuGroup,
    title: "MenuGroup",
    excludeStories: /.*Data$/,
};

export const Default = () => <MenuGroup menuGroup={{ ...menuGroupData }} { ...taskData }/>;

export const Collapsed = () => <MenuGroup menuGroup={{ ...menuGroupData, isCollapsed: true }} />;
