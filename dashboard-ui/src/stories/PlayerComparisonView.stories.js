import React from 'react';

import PlayerComparisonView from '../views/PlayerComparisonView';
import { filterControl } from './CardWithFilter.stories';
import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparisonView,
    title: 'Views/PlayerComparisonView'
};

const basePlayerData = getPlayerData(getAttributeNamesList(3 * 10));
const comparedPlayerData = getPlayerData(getAttributeNamesList(3 * 10));

const Template = args => <PlayerComparisonView { ...args } />;

export const Default = Template.bind({});

Default.args = {
    basePlayer: basePlayerData,
    comparedPlayer: comparedPlayerData,
    filterControl
};

export const SinglePlayer = Template.bind({});

SinglePlayer.args = {
    basePlayer: basePlayerData,
    comparedPlayer: null,
    filterControl
};