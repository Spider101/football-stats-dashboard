import React from 'react';
import PropTypes from 'prop-types';

import { getPlayerData } from '../clients/DashboardClient';
import PlayerProgressionView from '../views/PlayerProgressionView';
import { CircularProgress, makeStyles } from '@material-ui/core';

const useStyles = makeStyles({
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        alignSelf: 'center',
        margin: '25vh'
    }
});
export default function Player({ match: { params: { id } } }) {
    const classes = useStyles();

    const [pageStatus, setPageStatus] = React.useState('loading');
    const [playerViewData, setPlayerViewData] = React.useState({});

    React.useEffect(() => {
        const getPlayerViewData = async () => {
            const { metadata, roles, ability, attributes } = await getPlayerData(id);

            setPlayerViewData({
                isSelected: true,
                orientation: '',
                playerMetadata: metadata,
                playerRoles: roles,
                playerOverall: {
                    currentValue: ability.current,
                    history: ability.history
                },
                playerAttributes: attributes
            });

            setPageStatus('ready');
        };

        getPlayerViewData();
    }, [id]);

    return (
        <>
            <h2 style={{textAlign: 'center', width: '100%'}}>{ `Player #${id} Details Page` }</h2>
            { pageStatus === 'loading' && <CircularProgress className={ classes.loadingCircle }/> }
            { pageStatus === 'ready' && <PlayerProgressionView { ...playerViewData } /> }
        </>
    );
}

Player.propTypes = {
    match: PropTypes.shape({
        params: PropTypes.shape({
            id: PropTypes.number
        })
    })
};