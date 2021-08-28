import React from 'react';
import PlayerProgressionView from '../views/PlayerProgressionView';

import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerProgressionView,
    title: 'Views/PlayerProgressionView'
};

const Template = args => <PlayerProgressionView { ...args } />;

export const Default = Template.bind({});
Default.args = getPlayerData(getAttributeNamesList(3 * 10), true);