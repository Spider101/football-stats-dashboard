import React from 'react';

import SquadHubTable from '../widgets/SquadHubTable';

import { getSquadHubTableData } from './utils/storyDataGenerators';
import { moraleIconsMap, nationalityFlagMap } from '../utils';

export default {
    component: SquadHubTable,
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

export const Default = () => <SquadHubTable { ...defaultSquadHubTableData } />;

export const EmptyRows = () => <SquadHubTable { ...squadHubTableDataWithNoRows } />;

export const EmptyTable = () => <SquadHubTable { ...squadHubTableDataWithNoData } />;