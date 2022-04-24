import MoodIcon from '@material-ui/icons/Mood';
import MoodBadIcon from '@material-ui/icons/MoodBad';

export const caseFormat = {
    CAMEL_CASE: 'camelcase',
    SNAKE_CASE: 'snakecase'
};

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
    USER_DATA: 'userData',
    ALL_BOARD_OBJECTIVES: 'allBoardObjectives'
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


export const PLAYER_NATIONS = ['France', 'Germany', 'Spain', 'Netherlands', 'England'];

export const MORALE_ICON_MAPPING = [
    { morale: 'Angry', icon: <MoodBadIcon /> },
    { morale: 'Happy', icon: <MoodIcon /> }
];

export const PLAYER_ATTRIBUTE_METADATA = [
    { name: 'freekickAccuracy', category: 'technical', defaultValue: '0' },
    { name: 'penalties', category: 'technical', defaultValue: '0' },
    { name: 'headingAccuracy', category: 'technical', defaultValue: '0' },
    { name: 'crossing', category: 'technical', defaultValue: '0' },
    { name: 'shortPassing', category: 'technical', defaultValue: '0' },
    { name: 'longPassing', category: 'technical', defaultValue: '0' },
    { name: 'longShots', category: 'technical', defaultValue: '0' },
    { name: 'finishing', category: 'technical', defaultValue: '0' },
    { name: 'volleys', category: 'technical', defaultValue: '0' },
    { name: 'ballControl', category: 'technical', defaultValue: '0' },
    { name: 'standingTackle', category: 'technical', defaultValue: '0' },
    { name: 'slidingTackle', category: 'technical', defaultValue: '0' },
    { name: 'dribbling', category: 'technical', defaultValue: '0' },
    { name: 'curve', category: 'technical', defaultValue: '0' },
    { name: 'stamina', category: 'physical', defaultValue: '0' },
    { name: 'jumping', category: 'physical', defaultValue: '0' },
    { name: 'strength', category: 'physical', defaultValue: '0' },
    { name: 'sprintSpeed', category: 'physical', defaultValue: '0' },
    { name: 'acceleration', category: 'physical', defaultValue: '0' },
    { name: 'agility', category: 'physical', defaultValue: '0' },
    { name: 'balance', category: 'physical', defaultValue: '0' },
    { name: 'aggression', category: 'mental', defaultValue: '0' },
    { name: 'vision', category: 'mental', defaultValue: '0' },
    { name: 'composure', category: 'mental', defaultValue: '0' },
    { name: 'defensiveAwareness', category: 'mental', defaultValue: '0' },
    { name: 'attackingPosition', category: 'mental', defaultValue: '0' }
];

export const PLAYER_ROLE_NAMES = [
    'defensiveCentralMidfielder',
    'falseNine',
    'sweeperKeeper',
    'regista',
    'insideForward'
];

export const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];


// TODO: 08/02/22 do some profiling to settle on a better number for this
// setting it to 10 for now.
export const CHART_ANIMATION_THRESHOLD = 10;