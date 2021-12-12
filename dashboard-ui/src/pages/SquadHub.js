import SquadHubView from '../views/SquadHubView';
import StyledLoadingCircle from '../components/StyledLoadingCircle';

import useSquadHubData from '../hooks/useSquadHubData';

const SquadHub = () => {
    const { isLoading, data: squadHubData } = useSquadHubData();

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    return <SquadHubView players={ squadHubData } />;
};

export default SquadHub;