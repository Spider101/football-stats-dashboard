import React from 'react';
import LeagueTable from '../widgets/LeagueTable';
import { getLeagueTableData } from './utils/storyDataGenerators';

export default {
    component: LeagueTable,
    title: 'Widgets/HomePageView/LeagueTable',
    parameters: {
        docs: {
            description: {
                component: 'Widget for displaying the league table.'
            }
        }
    }
};

const Template = args => <LeagueTable { ...args } />;

export const Default = Template.bind({});
Default.args = {
    metadata: getLeagueTableData(10)
};