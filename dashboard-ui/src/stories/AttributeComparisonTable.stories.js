import React from 'react';
import PlayerAttributesTable from '../widgets/PlayerAttributesTable';
import AttributeComparisonItem from '../components/AttributeComparisonItem';

import { getAttrComparisonItemData, getAttributeComparisonTableData } from './utils/storyDataGenerators';

export default {
    component: PlayerAttributesTable,
    title: 'Widgets/PlayerComparisonView/AttributeComparisonTable'
};

const Template = args => (
    <PlayerAttributesTable { ...args } >
        <AttributeComparisonItem />
    </PlayerAttributesTable>
);

export const Default = Template.bind({});
Default.args = getAttributeComparisonTableData(getAttrComparisonItemData);

export const SinglePlayer = Template.bind({});
SinglePlayer.args = getAttributeComparisonTableData((attributeName) => getAttrComparisonItemData(attributeName, 1));