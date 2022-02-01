import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';

import PlayerBioCard from '../components/PlayerBioCard';
import PlayerComparison from '../widgets/PlayerComparison';
import CardWithFilter from '../widgets/CardWithFilter';
import PlayerProgressionView from './PlayerProgressionView';

export default function PlayerComparisonView({ basePlayer, comparedPlayer, filterControl }) {
    // filter out any non-existent player data
    const players = [ basePlayer, comparedPlayer ].filter(player => !_.isEmpty(player));

    const playerRoles = players.map(player => player.playerRoles);
    const basePlayerData = { name: basePlayer.playerMetadata.name, attributes: basePlayer.playerAttributes };
    const comparedPlayerData = _.isEmpty(comparedPlayer) ? null : {
        name: comparedPlayer.playerMetadata.name,
        attributes: comparedPlayer.playerAttributes
    };

    return (
        <>
            { !_.isEmpty(comparedPlayer) &&
            <Grid container spacing={2}>
                <Grid item xs={12} style={{ display: 'flex', justifyContent: 'flex-end' }}>
                    { filterControl }
                </Grid>
            </Grid>
            }
            <Grid container spacing={2}>
                {
                    players.map((player, _idx) => (
                        <Grid item xs={6} key={ _idx }>
                            <PlayerBioCard { ...player.playerMetadata } />
                        </Grid>
                    ))
                }
                { _.isEmpty(comparedPlayer) &&
                    <Grid item xs={6}>
                        <CardWithFilter filterControl={ filterControl } />
                    </Grid>
                }
                <PlayerComparison
                    basePlayerData={basePlayerData}
                    comparedPlayerData={comparedPlayerData}
                    playerRoles={playerRoles}
                />
            </Grid>
        </>
    );
}

PlayerComparisonView.propTypes = {
    basePlayer: PlayerProgressionView.propTypes,
    comparedPlayer: PlayerProgressionView.propTypes,
    filterControl: PropTypes.node,
};