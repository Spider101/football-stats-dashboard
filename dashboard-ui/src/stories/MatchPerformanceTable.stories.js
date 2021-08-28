import React from 'react';

import SortableTable from '../widgets/SortableTable';
import { getMatchPerformanceTableData } from './utils/storyDataGenerators';

export default {
    component: SortableTable,
    title: 'Widgets/MatchPerformanceView/MatchPerformanceTable'
};

const Template = args => <SortableTable { ...args } />;

export const Default = Template.bind({});
Default.args = getMatchPerformanceTableData(5);