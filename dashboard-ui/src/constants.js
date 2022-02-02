import MoodIcon from '@material-ui/icons/Mood';
import MoodBadIcon from '@material-ui/icons/MoodBad';

export const httpStatus = {
    OK: 200,
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    CREATED: 201,
    CONFLICT: 409
};

export const queryKeys = {
    SQUAD_DATA: 'squadData',
    PLAYER_DATA: 'playerData',
    COMPARED_PLAYER_DATA: 'comparedPlayerData',
    PLAYER_PERFORMANCE_DATA: 'playerPerformance',
    ALL_CLUBS: 'allClubsData',
    USER_DATA: 'userData'
};

// key for storing the auth token data in localstorage
export const AUTH_DATA_LS_KEY = 'auth-data';

export const playerAttributes = {
    CATEGORIES: ['Technical', 'Physical', 'Mental'],
    GROUPS: ['Defending', 'Speed', 'Vision', 'Attacking', 'Aerial']
};

export const DRAWER_WIDTH = 240;

export const formSubmission = {
    COMPLETE: 'COMPLETE',
    READY: 'READY',
    NOT_READY: 'NOT_READY',
    INPROGRESS: 'INPROGRESS'
};


// TODO: 01/02/22 build this from the server lookup for roles instead of hard-coding it here
export const nationalityFlagMap = [
    { nationality: 'France', flag: 'https://upload.wikimedia.org/wikipedia/commons/6/62/Flag_of_France.png' },
    { nationality: 'Germany', flag: 'https://freepngimg.com/thumb/germany_flag/1-2-germany-flag-picture.png' },
    { nationality: 'Spain', flag: 'https://freepngimg.com/thumb/spain/5-2-spain-flag-picture.png' },
    {
        nationality: 'Netherlands',
        flag:
            'https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/' +
            'Flag_of_the_Netherlands.svg/125px-Flag_of_the_Netherlands.svg.png'
    }
];

export const moraleIconsMap = [
    { morale: 'Angry', icon: <MoodBadIcon /> },
    { morale: 'Happy', icon: <MoodIcon /> }
];