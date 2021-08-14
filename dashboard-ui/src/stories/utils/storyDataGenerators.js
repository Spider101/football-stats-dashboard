import faker from 'faker';
import _ from 'lodash';
import { allMatchPerformanceTableHeaders, allSquadHubTableHeaders } from '../../utils';

const GROWTH_INDICATOR_LIST = ['up', 'flat', 'down'];
export const MAX_ATTR_VALUE = 20;
export const MAX_OVERALL_VALUE = 100;
const NUM_MONTHS = 6;

const getRandomNumberInRange = (upper, lower = 0) => Math.round(Math.random() * (upper - lower)) + lower;

export const getAttributeItemData = (attributeName, highlightedAttributes = []) => ({
    attributeName,
    attributeValue: getRandomNumberInRange(MAX_ATTR_VALUE),
    highlightedAttributes,
    growthIndicator: _.sample(GROWTH_INDICATOR_LIST)
});

export const getAttrComparisonItemData = (attributeName, numPlayers = 2, isHighlighted = false) => ({
    attrComparisonItem: {
        attrValues: [ ...Array(numPlayers) ].map((_, idx) => {
            const sign = idx % 2 === 0 ? -1 : 1;
            return {
                name: faker.name.lastName(1),
                data: [ sign * getRandomNumberInRange(MAX_ATTR_VALUE) ]
            };
        }),
        label: attributeName
    },
    highlightedAttributes: isHighlighted ? [ attributeName ] : []
});

const getAttrComparisonTableMetaData = (numGroups) => ({
    groups: [ ...Array(numGroups) ].map(() => ({
        name: faker.lorem.word(),
        numAttr: getRandomNumberInRange(9, 1)
    }))
});

export const getAttributeNamesList = (totalNumOfAttributes) =>
    [ ...Array(totalNumOfAttributes) ].map(() => faker.hacker.noun());

const getPlayerRolesMap = (numOfRoles, attributeList) => {
    const roles = faker.lorem.words(numOfRoles).split(' ');
    return roles.map(role => ({
        name: role,
        associatedAttributes: _.sampleSize(attributeList, 6)
    }));
};

export const getAttributeComparisonTableData = (getAttrItemData) => {
    const numGroups = 3;
    const attributeComparisonTableMetadata = getAttrComparisonTableMetaData(numGroups);
    const maxRows = Math.max(
        ...attributeComparisonTableMetadata.groups.map(group => group.numAttr)
    );
    const totalNumOfAttributes = [ ...attributeComparisonTableMetadata.groups.map(group => group.numAttr) ]
        .reduce((a, b) => a + b, 0);

    const attributeNamesList = getAttributeNamesList(totalNumOfAttributes);
    const rows = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(numGroups) ].map((_, j) => {
            const currGroup = attributeComparisonTableMetadata.groups[j];
            return i > currGroup.numAttr ? null : {
                ...getAttrItemData(attributeNamesList[i+j])
            };
        })
    ));

    const playerRolesMap = getPlayerRolesMap(4, attributeNamesList);

    return {
        roles: playerRolesMap,
        headers: attributeComparisonTableMetadata.groups.map(group => group.name),
        rows
    };
};

export const getAttrGroupData = (numGroups) => (
    [ ...Array(numGroups) ].map(() => ({
        groupName: '',
        attributesInGroup: [ ...Array(10) ].map(() => getRandomNumberInRange(MAX_ATTR_VALUE))
    }))
);

export const getPlayerMetadata = () => ({
    name: faker.name.findName(),
    dateOfBirth: faker.date.past().toJSON(),
    club: faker.company.companyName(),
    country: faker.address.country(),
    photo: `${faker.image.people()}?random=${getRandomNumberInRange(20)}`,
    clubLogo: `${faker.image.abstract()}?random=${getRandomNumberInRange(20)}`,
    countryLogo: `${faker.image.avatar()}?random=${getRandomNumberInRange(20)}`,
    age:  ' (' + faker.random.number({ 'min': 16, 'max': 35 }) + ' years old)'
});


const getAttributes = (numAttributes, categories, groups, attributeList, hasHistory) => attributeList.map(attribute => {
    const attributeValue = getRandomNumberInRange(MAX_ATTR_VALUE);
    const attributeHistory = [ ...Array(NUM_MONTHS - 1) ].map(() => getRandomNumberInRange(MAX_ATTR_VALUE));
    return {
        name: attribute,
        category: _.sample(categories),
        group: _.sample(groups),
        value: attributeValue,
        ...(hasHistory && { history: [ ...attributeHistory, attributeValue ] })
    };
});

export const getPlayerData = (attributeNamesList, hasHistory = false) => {
    const currentPlayerOverall = getRandomNumberInRange(MAX_OVERALL_VALUE);
    const playerOverallHistory = [ ...Array(NUM_MONTHS - 1) ].map(() => getRandomNumberInRange(MAX_OVERALL_VALUE));

    const categories = ['Technical', 'Physical', 'Mental'];
    const categoryGroups = ['Defending', 'Speed', 'Vision', 'Attacking', 'Aerial'];

    return {
        playerMetadata: getPlayerMetadata(),
        playerRoles: getPlayerRolesMap(3, attributeNamesList),
        playerOverall: {
            currentValue: currentPlayerOverall,
            history: [ ...playerOverallHistory, currentPlayerOverall ],
        },
        playerAttributes: getAttributes(30, categories, categoryGroups, attributeNamesList, hasHistory)
    };
};

export const getPlayerProgressionData = (numAttributes, keyName, maxValue) => {
    return [ ...Array(numAttributes) ].map(() => ({
        name: keyName || faker.hacker.noun(),
        data: [ ...Array(6) ].map(() => getRandomNumberInRange(maxValue))
    }));
};

export const getSquadHubTableData = (numRows, nationalityFlagMap, moraleIconsMap, withLink = false) => ({
    headers: allSquadHubTableHeaders,
    rows: [ ...Array(numRows) ].map((_0, idx) => {
        const country = _.sample(nationalityFlagMap);
        const moraleEntity = _.sample(moraleIconsMap);
        const chartData = {
            type: 'bar',
            series: [{
                name: 'Match Rating',
                data: [ ...Array(5) ].map(() => (Math.random() * 10).toFixed(2) + 1)
            }]
        };

        const tableData = [
            { id: 'nationality', type: 'image', data: country.flag, metadata: { sortValue: country.nationality } },
            { id: 'role', type: 'string', data: faker.hacker.noun() },
            { id: 'wages', type: 'string', data: '$' + getRandomNumberInRange(1000, 100) + 'K'},
            { id: 'form', type: 'chart', data: chartData, metadata: { sortValue: getRandomNumberInRange(10, 1) } },
            { id: 'morale', type: 'icon', data: moraleEntity.icon, metadata: { sortValue: moraleEntity.morale } },
            { id: 'current_ability', type: 'number', data: getRandomNumberInRange(MAX_OVERALL_VALUE, 1) }
        ];
        const nameColumnData = withLink
            ? { id: 'name', type: 'link', data: faker.name.findName(), metadata: { playerId: idx} }
            : { id: 'name', type: 'string', data: faker.name.findName() };

        return [
            nameColumnData,
            ...tableData
        ];
    })
});

// TODO: simplify this to match what we are sending into the actual table
export const getMatchPerformanceTableData = (numCompetitions) => ({
    headers: allMatchPerformanceTableHeaders,
    rows: [ ...Array(numCompetitions) ].map(() => {
        return [
            { id: 'competition', type: 'string', data: faker.hacker.noun() },
            { id: 'apps', type: 'number', data: getRandomNumberInRange(30) },
            { id: 'goals', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'pens', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'assts', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'pom', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'yel', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'red', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'tck', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'pas%', type: 'string', data: getRandomNumberInRange(25) + '%' },
            { id: 'drb', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'fouls', type: 'number', data: getRandomNumberInRange(25) },
            { id: 'avr', type: 'number', data: getRandomNumberInRange(25) }
        ];
    })
});

export const getSquadHubPlayerData = (numPlayers, nationsList, moraleList) => {
    return {
        players: [ ...Array(numPlayers) ].map((_0, idx) => ({
            playerId: idx,
            name: faker.name.findName(),
            nationality: _.sample(nationsList),
            role: faker.hacker.noun(),
            wages: getRandomNumberInRange(1000, 100),
            form: [ ...Array(5) ].map(() => getRandomNumberInRange(MAX_ATTR_VALUE)),
            morale: _.sample(moraleList),
            current_ability: getRandomNumberInRange(MAX_OVERALL_VALUE, 1)
        }))
    };
};

export const getMatchPerformanceBreakDown = (numCompetitions, numMatches = 0) => ({
    competitions: [ ...Array(numCompetitions) ].map(() => {
        let competitionData = {
            id: faker.hacker.noun(),
            appearances: getRandomNumberInRange(30),
            goals: getRandomNumberInRange(30),
            penalties: getRandomNumberInRange(25),
            assists: getRandomNumberInRange(25),
            playerOfTheMatch: getRandomNumberInRange(10),
            yellowCards: getRandomNumberInRange(25),
            redCards: getRandomNumberInRange(25),
            tackles: getRandomNumberInRange(25),
            passCompletionRate: getRandomNumberInRange(25),
            dribbles: getRandomNumberInRange(25),
            fouls: getRandomNumberInRange(25)
        };

        // TODO: check if we need this logic or can use matchRatingHistory object directly
        return numMatches === 0 ? {
            ...competitionData,
            averageRating: getRandomNumberInRange(10),
        } : {
            ...competitionData,
            matchRatingHistory: [ ...Array(numMatches) ].map(() => getRandomNumberInRange(10)),
        };
    })
});

/**
 * The league table will show the following data -
 * 1. team name,
 * 2. games played,
 * 3. goals for
 * 4. goals against
 * 5. points tally
 * @param {*} numTeams the number of team to be displayed in the table
 * @returns the metadata to build the table
 */
export const getLeagueTableData = (numTeams) => {
    return [ ...Array(numTeams)].map(() => ({
        team: faker.company.companyName(),
        gamesPlayed: getRandomNumberInRange(35, 30),
        goalsFor: getRandomNumberInRange(30, 10),
        goalsAgainst: getRandomNumberInRange(30, 10),
        points: getRandomNumberInRange(80, 50)
    }));
};