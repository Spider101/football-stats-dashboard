import React from 'react';

import SquadHubTable from '../widgets/SquadHubTable';

import { getSquadHubTableData } from './utils/storyDataGenerators';
import { moraleIconsMap, nationalityFlagMap } from '../utils';

export default {
    component: SquadHubTable,
    title: 'Widgets/SquadHubView/SquadHubTable',
    excludeStories: /.*Data$/
};

export const Default = () => <SquadHubTable { ...getSquadHubTableData(10, nationalityFlagMap, moraleIconsMap) } />;