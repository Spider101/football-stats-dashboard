import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';

import SortableTable from '../widgets/SortableTable';

import { getSquadHubTableData } from './utils/storyDataGenerators';
import { moraleIconsMap, nationalityFlagMap } from '../utils';

export default {
    component: SortableTable,
    title: 'Widgets/SquadHubView/SquadHubTable',
    parameters: {
        docs: {
            description: {
                component: 'Widget to display all the players in the squad with some key information and attributes.'
            }
        }
    }
};

const Template = args => <SortableTable { ...args } />;

export const Default = Template.bind({});
Default.args = getSquadHubTableData(10, nationalityFlagMap, moraleIconsMap);


export const WithRouterLink = Template.bind({});
WithRouterLink.decorators = [(Story) => <Router><Story/></Router>];
WithRouterLink.args = getSquadHubTableData(10, nationalityFlagMap, moraleIconsMap, true);

export const EmptyRows = Template.bind({});
EmptyRows.args = {
    headers: Default.args.headers,
    rows: []
};

export const EmptyTable = Template.bind({});
EmptyTable.args = {
    headers: [],
    rows: []
};