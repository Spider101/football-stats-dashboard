import SquadHubView from '../views/SquadHubView';
import StyledLoadingCircle from '../components/StyledLoadingCircle';

import useSquadHubData from '../hooks/useSquadHubData';
import useAddNewPlayer from '../hooks/useAddNewPlayer';

import AddPlayer from '../widgets/AddPlayer';

const SquadHub = () => {
    const { addNewPlayerAction: addNewPlayerAciton } = useAddNewPlayer();
    const addPlayerWidget = <AddPlayer addPlayerAction={addNewPlayerAciton} />;
    const { isLoading, data: squadHubData } = useSquadHubData();

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    return <SquadHubView players={ squadHubData } addPlayerWidget={addPlayerWidget} />;
};

export default SquadHub;