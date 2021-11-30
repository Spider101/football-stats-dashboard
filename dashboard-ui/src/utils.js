import React from 'react';
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

export const squadTableHeaderDisplayTypeMap = {
    name: 'string',
    nationality: 'image',
    role: 'string',
    wages: 'string',
    form: 'chart',
    morale: 'icon',
    current_ability: 'number'
};

export const matchPerformanceTableHeaderDisplayTypeMap = {
    competition: 'string',
    appearances: 'number',
    goals: 'number',
    penalties: 'number',
    assists: 'number',
    player_of_the_match: 'number',
    yellow_cards: 'number',
    red_cards: 'number',
    tackles: 'number',
    pass_completion_rate: 'string',
    dribbles: 'number',
    fouls: 'number',
    average_rating: 'number'
};

export const playerAttributes = {
    CATEGORIES: ['Technical', 'Physical', 'Mental'],
    GROUPS: ['Defending', 'Speed', 'Vision', 'Attacking', 'Aerial']
};

export const DRAWER_WIDTH = 240;

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

export const formSubmission = {
    COMPLETE: 'COMPLETE',
    INPROGRESS: 'INPROGRESS'
};

export const capitalizeLabel = label => {
    return label
        .split('_')
        .map(word => word[0].toUpperCase() + word.slice(1))
        .join(' ');
};

// TODO: update this to remove redundant code
const iconMetadataComparator = (x, y) =>
    x.metadata.sortValue < y.metadata.sortValue ? -1 : x.metadata.sortValue > y.metadata.sortValue ? 1 : 0;
const imageMetadataComparator = (x, y) =>
    x.metadata.sortValue < y.metadata.sortValue ? -1 : x.metadata.sortValue > y.metadata.sortValue ? 1 : 0;
const chartMetadataComparator = (x, y) =>
    x.metadata.sortValue < y.metadata.sortValue ? -1 : x.metadata.sortValue > y.metadata.sortValue ? 1 : 0;
const defaultComparator = (x, y) => (x.data < y.data ? -1 : x.data > y.data ? 1 : 0);

const compare = (row1, row2, sortOrder, cellLabel) => {
    const cell1 = row1.find(cell => cell.id === cellLabel);
    const cell2 = row2.find(cell => cell.id === cellLabel);

    let comparator = null;

    // we can assume that the two cells are from the same column
    // so we can just take the comparator method from one of them
    switch (cell1.type) {
    case 'icon':
        comparator = iconMetadataComparator;
        break;
    case 'image':
        comparator = imageMetadataComparator;
        break;
    case 'chart':
        comparator = chartMetadataComparator;
        break;
    default:
        comparator = defaultComparator;
    }

    return sortOrder === 'asc' ? comparator(cell1, cell2) : -comparator(cell1, cell2);
};

export const stableSortList = (array, sortOrder, columnNameToOrderBy) => {
    // no need to sort if no column has been chosen to sort on
    if (columnNameToOrderBy === '') {
        return array;
    }

    const orderedArray = array.map((el, _idx) => [el, _idx]);

    orderedArray.sort((a, b) => {
        const order = compare(a[0], b[0], sortOrder, columnNameToOrderBy);

        // if the elements are not equal, return if one was bigger than the other
        if (order !== 0) return order;

        // if they are equal, use the position/order to sort the elements
        return a[1] - b[1];
    });

    // return the array with the order information
    // eslint-disable-next-line no-unused-vars
    return orderedArray.map(([el, _]) => el);
};

export const convertCamelCaseToSnakeCase = camelCaseString =>
    camelCaseString.replace(/([a-z])([A-Z])/g, '$1_$2').toLowerCase();
