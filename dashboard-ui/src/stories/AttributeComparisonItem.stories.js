import React from 'react';
import AttributeComparisonItem from '../components/AttributeComparisonItem';

import { getAttrComparisonItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonItem,
    title: 'Components/PlayerComparisonView/AttributeComparisonTable/AttributeComparisonItem',
    excludeStories: /.*Data$/,
};

export const Default = () => <AttributeComparisonItem { ...getAttrComparisonItemData() } />;

export const Highlighted = () => <AttributeComparisonItem { ...getAttrComparisonItemData() } isHighlighted={ true }/>;
