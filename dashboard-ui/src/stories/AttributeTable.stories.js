import AttributeItem from '../components/AttributeItem';
import PlayerAttributesTable from '../components/PlayerAttributesTable';

import { getAttributeItemData, getAttributeComparisonTableData } from './utils/storyDataGenerators';

export default {
    component: PlayerAttributesTable,
    title: 'Widgets/PlayerProgressionView/AttributesTable',
    argTypes: {
        children: { control: '' }
    },
    parameters: {
        docs: {
            description: {
                component: 'Widget displaying a table consisting of all the attributes of a player.'
                + ' Each column in the table represents a particular category of attributes.'
                + ' It is formed by composing multiple `AttributeItem` components together.'
            }
        }
    }
};

const Template = args => (
    <PlayerAttributesTable { ...args }>
        <AttributeItem />
    </PlayerAttributesTable>
);

export const Default = Template.bind({});
Default.args = getAttributeComparisonTableData(getAttributeItemData);