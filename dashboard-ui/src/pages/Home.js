import Typography from '@material-ui/core/Typography';

import useUserData from '../hooks/useUserData';
import { useClubSummariesData } from '../hooks/useClubData';

import HomePageView from '../views/HomePageView';
import useAddNewClub from '../hooks/useAddNewClub';
import AddClub from '../widgets/AddClub';
import StyledLoadingCircle from '../components/StyledLoadingCircle';

const Home = () => {
    const { isLoading, userData } = useUserData();

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    return (
        <>
            <Typography component='h2' variant='h3' align='center' paragraph style={{ width: '100%' }}>
                Welcome to your dashboard, {`${userData.firstName} ${userData.lastName}`}
            </Typography>
            <HomeContainer />
        </>
    );
};

const HomeContainer = () => {
    const { addNewClubAction } = useAddNewClub();
    const addClubWidget = <AddClub addClubAction={addNewClubAction} />;

    const { isLoading, data: clubSummariesData } = useClubSummariesData();

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    return <HomePageView clubSummaries={clubSummariesData} addClubWidget={addClubWidget}/>;
};

export default Home;
