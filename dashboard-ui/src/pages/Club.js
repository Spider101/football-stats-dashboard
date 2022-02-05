import { useEffect } from 'react';
import { useParams } from 'react-router-dom';

import { useClubData } from '../hooks/useClubData';
import ClubPageView from '../views/ClubPageView';
import { useCurrentClub } from '../context/clubProvider';

import StyledLoadingCircle from '../components/StyledLoadingCircle';

export default function Club() {
    const { currentClubId, setCurrentClubId } = useCurrentClub();
    const { clubId } = useParams();
    const { isLoading, data: clubData } = useClubData(clubId);

    useEffect(() => {
        if(clubData && clubData.id !== currentClubId) {
            setCurrentClubId(clubData.id);
        }
    }, []);

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    return <ClubPageView club={clubData} />;
}