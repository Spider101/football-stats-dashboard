import React from 'react';

import HomeIcon from '@material-ui/icons/Home';
import GroupIcon from '@material-ui/icons/Group';

import SquadHub from './pages/SquadHub';
import Home from './pages/Home';
import Player from './pages/Player';

const sideBarData = [{
    text: 'Home',
    icon: <HomeIcon />,
    routePath: '/',
    component: Home,
    isExact: true,
    showInSidebar: true
}, {
    text: 'SquadHub',
    icon: <GroupIcon />,
    routePath: '/squadHub',
    component: SquadHub,
    isExact: false,
    showInSidebar: true
}, {
    routePath: '/player/:id',
    component: Player,
    isExact: false,
    showInSidebar: false
}];

export default sideBarData;
