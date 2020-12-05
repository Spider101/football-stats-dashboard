import React from 'react';
import AttributeItem from '../components/AttributeItem';
import faker from 'faker';

export default {
    component: AttributeItem,
    title: 'Components/PlayerAttributeTable/AttributeItem',
    excludeStories: /.*Data$/,
};

export const getAttributeItemData = (isHighlighted = false) => ({
    attributeName: faker.hacker.noun(),
    attributeValue: Math.round(Math.random() * 20),
    isHighlighted
});

export const Default = () => <AttributeItem { ...getAttributeItemData() } />;

export const Highlighted = () => <AttributeItem { ...getAttributeItemData(true) } />