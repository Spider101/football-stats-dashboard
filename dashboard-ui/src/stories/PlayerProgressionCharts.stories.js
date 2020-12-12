import React from 'react';
import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';
import { getPlayerProgressionData, MAX_ATTR_VALUE, MAX_OVERALL_VALUE } from './utils/storyDataGenerators';

export default {
    component: PlayerProgressionCharts,
    title: 'Widgets/PlayerProgressionView/PlayerProgressionCharts',
    excludeStories: /.*Data$/,
    argTypes: {
        playerAttributeProgressData: {
            name: 'Attribute Progression Data',
            control: { type: 'object' }
        },
        playerOverallProgressData: {
            name: 'Overall Progression Data',
            control: { type: 'object' }
        }
    }
};

const playerProgressionChartsData = {
    playerAttributeProgressData: {
        attributeData: getPlayerProgressionData(10, null, MAX_ATTR_VALUE)
    },
    playerOverallProgressData: {
        overallData: getPlayerProgressionData(1, 'Player Ability', MAX_OVERALL_VALUE)
    }
};

export const Default = (args) => <PlayerProgressionCharts { ...args } />;

Default.args = {
    ...playerProgressionChartsData
};