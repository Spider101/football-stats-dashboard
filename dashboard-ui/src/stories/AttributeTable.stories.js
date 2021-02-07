import React from 'react';
import AttributeItem from '../components/AttributeItem';
import PlayerAttributesTable from '../widgets/PlayerAttributesTable';

import { getAttributeItemData, getAttributeComparisonTableData } from './utils/storyDataGenerators';

export default {
    component: PlayerAttributesTable,
    title: 'Widgets/PlayerProgressionView/AttributesTable',
    excludeStories: /.*Data$/,
};

export const Default = () => (
    <PlayerAttributesTable { ...getAttributeComparisonTableData(getAttributeItemData)}>
        <AttributeItem/>
    </PlayerAttributesTable>
);

export const Highlighted = () => (
    <PlayerAttributesTable { ...getAttributeComparisonTableData(getAttributeItemData, true)}>
        <AttributeItem />
    </PlayerAttributesTable>
);