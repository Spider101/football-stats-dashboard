import React from 'react';
import PlayerComparison from '../widgets/PlayerComparison';
import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparison,
    title: 'Widgets/PlayerComparisonView/PlayerComparison'
};

const Template = args => <PlayerComparison { ...args } />;

export const Default = Template.bind({});
export const SinglePlayer = Template.bind({});

Default.args = {
    players: [
        getPlayerData(getAttributeNamesList(3 * 10)),
        getPlayerData(getAttributeNamesList(3 * 10))
    ]
};

SinglePlayer.args = {
    players: [ getPlayerData(getAttributeNamesList(3 * 10)) ]
};