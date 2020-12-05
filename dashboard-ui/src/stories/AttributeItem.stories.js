import React from 'react';
import AttributeItem from '../components/AttributeItem';

import { getAttributeItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeItem,
    title: 'Components/PlayerAttributeTable/AttributeItem',
    excludeStories: /.*Data$/,
};

export const Default = () => <AttributeItem { ...getAttributeItemData() } />;

export const Highlighted = () => <AttributeItem { ...getAttributeItemData(true) } />