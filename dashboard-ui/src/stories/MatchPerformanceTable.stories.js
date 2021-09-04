import React from 'react';

import SortableTable from '../widgets/SortableTable';
import { getMatchPerformanceTableData } from './utils/storyDataGenerators';

export default {
    component: SortableTable,
    title: 'Widgets/MatchPerformanceView/MatchPerformanceTable',
    parameters: {
        docs: {
            description: {
                component: 'Widget for displaying the match performance details of a player in different competitions.'
            }
        }
    }
};

const Template = args => <SortableTable { ...args } />;

export const Default = Template.bind({});
Default.args = getMatchPerformanceTableData(5);