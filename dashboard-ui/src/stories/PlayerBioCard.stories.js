import PlayerBioCard from '../components/PlayerBioCard';
import { getPlayerMetadata } from './utils/storyDataGenerators';

export default {
    component: PlayerBioCard,
    title: 'Components/PlayerComparisonView/PlayerBioCard',
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying content regarding a single player. It includes an image of the'
                + ' player, their name, their date of birth and age, their club and country with the associated logos.'
            }
        }
    }
};

const Template = args => <PlayerBioCard { ...args } />;

export const Default = Template.bind({});
Default.args = getPlayerMetadata();