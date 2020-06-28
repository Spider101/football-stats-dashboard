import React from "react";

import Sidebar from "../widgets/Sidebar";
import { menuGroupData } from "./MenuItemGroup.stories";
import { menuItemData} from "./MenuItem.stories";

const sideBarData = [{
    isGroup: true,
    listItem: { ...menuGroupData }
}, {
    isGroup: false,
    listItem: { ...menuItemData }
}];

export default {
    component: Sidebar,
    title: "Sidebar",
    excludeStories: /.*Data$/,
}

export const Default = () => <Sidebar sideBarItems={ sideBarData } />;
