import React from 'react';
import PlayerComparisonView from '../views/PlayerComparison';
import { getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparisonView,
    title: 'Views/PlayerComparisonView/PlayerComparisonView',
    excludeStories: /.*Data$/
};

export const Default = () => <PlayerComparisonView  { ...getPlayerData() } />;