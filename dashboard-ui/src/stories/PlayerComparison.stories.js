import React from 'react';
import PlayerComparisonView from '../views/PlayerComparison';
import faker from 'faker';

export default {
    component: PlayerComparisonView,
    title: 'PlayerComparisonView',
    excludeStories: /.*Data$/
};

const MAX_ATTR_VALUE = 20;

const getAttributesInGroup = (numAttributes) => {
    let attributesInGroup = [];
    for (let i=0; i<numAttributes; i++) {
        attributesInGroup.push({ name: faker.hacker.noun(), value: Math.round(Math.random() * MAX_ATTR_VALUE) });
    }
    return attributesInGroup;
};

const  getPlayerAttributeData = () => ([
    {
        groupName: 'Technical',
        attributesInGroup: getAttributesInGroup(10)
    }, {
        groupName: 'Physical',
        attributesInGroup: getAttributesInGroup(10)
    }, {
        groupName: 'Mental',
        attributesInGroup: getAttributesInGroup(10)
    }
]);

const getPlayerMetadata = () => ({
    name: faker.name.findName(),
    photo: faker.image.imageUrl()
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