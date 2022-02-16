import AbilityProgressChart from '../components/AbilityProgressChart';
import { getPlayerAbilityData } from './utils/storyDataGenerators';

export default {
    component: AbilityProgressChart,
    title: 'Components/PlayerProgressionView/AbilityProgressChart',
    excludeStories: /.*Data$/,
    argTypes: {
        abilityData: {
            name: 'Ability Progression Data',
            control: { type: 'object' }
        }
    },
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying a bar chart documenting a player\'s Ability progress over the'
                + ' last _N_ months.'
            }
        }
    }
};

export const Default = (args) => <AbilityProgressChart { ...args } />;

Default.args = {
    abilityData: getPlayerAbilityData()
};