import React from 'react';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import faker from 'faker';

export const getAttrComparisonItemData = () => ({
    attrComparisonItem: {
        attrValues: [{
            name: faker.name.lastName(1),
            data: [ Math.round(Math.random() * 20) ]
        }, {
            name: faker.name.lastName(1),
            data: [ -1 * Math.round(Math.random() * 20) ]
        }],
        label: faker.hacker.noun()
    },
    isHighlighted: false
});

export default {
    component: AttributeComparisonItem,
    title: 'Components | PlayerComparisonView/AttributeComparisonTable/AttributeComparisonItem',
    excludeStories: /.*Data$/,
};

export const Default = () => <AttributeComparisonItem { ...getAttrComparisonItemData() } />;

export const Highlighted = () => <AttributeComparisonItem { ...getAttrComparisonItemData() } isHighlighted={ true }/>;
