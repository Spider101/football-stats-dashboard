import React from 'react';
import AttributeLineChart from '../components/AttributeLineChart';

import { getAttributeLineData } from './utils/storyDataGenerators';

export default {
    component: AttributeLineChart,
    title: 'Widgets/PlayerProgressionView/AttributeLineChart',
    excludeStories: /.*Data$/
};

export const Default = () => <AttributeLineChart { ...getAttributeLineData(10) } />;