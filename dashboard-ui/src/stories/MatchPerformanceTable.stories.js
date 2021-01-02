import React from 'react';

import SortableTable from '../widgets/SortableTable';
import { getMatchPerformanceTableData } from './utils/storyDataGenerators';

export default {
    component: SortableTable,
    title: 'Widgets/MatchPerformanceView/MatchPerformanceTable',
    excludeStories: /.*Data$/
};


export const Default = () => <SortableTable { ...getMatchPerformanceTableData(5) }/>;