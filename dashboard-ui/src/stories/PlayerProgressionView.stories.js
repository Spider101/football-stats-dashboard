import React from 'react';
import PlayerProgressionView from '../views/PlayerProgressionView';

import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerProgressionView,
    title: 'Views/PlayerProgressionView',
    parameters: {
        docs: {
            description: {
                component: 'View containing information about the player and charts to represent their progression.'
            }
        }
    }
};

const Template = args => <PlayerProgressionView { ...args } />;

export const Default = Template.bind({});
Default.args = getPlayerData(getAttributeNamesList(3 * 10), true);