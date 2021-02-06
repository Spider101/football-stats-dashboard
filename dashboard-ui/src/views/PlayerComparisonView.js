import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import PlayerBioCard from '../components/PlayerBioCard';
import PlayerComparison from '../widgets/PlayerComparison';

export default function PlayerComparisonView({ basePlayer, comparedPlayer, cardWithFilter }) {
    // filter out any non-existent player data
    const playerData = {
        players: [ basePlayer, comparedPlayer ].filter(player => player !== null)
    };

    return (
        <>
            <Grid container spacing={2}>
                {
                    playerData.players.map((player, _idx) => (
                        <Grid item xs={6} key={ _idx }>
                            <PlayerBioCard { ...player.playerMetadata } />
                        </Grid>
                    ))
                }
                { cardWithFilter !== null &&
                    <Grid item xs={6}>
                        { cardWithFilter }
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
    cardWithFilter: PropTypes.object
};