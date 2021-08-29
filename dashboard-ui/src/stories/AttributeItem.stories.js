import React from 'react';
import faker from 'faker';

import AttributeItem from '../components/AttributeItem';

import { getAttributeItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeItem,
    title: 'Components/PlayerAttributeTable/AttributeItem'
};

const highlightedAttribute = faker.hacker.noun();

const Template = args => <AttributeItem { ...args } />;

export const Default = Template.bind({});
Default.args = getAttributeItemData(faker.hacker.noun());

export const Highlighted = Template.bind({});
Highlighted.args = getAttributeItemData(highlightedAttribute, [ highlightedAttribute] );