import React from 'react';
import { Link } from 'react-router-dom';

import Commute from '@material-ui/icons/Commute';

import Sidebar from './widgets/Sidebar';
import { HomeRoute, SquadHubRoute } from "./routes";

const Navigator = () => {
    const sideBarData = {
        sideBarItems: [{
            isGroup: false,
            listItem: {
                text: 'Home',
                icon: <Commute />,
                onSelectMenuItem: x => x,
                componentType: Link,
                routePath: HomeRoute    
            }
        }, {
            isGroup: false,
            listItem: {
                text: 'SquadHub',
                icon: <Commute />,
                onSelectMenuItem: x => x,
                componentType: Link,
                routePath: SquadHubRoute     
            }
        }]
    };

    return (
        <Sidebar { ...sideBarData } />
    );
};

export default Navigator;
