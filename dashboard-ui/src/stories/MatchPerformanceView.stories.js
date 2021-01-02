import React from 'react';

import MatchPerformanceView from '../views/MatchPerformanceView';

import { getMatchPerformanceBreakDown } from './utils/storyDataGenerators';

export default {
    component: MatchPerformanceView,
    title: 'Views/MatchPerformanceView',
    excludeStories: /.*Data$/
};

const playerPerformanceData = {
    playerPerformance: getMatchPerformanceBreakDown(5, 10)
};

export const Default = () => <MatchPerformanceView { ...playerPerformanceData } />;