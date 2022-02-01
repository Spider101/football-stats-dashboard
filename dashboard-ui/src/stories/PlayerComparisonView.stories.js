import PlayerComparisonView from '../views/PlayerComparisonView';
import { Selected as CardWithFilter } from './CardWithFilter.stories';
import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparisonView,
    title: 'Views/PlayerComparisonView',
    parameters: {
        docs: {
            description: {
                component: 'View containing tools for comparing the attributes of two players in various _categories_'
                + ' and _groups_ with the ability to highlight key attributes relevant to their _role_.'
            }
        }
    }
};

// since the attributes are randomized but always belonging to one of the three categories,
// there maybe a lot of `empty` data. This won't be the case in the actual application though.
const basePlayerData = getPlayerData(getAttributeNamesList(3 * 10), true);
const comparedPlayerData = getPlayerData(getAttributeNamesList(3 * 10), true);

const Template = args => <PlayerComparisonView {...args} />;

export const Default = Template.bind({});
Default.args = {
    basePlayer: basePlayerData,
    comparedPlayer: comparedPlayerData,
    filterControl: null
};

export const SinglePlayer = Template.bind({});
SinglePlayer.args = {
    basePlayer: basePlayerData,
    comparedPlayer: null,
    filterControl: <CardWithFilter {...CardWithFilter.args} />
};