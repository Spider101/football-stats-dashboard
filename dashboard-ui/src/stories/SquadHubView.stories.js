import React from 'react';
import { moraleIconsMap, nationalityFlagMap } from '../utils';

import SquadHubView from '../views/SquadHubView';

import { getSquadHubPlayerData } from './utils/storyDataGenerators';

export default {
    component: SquadHubView,
    title: 'Views/SquadHubView',
    excludeStories: /.*Data$/
};

const nations = nationalityFlagMap.map(entity => entity.nationality);
const moraleList = moraleIconsMap.map(entity => entity.morale);

export const Default = () => <SquadHubView { ...getSquadHubPlayerData(10, nations, moraleList)  } />;