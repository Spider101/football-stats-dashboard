import React from 'react';
import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';
import { getPlayerProgressionData, MAX_ATTR_VALUE, MAX_OVERALL_VALUE } from './utils/storyDataGenerators';

export default {
    component: PlayerProgressionCharts,
    title: 'Widgets/PlayerProgressionView/PlayerProgressionCharts',
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

const Template = (args) => <PlayerProgressionCharts { ...args } />;

export const Default = Template.bind({});
Default.args = {
    playerAttributeProgressData: {
        attributeData: getPlayerProgressionData(10, null, MAX_ATTR_VALUE)
    },
    playerOverallProgressData: {
        overallData: getPlayerProgressionData(1, 'Player Ability', MAX_OVERALL_VALUE)
    }
};