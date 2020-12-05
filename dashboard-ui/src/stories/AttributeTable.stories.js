import React from 'react';
import AttributeItem from '../components/AttributeItem';
import AttributeComparisonTable from '../widgets/AttributeComparisonTable';

import { getAttributeComparisonTableData } from './AttributeComparisonTable.stories';
import { getAttributeItemData } from './AttributeItem.stories';

export default {
    component: AttributeComparisonTable,
    title: 'Widgets/PlayerAttributeTable',
    excludeStories: /.*Data$/,
};

export const Default = () => (
    <AttributeComparisonTable { ...getAttributeComparisonTableData(getAttributeItemData)}>
        <AttributeItem/>
    </AttributeComparisonTable>
)

export const Highlighted = () => (
    <AttributeComparisonTable { ...getAttributeComparisonTableData(getAttributeItemData, true)}>
        <AttributeItem />
    </AttributeComparisonTable>
)