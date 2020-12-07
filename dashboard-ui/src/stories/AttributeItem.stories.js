import React from 'react';
import AttributeItem from '../components/AttributeItem';
import faker from 'faker';

import { getAttributeItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeItem,
    title: 'Components/PlayerAttributeTable/AttributeItem',
    excludeStories: /.*Data$/,
};

const highlightedAttribute = faker.hacker.noun();

export const Default = () => <AttributeItem { ...getAttributeItemData(faker.hacker.noun()) } />;

export const Highlighted = () => <AttributeItem { ...getAttributeItemData(highlightedAttribute,
    [ highlightedAttribute ]) } />;