import React from 'react';
import SquadHubView from '../views/SquadHubView';
import { getSquadHubData } from '../clients/DashboardClient';

const SquadHub = () => {
    const [squadHubViewData, setSquadHubViewData] = React.useState({ players: []});

    React.useEffect(() => {
        const getSquadHubViewData = async () => {
            const squadHubData = await getSquadHubData();
            setSquadHubViewData({
                players: squadHubData
            });
        };

        getSquadHubViewData();
    }, []);

    return (
        <>
            <SquadHubView { ...squadHubViewData } />
        </>
    );
};

export default SquadHub;
