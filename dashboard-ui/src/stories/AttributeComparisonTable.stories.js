import PlayerAttributesTable from '../components/PlayerAttributesTable';
import AttributeComparisonItem from '../components/AttributeComparisonItem';

import { getAttrComparisonItemData, getAttributeComparisonTableData } from './utils/storyDataGenerators';

export default {
    component: PlayerAttributesTable,
    title: 'Components/PlayerComparisonView/AttributeComparisonTable',
    argTypes: {
        children: { table: { disable: true } }
    },
    parameters: {
        docs: {
            description: {
                component: 'UI component for displaying a table consisting of two player\'s attributes compared against'
                + ' each other. Each column in the table represents a particular category of attributes. The table is'
                + ' also capable of displaying a single player\'s attributes.'
            }
        }
    }
};

const Template = args => (
    <PlayerAttributesTable {...args}>
        <AttributeComparisonItem />
    </PlayerAttributesTable>
);

export const Default = Template.bind({});
Default.args = getAttributeComparisonTableData(getAttrComparisonItemData);

export const SinglePlayer = Template.bind({});
SinglePlayer.args = getAttributeComparisonTableData(attributeName => getAttrComparisonItemData(attributeName, 1));