import React from 'react';
import PlayerBioCard from '../components/PlayerBioCard';
import { getPlayerMetadata } from './utils/storyDataGenerators';

export default {
    component: PlayerBioCard,
    title: 'Components/PlayerComparisonView/PlayerBioCard',
    excludeStories: /.*data$/
};

export const Default = () => <PlayerBioCard { ...getPlayerMetadata() } />;