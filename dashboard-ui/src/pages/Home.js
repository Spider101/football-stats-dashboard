import React from 'react';
import PropTypes from 'prop-types';

import CircularProgress from '@material-ui/core/CircularProgress';
import Typography from '@material-ui/core/Typography';

import { makeStyles } from '@material-ui/styles';

import { useUserAuth } from '../context/authProvider';
import useUserData from '../hooks/useUserData';

import HomePageView from '../views/HomePageView';

const useStyles = makeStyles({
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        margin: '35vh'
    }
});

const Home = () => {
    const classes = useStyles();
    const { isLoading, userData } = useUserData();

    return (
        <>
            {isLoading ? (
                <div className={classes.loadingCircleRoot}>
                    <CircularProgress className={classes.loadingCircle} />
                </div>
            ) : (
                <HomeContainer userData={userData} />
            )}
        </>
    );
};

const HomeContainer = ({ userData }) => {
    return (
        <>
            <Typography component='h2' variant='h3' align='center' paragraph style={{ width: '100%' }}>
                Welcome to your dashboard, {`${userData.firstName} ${userData.lastName}`}
            </Typography>
            <HomePageView />
        </>
    );
};

HomeContainer.propTypes = {
    userData: PropTypes.shape({
        firstName: PropTypes.string,
        lastName: PropTypes.string,
        email: PropTypes.string,
        password: PropTypes.string,
        authToken: PropTypes.string
    })
};

export default Home;
