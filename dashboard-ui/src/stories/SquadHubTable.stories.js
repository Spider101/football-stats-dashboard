import React from 'react';

import SortableTable from '../widgets/SortableTable';

import { getSquadHubTableData } from './utils/storyDataGenerators';
import { moraleIconsMap, nationalityFlagMap } from '../utils';

export default {
    component: SortableTable,
    title: 'Widgets/SquadHubView/SquadHubTable',
    excludeStories: /.*Data$/
};

const defaultSquadHubTableData = getSquadHubTableData(10, nationalityFlagMap, moraleIconsMap);
const squadHubTableDataWithNoRows = {
    headers: defaultSquadHubTableData.headers,
    rows: []
};
const squadHubTableDataWithNoData = {
    headers: [],
    rows: []
};

export const Default = () => <SortableTable { ...defaultSquadHubTableData } />;

export const EmptyRows = () => <SortableTable { ...squadHubTableDataWithNoRows } />;

export const EmptyTable = () => <SortableTable { ...squadHubTableDataWithNoData } />;