import React from 'react';
import PlayerAttributesTable from '../widgets/PlayerAttributesTable';
import AttributeComparisonItem from '../components/AttributeComparisonItem';

import { getAttrComparisonItemData, getAttributeComparisonTableData } from './utils/storyDataGenerators';

export default {
    component: PlayerAttributesTable,
    title: 'Widgets/PlayerComparisonView/AttributeComparisonTable',
    excludeStories: /.*Data$/,
};

export const Default = () => (
    <PlayerAttributesTable { ...getAttributeComparisonTableData(getAttrComparisonItemData) }>
        <AttributeComparisonItem />
    </PlayerAttributesTable>
);

export const Highlighted = () => (
    <PlayerAttributesTable { ...getAttributeComparisonTableData(getAttrComparisonItemData, true) }>
        <AttributeComparisonItem />
    </PlayerAttributesTable>
);
