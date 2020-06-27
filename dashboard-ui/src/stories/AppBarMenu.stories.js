import React from "react";
// import { action } from "@storybook/addon-actions";

import AppBarMenu from "../components/AppBarMenu";

export const menuData = {
    title: "Manchester United",
    teamColor: "red"
};

export default {
    component: AppBarMenu,
    title: "AppBarMenu",
    excludeStories: /.*Data$/,
};

export const Default = () => <AppBarMenu menu={{...menuData}}/>;

