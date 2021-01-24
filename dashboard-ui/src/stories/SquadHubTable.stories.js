import React from 'react';
import { BrowserRouter as Router } from "react-router-dom";

import SortableTable from '../widgets/SortableTable';

import { getSquadHubTableData } from './utils/storyDataGenerators';
import { moraleIconsMap, nationalityFlagMap } from '../utils';

export default {
    component: SortableTable,
    title: 'Widgets/SquadHubView/SquadHubTable',
    excludeStories: /.*Data$/
};

const defaultSquadHubTableData = getSquadHubTableData(10, nationalityFlagMap, moraleIconsMap);
const squadHubTableDataWithRouterLink = getSquadHubTableData(10, nationalityFlagMap, moraleIconsMap, true);
const squadHubTableDataWithNoRows = {
    headers: defaultSquadHubTableData.headers,
    rows: []
};
const squadHubTableDataWithNoData = {
    headers: [],
    rows: []
};

export const Default = () => <SortableTable { ...defaultSquadHubTableData } />;

export const WithRouterLink = () => (
    <Router>
        <SortableTable { ...squadHubTableDataWithRouterLink } />;
    </Router>
);

export const EmptyRows = () => <SortableTable { ...squadHubTableDataWithNoRows } />;

export const EmptyTable = () => <SortableTable { ...squadHubTableDataWithNoData } />;