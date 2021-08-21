import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';

import { moraleIconsMap, nationalityFlagMap } from '../utils';

import SquadHubView from '../views/SquadHubView';

import { getSquadHubPlayerData } from './utils/storyDataGenerators';

export default {
    component: SquadHubView,
    title: 'Views/SquadHubView',
    excludeStories: /.*Data$/,
    decorators: [
        (Story) => (
            <Router>
                <Story/>
            </Router>
        )
    ]
};

const nations = nationalityFlagMap.map(entity => entity.nationality);
const moraleList = moraleIconsMap.map(entity => entity.morale);

const Template = args => <SquadHubView { ...args } />;
export const Default = Template.bind({});
Default.args = {
    ...getSquadHubPlayerData(10, nations, moraleList)
};