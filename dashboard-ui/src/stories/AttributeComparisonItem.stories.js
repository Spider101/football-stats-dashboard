import React from 'react';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import faker from 'faker';

export const attrComparisonItemData = () => ({
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
    title: 'AttributeComparisonItem',
    excludeStories: /.*Data$/,
};

export const Default = () => <AttributeComparisonItem { ...attrComparisonItemData() } />;

export const Highlighted = () => <AttributeComparisonItem { ...attrComparisonItemData() } isHighlighted={ true }/>;
