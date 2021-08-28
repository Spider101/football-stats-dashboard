import React from 'react';
import PlayerBioCard from '../components/PlayerBioCard';
import { getPlayerMetadata } from './utils/storyDataGenerators';

export default {
    component: PlayerBioCard,
    title: 'Components/PlayerComparisonView/PlayerBioCard'
};

const Template = args => <PlayerBioCard { ...args } />;

export const Default = Template.bind({});
Default.args = getPlayerMetadata();