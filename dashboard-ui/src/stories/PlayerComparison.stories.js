import React from 'react';
import PlayerComparisonView from '../views/PlayerComparison';
import faker from 'faker';

export default {
    component: PlayerComparisonView,
    title: 'PlayerComparisonView',
    excludeStories: /.*Data$/
};

const getPlayerMetadata = () => ({
    name: faker.name.findName(),
    photo: faker.image.imageUrl()
});

const getPlayerAttributeData = () => ({
    technical: [ ...Array(10)].map((_, idx) => ({ name: faker.name.lastName(1), attr: Math.round(Math.random() * 20) })),
    physical:  [ ...Array(10)].map((_, idx) => ({ name: faker.name.lastName(1), attr: Math.round(Math.random() * 20) })),
    mental:  [ ...Array(10)].map((_, idx) => ({ name: faker.name.lastName(1), attr: Math.round(Math.random() * 20) })),
});

const playerData = {
    players: [{
        isSelected: true,
        orientation: 'LEFT',
        playerMetadata: getPlayerMetadata(),
        playerAttributes: getPlayerAttributeData()
    }, {
        isSelected: true,
        orientation: 'RIGHT',
        playerMetadata: getPlayerMetadata(),
        playerAttributes: getPlayerAttributeData()
    }]
};

export const Default = () => <PlayerComparisonView  { ...playerData } />;