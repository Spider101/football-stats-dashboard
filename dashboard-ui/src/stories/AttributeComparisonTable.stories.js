import React from 'react';
import AttributeComparisonTable from '../widgets/AttributeComparisonTable';
import AttributeComparisonItem from '../components/AttributeComparisonItem';

import { getAttrComparisonItemData, getAttributeComparisonTableData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonTable,
    title: 'Widgets/PlayerComparisonView/AttributeComparisonTable',
    excludeStories: /.*Data$/,
};

export const Default = () => (
    <AttributeComparisonTable { ...getAttributeComparisonTableData(getAttrComparisonItemData) }>
        <AttributeComparisonItem />
    </AttributeComparisonTable>
);

export const Highlighted = () => (
    <AttributeComparisonTable { ...getAttributeComparisonTableData(getAttrComparisonItemData,true) }>
        <AttributeComparisonItem />
    </AttributeComparisonTable>
);
