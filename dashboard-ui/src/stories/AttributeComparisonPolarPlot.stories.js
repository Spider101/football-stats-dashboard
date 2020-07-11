import React from 'react';
import AttributeComparisonPolarPlot from "../components/AttributeComparisonPolarPlot";
import faker from 'faker';

export default {
    component: AttributeComparisonPolarPlot,
    title: 'AttributeComparisonPolarPlot',
    excludeStories: /.*Data$/,
}

const polarPlotData = {
    playerAttributes: [{
        name: faker.name.lastName(1),
        data: [ ...Array(5) ].map(() => Math.round(Math.random() * 19) + 1)
    }, {
        name: faker.name.lastName(1),
        data: [ ...Array(5) ].map(() => Math.round(Math.random() * 19) + 1)
    }]
};

export const Default = () => <AttributeComparisonPolarPlot { ...polarPlotData } />;
