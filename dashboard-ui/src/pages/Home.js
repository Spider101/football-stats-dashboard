import React from 'react';

import CircularProgress from '@material-ui/core/CircularProgress';
import Typography from '@material-ui/core/Typography';

import { makeStyles } from '@material-ui/styles';

import useUserData from '../hooks/useUserData';
import useClubData from '../hooks/useClubData';
import HomePageView from '../views/HomePageView';
import useAddNewClub from '../hooks/useAddNewClub';
import AddClub from '../widgets/AddClub';

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
                <>
                    <Typography component='h2' variant='h3' align='center' paragraph style={{ width: '100%' }}>
                        Welcome to your dashboard, {`${userData.firstName} ${userData.lastName}`}
                    </Typography>
                    <HomeContainer />
                </>
            )}
        </>
    );
};

const HomeContainer = () => {
    const classes = useStyles();
    const { addNewClubAction } = useAddNewClub();
    const addClubWidget = <AddClub addClubAction={addNewClubAction} />;

    const { isLoading, data: allClubsData } = useClubData();
    if (isLoading) {
        return <CircularProgress className={classes.loadingCircle} />;
    }

    return <HomePageView clubs={allClubsData} addClubWidget={addClubWidget}/>;
};

export default Home;
