import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';

import PlayerBioCard from '../components/PlayerBioCard';
import PlayerComparison from '../widgets/PlayerComparison';
import CardWithFilter from '../widgets/CardWithFilter';

export default function PlayerComparisonView({ basePlayer, comparedPlayer, filterControl }) {
    // filter out any non-existent player data
    const playerData = {
        players: [ basePlayer, comparedPlayer ].filter(player => !_.isEmpty(player))
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
                    playerData.players.map((player, _idx) => (
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
                <PlayerComparison { ...playerData } />
            </Grid>
        </>
    );
}

PlayerComparisonView.propTypes = {
    basePlayer: PropTypes.shape({
        playerMetadata: PropTypes.shape(PlayerBioCard.propTypes)
    }),
    comparedPlayer: PropTypes.shape({
        playerMetadata: PropTypes.shape(PlayerBioCard.propTypes)
    }),
    filterControl: PropTypes.node,
};