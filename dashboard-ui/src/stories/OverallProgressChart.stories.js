import React from 'react';
import OverallProgressChart from '../components/OverallProgressChart';
import { getAttributeLineData } from './utils/storyDataGenerators';

export default {
    component: OverallProgressChart,
    title: 'Components/PlayerProgressionView/OverallProgressnChart',
    excludeStories: /.*Data$/,
    argTypes: {
        attributeData: {
            name: 'Overall Progression Data',
            control: { type: 'object' }
        }
    }
};

export const Default = (args) => <OverallProgressChart { ...args } />;

Default.args = {
    ...getAttributeLineData(1)
};