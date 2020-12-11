import React from 'react';
import PlayerComparisonView from '../views/PlayerComparison';
import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparisonView,
    title: 'Views/PlayerComparisonView',
    excludeStories: /.*Data$/
};

const playerData = {
    players: [
        getPlayerData(getAttributeNamesList(3 * 10), 'LEFT'),
        getPlayerData(getAttributeNamesList(3 * 10), 'RIGHT')
    ]
};

export const Default = () => <PlayerComparisonView  { ...playerData } />;