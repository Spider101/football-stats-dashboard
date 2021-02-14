import React from 'react';

import CircularProgress from '@material-ui/core/CircularProgress';
import { makeStyles } from '@material-ui/core/styles';

import SquadHubView from '../views/SquadHubView';
import useSquadHubData from '../hooks/useSquadHubData';

const useStyles = makeStyles({
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        alignSelf: 'center',
        margin: '25vh'
    }
});

const SquadHub = () => {
    const classes = useStyles();
    const { isLoading, data: squadHubData } = useSquadHubData();

    return (
        <>
            { isLoading ? <CircularProgress className={ classes.loadingCircle }/>
                : <SquadHubView players={ squadHubData } /> }
        </>
    );
};

export default SquadHub;
