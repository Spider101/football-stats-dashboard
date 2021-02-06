import React from 'react';

import PlayerComparisonView from '../views/PlayerComparisonView';
import { Default as CardWithFilter } from './CardWithFilter.stories';
import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparisonView,
    title: 'Views/PlayerComparisonView',
    excludeStories: /.*Data$/
};

const basePlayerData = getPlayerData(getAttributeNamesList(3 * 10));
const comparedPlayerData = getPlayerData(getAttributeNamesList(3 * 10));

const Template = args => <PlayerComparisonView { ...args } />;

export const Default = Template.bind({});

Default.args = {
    basePlayer: basePlayerData,
    comparedPlayer: comparedPlayerData
};

export const SinglePlayer = Template.bind({});

SinglePlayer.args = {
    basePlayer: basePlayerData,
    comparedPlayer: null,
    cardWithFilter: <CardWithFilter { ...CardWithFilter.args } />
};