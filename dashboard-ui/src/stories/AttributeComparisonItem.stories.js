import React from 'react';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import faker from 'faker';

export const attrComparisonItemData = () => ({
    item: {
        series: [{
            name: faker.name.lastName(),
            data: [ Math.round(Math.random() * 20) ]
        }, {
            name: faker.name.lastName(),
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
