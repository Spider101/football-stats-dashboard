import React from 'react';
import OverallProgressChart from '../components/OverallProgressChart';
import { getPlayerProgressionData, MAX_OVERALL_VALUE } from './utils/storyDataGenerators';

export default {
    component: OverallProgressChart,
    title: 'Components/PlayerProgressionView/OverallProgressChart',
    excludeStories: /.*Data$/,
    argTypes: {
        attributeData: {
            name: 'Overall Progression Data',
            control: { type: 'object' }
        }
    }
};

export const Default = (args) => <OverallProgressChart { ...args } />;

Default.args = {
    overallData: getPlayerProgressionData(1, 'Player Ability', MAX_OVERALL_VALUE)
};