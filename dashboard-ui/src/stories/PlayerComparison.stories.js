import React from 'react';
import PlayerComparisonView from '../views/PlayerComparison';
import faker from 'faker';

import { getPlayerMetadata } from './PlayerBioCard.stories';

export default {
    component: PlayerComparisonView,
    title: 'Views/PlayerComparisonView/PlayerComparisonView',
    excludeStories: /.*Data$/
};

const MAX_ATTR_VALUE = 20;

const getAttributesInCategory = (numAttributes) => (
    [ ...Array(numAttributes)].map(() => (
        { name: faker.hacker.noun(), value: Math.round(Math.random() * MAX_ATTR_VALUE) }
    ))
);

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