import React from 'react';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import faker from 'faker';

import { getAttrComparisonItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonItem,
    title: 'Components/PlayerComparisonView/AttributeComparisonTable/AttributeComparisonItem'
};

const Template = args => <AttributeComparisonItem { ...args } />;

export const Default = Template.bind({});
export const Highlighted = Template.bind({});
export const SingleAttribute = Template.bind({});

Default.args = {
    ...getAttrComparisonItemData(faker.hacker.noun())
};

Highlighted.args = {
    ...getAttrComparisonItemData(faker.hacker.noun(), 2, true)
};

SingleAttribute.args = {
    ...getAttrComparisonItemData(faker.hacker.noun(), 1)
};