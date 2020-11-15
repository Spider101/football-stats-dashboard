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
    dob: faker.date.past(),
    club: faker.company.companyName(),
    country: faker.address.country(),
    photo: `${faker.image.people()}?random=${Math.round(Math.random() * 20)}`,
    clubLogo: `${faker.image.abstract()}?random=${Math.round(Math.random() * 20)}`,
    countryLogo: `${faker.image.avatar()}?random=${Math.round(Math.random() * 20)}`,
    age:  ' (' + faker.random.number({ 'min': 16, 'max': 35 }) + ' years old)'
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