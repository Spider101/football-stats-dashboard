// export const HomeRoute = '/';
// export const SquadHubRoute = '/squadHub';
import React from 'react';

import HomeIcon from '@material-ui/icons/Home';
import GroupIcon from '@material-ui/icons/Group';

import SquadHub from './pages/SquadHub';
import Home from './pages/Home';

const sideBarData = [{
    text: 'Home',
    icon: <HomeIcon />,
    routePath: '/',
    component: Home
}, {
    text: 'SquadHub',
    icon: <GroupIcon />,
    routePath: '/squadHub',
    component: SquadHub
}];

export default sideBarData;

