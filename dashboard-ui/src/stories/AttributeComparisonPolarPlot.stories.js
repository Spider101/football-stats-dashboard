import React from 'react';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import faker from 'faker';

import { getAttrGroupData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonPolarPlot,
    title: 'Components/PlayerComparisonView/AttributeComparisonPolarPlot',
    excludeStories: /.*Data$/,
};

const polarPlotData = {
    playerAttributes: [{
        name: faker.name.lastName(1),
        attributes: getAttrGroupData(5)
    }, {
        name: faker.name.lastName(1),
        attributes: getAttrGroupData(5)
    }]
};

export const Default = () => <AttributeComparisonPolarPlot { ...polarPlotData } />;
