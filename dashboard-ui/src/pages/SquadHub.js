import { Redirect } from 'react-router-dom';

import SquadHubView from '../views/SquadHubView';
import StyledLoadingCircle from '../components/StyledLoadingCircle';

import { useCurrentClub } from '../context/clubProvider';
import useSquadHubData from '../hooks/useSquadHubData';
import useAddNewPlayer from '../hooks/useAddNewPlayer';

import AddPlayer from '../widgets/AddPlayer';

const SquadHub = () => {
    const { currentClubId } = useCurrentClub();
    if (!currentClubId) {
        return <Redirect to='/'/>;
    }
    const { addNewPlayerAction: addNewPlayerAction } = useAddNewPlayer();
    const addPlayerWidget = <AddPlayer addPlayerAction={addNewPlayerAction} />;
    const { isLoading, data: squadHubData } = useSquadHubData();

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    return <SquadHubView players={ squadHubData } addPlayerWidget={addPlayerWidget} />;
};

export default SquadHub;