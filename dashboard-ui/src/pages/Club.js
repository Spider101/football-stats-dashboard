import { useParams } from 'react-router-dom';

import CircularProgress from '@material-ui/core/CircularProgress';
import { makeStyles } from '@material-ui/core/styles';

import { useClubData } from '../hooks/useClubData';
import ClubPageView from '../views/ClubPageView';
import { useCurrentClub } from '../context/clubProvider';

const useStyles = makeStyles({
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        alignSelf: 'center',
        margin: '25vh'
    }
});

export default function Club() {
    const classes = useStyles();
    const { currentClubId, setCurrentClubId } = useCurrentClub();
    const { clubId } = useParams();
    const { isLoading, data: clubData } = useClubData(clubId);

    if (isLoading) {
        return <CircularProgress className={classes.loadingCircle} />;
    }

    if(clubData && clubData.id !== currentClubId) {
        setCurrentClubId(clubData.id);
    }

    return <ClubPageView club={clubData} />;
}