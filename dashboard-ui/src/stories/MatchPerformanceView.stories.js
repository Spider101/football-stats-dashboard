import React from 'react';

import MatchPerformanceView from '../views/MatchPerformanceView';

import { getMatchPerformanceBreakDown } from './utils/storyDataGenerators';

export default {
    component: MatchPerformanceView,
    title: 'Views/MatchPerformanceView'
};

const Template = args => <MatchPerformanceView { ...args } />;
export const Default = Template.bind({});
Default.args = {
    playerPerformance: getMatchPerformanceBreakDown(5, 10)
};