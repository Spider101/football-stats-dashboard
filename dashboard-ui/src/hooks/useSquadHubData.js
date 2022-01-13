import { useQuery } from 'react-query';

import { fetchSquadHubData } from '../clients/ClubClient';
import { useUserAuth } from '../context/authProvider';
import { useCurrentClub } from '../context/clubProvider';
import { queryKeys } from '../utils';

export default function() {
    const { authData } = useUserAuth();
    const { currentClubId } = useCurrentClub();

    const { isLoading, data: squadPlayersData } = useQuery([queryKeys.SQUAD_DATA, currentClubId],
        fetchSquadHubData, {
            meta: { authData }
        }
    );

    return {
        isLoading,
        data: isLoading ? [] : squadPlayersData.map(squadPlayerData => ({
            playerId: squadPlayerData.playerId,
            name: squadPlayerData.name,
            nationality: {
                countryName: squadPlayerData.country,
                flagURL: squadPlayerData.countryFlag
            },
            form: squadPlayerData.recentForm,
            current_ability: squadPlayerData.currentAbility,
            role: squadPlayerData.role
        }))
    };
}