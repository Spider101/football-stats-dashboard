import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';

import { moraleIconsMap, nationalityFlagMap } from '../utils';

import SquadHubView from '../views/SquadHubView';

import { getSquadHubPlayerData } from './utils/storyDataGenerators';

export default {
    component: SquadHubView,
    title: 'Views/SquadHubView'
};

const nations = nationalityFlagMap.map(entity => entity.nationality);
const moraleList = moraleIconsMap.map(entity => entity.morale);

export const Default = () => (
    <Router>
        <SquadHubView { ...getSquadHubPlayerData(10, nations, moraleList)  } />
    </Router>
);