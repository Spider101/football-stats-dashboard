import React from 'react';
import AttributeItem from '../components/AttributeItem';
import PlayerAttributesTable from '../widgets/PlayerAttributesTable';

import { getAttributeItemData, getAttributeComparisonTableData } from './utils/storyDataGenerators';

export default {
    component: PlayerAttributesTable,
    title: 'Widgets/PlayerProgressionView/AttributesTable'
};

const Template = args => (
    <PlayerAttributesTable { ...args }>
        <AttributeItem />
    </PlayerAttributesTable>
);

// TODO: investigate why the highlighting is not working
export const Default = Template.bind({});
Default.args = getAttributeComparisonTableData(getAttributeItemData);
