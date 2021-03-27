import React from 'react';

import { CircularProgress, Divider, makeStyles, Typography } from '@material-ui/core';

import useTransferCenterData from '../hooks/useTransferCenterData';
import TransferActivityView from '../views/TransferActivityView';

const useStyles = makeStyles({
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        alignSelf: 'center',
        margin: '25vh'
    }
});

const Transfers = () => {
    const classes = useStyles();
    const { isLoading, data: transfers } = useTransferCenterData();
    console.log(isLoading, transfers);
    return (
        <>
            <Typography variant='h4' >
                Transfer Activity
            </Typography>
            <Divider />
            { isLoading ? <CircularProgress className={ classes.loadingCircle } />
                : <TransferActivityView transfers={ transfers } /> }
        </>
    );
};

export default Transfers;