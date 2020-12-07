import React from 'react';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import faker from 'faker';

import { getAttrComparisonItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonItem,
    title: 'Components/PlayerComparisonView/AttributeComparisonTable/AttributeComparisonItem',
    excludeStories: /.*Data$/,
};

const highlightedAttribute = faker.hacker.noun();

export const Default = () => <AttributeComparisonItem { ...getAttrComparisonItemData(faker.hacker.noun()) } />;

export const Highlighted = () => <AttributeComparisonItem { ...getAttrComparisonItemData(highlightedAttribute,
    [ highlightedAttribute ]) } />;
