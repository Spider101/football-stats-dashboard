import HomeIcon from '@material-ui/icons/Home';
import GroupIcon from '@material-ui/icons/Group';

import SquadHub from '../pages/SquadHub';
import Home from '../pages/Home';
import Player from '../pages/Player';
import Club from '../pages/Club';

const routingData = [{
    text: 'Home',
    icon: <HomeIcon />,
    routePath: '/',
    component: Home,
    isExact: true,
    showInSidebar: true
}, {
    routePath: '/club/:clubId',
    component: Club,
    isExact: false,
    showInSidebar: false
}, {
    text: 'SquadHub',
    icon: <GroupIcon />,
    routePath: '/squadHub',
    component: SquadHub,
    isExact: false,
    disabledPaths: ['/'],
    showInSidebar: true
}, {
    routePath: '/player/:playerId',
    component: Player,
    isExact: false,
    showInSidebar: false
}];

export default routingData;

