import React from 'react';

import HomeIcon from '@material-ui/icons/Home';
import GroupIcon from '@material-ui/icons/Group';
import SwapHorizIcon from '@material-ui/icons/SwapHoriz';

import SquadHub from './pages/SquadHub';
import Home from './pages/Home';
import Player from './pages/Player';
import Transfers from './pages/Transfers';

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
    text: 'Transfer Center',
    icon: <SwapHorizIcon />,
    component: Transfers,
    isExact: true,
    showInSidebar: true
}, {
    routePath: '/player/:playerId',
    component: Player,
    isExact: false,
    showInSidebar: false
}];

export default sideBarData;

