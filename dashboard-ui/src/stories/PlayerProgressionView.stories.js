import React from 'react';
import PlayerProgressionView from '../views/PlayerProgressionView';

import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerProgressionView,
    title: 'Views/PlayerProgressionView',
    excludeStories: /.*Data$/
};

const playerData = getPlayerData(getAttributeNamesList(3 * 10), true);

export const Default = () => <PlayerProgressionView { ...playerData } />;