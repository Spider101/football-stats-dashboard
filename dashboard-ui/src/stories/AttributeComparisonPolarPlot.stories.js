import React from 'react';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import faker from 'faker';

import { getAttrGroupData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonPolarPlot,
    title: 'Components/PlayerComparisonView/AttributeComparisonPolarPlot'
};

const Template = args => <AttributeComparisonPolarPlot { ...args } />;

export const Default = Template.bind({});
Default.args = {
    playersWithAttributes: [{
        name: faker.name.lastName(1),
        attributes: getAttrGroupData(5)
    }, {
        name: faker.name.lastName(1),
        attributes: getAttrGroupData(5)
    }]
};

export const SinglePlayer = Template.bind({});
SinglePlayer.args = {
    playersWithAttributes: [{
        name: faker.name.lastName(1),
        attributes: getAttrGroupData(5)
    }]
};