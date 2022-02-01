import PlayerComparison from '../widgets/PlayerComparison';
import { Default as TwoPlayersView, SinglePlayer as SinglePlayerView } from './PlayerComparisonView.stories';

export default {
    component: PlayerComparison,
    title: 'Widgets/PlayerComparisonView/PlayerComparison',
    parameters: {
        docs: {
            description: {
                component: 'Widget containing tools for comparing the _attributes_ of two players. It combines'
                + ' components like `AttributeComparisonTable` and `AttributeComparisonPolarPlot` in a tabbed view'
                + ' to achieve this.'
            }
        }
    }
};

const Template = args => <PlayerComparison { ...args } />;

export const Default = Template.bind({});
Default.args = {
    basePlayerData: {
        name: TwoPlayersView.args.basePlayer.playerMetadata.name,
        attributes: TwoPlayersView.args.basePlayer.playerAttributes
    },
    comparedPlayerData: {
        name: TwoPlayersView.args.comparedPlayer.playerMetadata.name,
        attributes: TwoPlayersView.args.comparedPlayer.playerAttributes
    },
    playerRoles: [
        TwoPlayersView.args.basePlayer.playerMetadata.name,
        TwoPlayersView.args.comparedPlayer.playerMetadata.name
    ]
};

export const SinglePlayer = Template.bind({});
SinglePlayer.args = {
    basePlayerData: {
        name: SinglePlayerView.args.basePlayer.playerMetadata.name,
        attributes: SinglePlayerView.args.basePlayer.playerAttributes
    },
    comparedPlayerData: null,
    playerRoles: [
        SinglePlayerView.args.basePlayer.playerMetadata.name
    ]
};