import React from 'react';
import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';
import { getAttributeLineData } from './utils/storyDataGenerators';

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
    playerAttributeProgressData: getAttributeLineData(10),
    playerOverallProgressData: getAttributeLineData(1)
};

export const Default = (args) => <PlayerProgressionCharts { ...args } />;
Default.args = {
    ...playerProgressionChartsData
}