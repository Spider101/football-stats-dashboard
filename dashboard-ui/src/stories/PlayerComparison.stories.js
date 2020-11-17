import React from 'react';
import PlayerComparisonView from '../views/PlayerComparison';
import faker from 'faker';

export default {
    component: PlayerComparisonView,
    title: 'PlayerComparisonView',
    excludeStories: /.*Data$/
};

const MAX_ATTR_VALUE = 20;

const getAttributesInCategory = (numAttributes) => {
    let attributesInCategory = [];
    for (let i=0; i<numAttributes; i++) {
        attributesInCategory.push({ name: faker.hacker.noun(), value: Math.round(Math.random() * MAX_ATTR_VALUE) });
    }
    return attributesInCategory;
};

const  getPlayerAttributeCategoryData = () => ([
    {
        categoryName: 'Technical',
        attributesInCategory: getAttributesInCategory(10)
    }, {
        categoryName: 'Physical',
        attributesInCategory: getAttributesInCategory(10)
    }, {
        categoryName: 'Mental',
        attributesInCategory: getAttributesInCategory(10)
    }
]);

const getPlayerAttributeGroupData = (numAttributes) => ([
    {
        groupName: 'Defending',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Speed',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Vision',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Attacking',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Aerial',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
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
        playerAttributes: {
            attributeCategories: getPlayerAttributeCategoryData(),
            attributeGroups: getPlayerAttributeGroupData(10)
        }
    }, {
        isSelected: true,
        orientation: 'RIGHT',
        playerMetadata: getPlayerMetadata(),
        playerAttributes: {
            attributeCategories: getPlayerAttributeCategoryData(),
            attributeGroups: getPlayerAttributeGroupData(10)
        }
    }]
};

export const Default = () => <PlayerComparisonView  { ...playerData } />;