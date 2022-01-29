import AttributeComparisonItem from '../components/AttributeComparisonItem';
import { faker } from '@faker-js/faker';

import { getAttrComparisonItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonItem,
    title: 'Components/PlayerComparisonView/AttributeComparisonTable/AttributeComparisonItem',
    parameters: {
        docs: {
            description: {
                component: 'UI Component for the smallest functional block used in composing the'
                + ' `AttributeComparisonTable` component. It consists of a horizontal bar chart comparing'
                + ' two players for a given _attribute_.'
            }
        }
    }
};

const attributeName = faker.hacker.noun();
const Template = args => <AttributeComparisonItem { ...args } />;

export const Default = Template.bind({});
Default.args = getAttrComparisonItemData(attributeName);

export const Highlighted = Template.bind({});
Highlighted.args = getAttrComparisonItemData(attributeName, 2, true);

export const SingleAttribute = Template.bind({});
SingleAttribute.args = getAttrComparisonItemData(attributeName, 1);