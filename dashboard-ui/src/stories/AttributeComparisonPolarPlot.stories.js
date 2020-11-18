import React from 'react';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import faker from 'faker';

export default {
    component: AttributeComparisonPolarPlot,
    title: 'Components | PlayerComparisonView/AttributeComparisonPolarPlot',
    excludeStories: /.*Data$/,
};

const getAttrGroupData = (numGroups) => (
    [ ...Array(numGroups) ].map(() => ({
        groupName: '',
        attributesInGroup: [ ...Array(10) ].map(() => Math.round(Math.random() * 19) + 1)
    }))
);

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
