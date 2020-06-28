import React from "react";
import { action } from '@storybook/addon-actions';

import MenuItem from "../components/MenuItem";
import SendIcon from "@material-ui/icons/Send";

export const actionsData = {
    onSelectMenuItem: action("onSelectMenuItem")
};

export const menuItemData = {
    text: "Menu Item Solo",
    icon: <SendIcon />
};

export default {
    component: MenuItem,
    title: "MenuItem",
    excludeStories: /.*Data$/,
}

export const Default = () => <MenuItem { ...menuItemData }  { ...actionsData }/>;
