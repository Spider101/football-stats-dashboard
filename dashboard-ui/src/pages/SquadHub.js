import React from 'react';
import { useQuery } from 'react-query';

import CircularProgress from '@material-ui/core/CircularProgress';
import { makeStyles } from '@material-ui/core/styles';

import SquadHubView from '../views/SquadHubView';
import { fetchSquadHubData } from '../clients/DashboardClient';

const useStyles = makeStyles((theme) => ({
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        alignSelf: 'center',
        margin: '25vh'
    }
}));

const SquadHub = () => {
    const classes = useStyles();
    const { status, data : squadHubData } = useQuery('squadData', fetchSquadHubData);

    return (
        <>
            { status === 'success' && <SquadHubView players={ squadHubData } /> }
            { status === 'loading' && <CircularProgress className={ classes.loadingCircle }/> }
        </>
    );
};

export default SquadHub;
