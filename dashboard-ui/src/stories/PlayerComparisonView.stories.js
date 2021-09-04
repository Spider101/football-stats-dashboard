import React from 'react';

import PlayerComparisonView from '../views/PlayerComparisonView';
import { filterControl } from './CardWithFilter.stories';
import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparisonView,
    title: 'Views/PlayerComparisonView',
    argTypes: {
        filterControl: { control: '' }
    },
    parameters: {
        docs: {
            description: {
                component: 'View containing tools for comparing the attributes of two players in various _categories_'
                + ' and _groups_ with the ability to highlight key attributes relevant to their _role_.'
            }
        }
    }
};

const basePlayerData = getPlayerData(getAttributeNamesList(3 * 10));
const comparedPlayerData = getPlayerData(getAttributeNamesList(3 * 10));

const Template = args => <PlayerComparisonView { ...args } />;

export const Default = Template.bind({});
Default.args = {
    basePlayer: basePlayerData,
    comparedPlayer: comparedPlayerData,
    filterControl
};

export const SinglePlayer = Template.bind({});
SinglePlayer.args = {
    basePlayer: basePlayerData,
    comparedPlayer: null,
    filterControl
};