import { faker } from '@faker-js/faker';
import _ from 'lodash';

import { playerAttributes } from '../../constants';

export const MAX_ATTR_VALUE = 20;
export const MAX_OVERALL_VALUE = 100;

const GROWTH_INDICATOR_LIST = ['up', 'flat', 'down'];
const NUM_MONTHS = 6;

export const countryFlagMetadata = [
    {
        id: faker.datatype.uuid(),
        countryName: 'France',
        countryCode: 'fr',
        countryFlagUrl: 'https://flagcdn.com/w40/fr.png'
    },
    {
        id: faker.datatype.uuid(),
        countryName: 'Germany',
        countryCode: 'fr',
        countryFlagUrl: 'https://flagcdn.com/w40/de.png'
    },
    {
        id: faker.datatype.uuid(),
        countryName: 'Spain',
        countryCode: 'fr',
        countryFlagUrl: 'https://flagcdn.com/w40/es.png'
    },
    {
        id: faker.datatype.uuid(),
        countryName: 'Italy',
        countryCode: 'fr',
        countryFlagUrl: 'https://flagcdn.com/w40/it.png'
    },
    {
        id: faker.datatype.uuid(),
        countryName: 'England',
        countryCode: 'gb-eng',
        countryFlagUrl: 'https://flagcdn.com/w40/gb-eng.png'
    },
    {
        id: faker.datatype.uuid(),
        countryName: 'Netherlands',
        countryCode: 'nl',
        countryFlagUrl: 'https://flagcdn.com/w40/nl.png'
    },
    {
        id: faker.datatype.uuid(),
        countryName: 'Norway',
        countryCode: 'nr',
        countryFlagUrl: 'https://flagcdn.com/w40/no.png'
    }
];

const allSquadHubTableHeaders = [
    { id: 'name', type: 'string' },
    { id: 'nationality', type: 'image' },
    { id: 'role', type: 'string' },
    { id: 'wages', type: 'string' },
    { id: 'form', type: 'chart' },
    { id: 'morale', type: 'icon' },
    { id: 'current_ability', type: 'number' }
];

const allMatchPerformanceTableHeaders = [
    { id: 'competition', type: 'string' },
    { id: 'appearances', type: 'number' },
    { id: 'goals', type: 'number' },
    { id: 'penalties', type: 'number' },
    { id: 'assists', type: 'number' },
    { id: 'player_of_the_match', type: 'number' },
    { id: 'yellow_cards', type: 'number' },
    { id: 'red_cards', type: 'number' },
    { id: 'tackles', type: 'number' },
    { id: 'pass_completion_rate', type: 'string' },
    { id: 'dribbles', type: 'number' },
    { id: 'fouls', type: 'number' },
    { id: 'average_rating', type: 'number' }
];

export const getAttributeItemData = (attributeName, highlightedAttributes = []) => ({
    attributeName,
    attributeValue: faker.datatype.number(MAX_ATTR_VALUE),
    highlightedAttributes,
    growthIndicator: _.sample(GROWTH_INDICATOR_LIST)
});

export const getAttrComparisonItemData = (attributeName, numPlayers = 2, isHighlighted = false) => ({
    attrComparisonItem: {
        attrValues: [ ...Array(numPlayers) ].map((_, idx) => {
            const sign = idx % 2 === 0 ? -1 : 1;
            return {
                name: faker.name.lastName(),
                data: [ sign * faker.datatype.number(MAX_ATTR_VALUE) ]
            };
        }),
        label: attributeName
    },
    highlightedAttributes: isHighlighted ? [ attributeName ] : []
});

const getAttrComparisonTableMetaData = (numGroups) => ({
    groups: [ ...Array(numGroups) ].map(() => ({
        name: faker.hacker.ingverb(),
        numAttr: faker.datatype.number({ max: 9, min: 1 })
    }))
});

export const getAttributeNamesList = (totalNumOfAttributes) =>
    [ ...Array(totalNumOfAttributes) ].map(() => faker.hacker.noun());

const getPlayerRolesMap = (numOfRoles, attributeList) => {
    const roles = [ ...Array(numOfRoles) ].map(() => faker.name.jobType());
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
        groupName: _.sample(playerAttributes.GROUPS),
        attributesInGroup: [ ...Array(10) ].map(() => faker.datatype.number(MAX_ATTR_VALUE))
    }))
);

export const getPlayerMetadata = () => ({
    name: faker.name.findName(),
    club: faker.company.companyName(),
    country: faker.address.country(),
    photo: `${faker.image.people()}?random=${faker.datatype.number(20)}`,
    clubLogo: faker.system.fileName(),
    countryLogo: `${faker.image.avatar()}?random=${faker.datatype.number(20)}`,
    age:  faker.datatype.number({ min: 16, max: 35 })
});


export const getAttributes = (attributeList, hasHistory) =>
    attributeList.map(attribute => {
        const attributeValue = faker.datatype.number(MAX_ATTR_VALUE);
        const attributeHistory = [ ...Array(NUM_MONTHS - 1) ].map(() => faker.datatype.number(MAX_ATTR_VALUE));
        return {
            name: attribute,
            category: _.sample(playerAttributes.CATEGORIES),
            group: _.sample(playerAttributes.GROUPS),
            value: attributeValue,
            ...(hasHistory && { history: [ ...attributeHistory, attributeValue ] })
        };
    });

export const getPlayerData = (attributeNamesList, hasHistory = false) => {
    const currentPlayerAbility = faker.datatype.number(MAX_OVERALL_VALUE);
    const playerAbilityHistory = [ ...Array(NUM_MONTHS - 1) ].map(() => faker.datatype.number(MAX_OVERALL_VALUE));

    return {
        playerMetadata: getPlayerMetadata(),
        playerRoles: getPlayerRolesMap(3, attributeNamesList),
        playerAbility: {
            currentValue: currentPlayerAbility,
            history: [...playerAbilityHistory, currentPlayerAbility]
        },
        playerAttributes: getAttributes(
            attributeNamesList,
            hasHistory
        )
    };
};

export const getPlayerProgressionData = (numAttributes, maxValue, numMonths = NUM_MONTHS) => {
    return [ ...Array(numAttributes) ].map(() => ({
        name: faker.hacker.noun(),
        history: [...Array(numMonths)].map(() => faker.datatype.number(maxValue))
    }));
};

export const getPlayerAbilityData = (numMonths = NUM_MONTHS) => ({
    name: 'Player Ability',
    history: [...Array(numMonths)].map(() => faker.datatype.number(MAX_OVERALL_VALUE))
});

export const getCardWithChartData = (dataKeys, numDataPoints, maxValue) =>
    [...Array(numDataPoints)].map(() =>
        Object.fromEntries(dataKeys.map(dataKey => [dataKey, faker.datatype.number(maxValue)]))
    );

export const getSquadHubTableData = (numRows, moraleIconsMapping, withLink = false) => ({
    headers: allSquadHubTableHeaders,
    rows: [ ...Array(numRows) ].map(() => {
        const nationalityMetadata = _.sample(
            countryFlagMetadata.map(flagMetadata => ({
                countryName: flagMetadata.name,
                flagURL: flagMetadata.countryFlagUrl
            }))
        );
        const moraleEntity = _.sample(moraleIconsMapping);
        const chartData = [...Array(5)].map(() => ({ form: faker.datatype.number({ min: 5, max: 10})}));

        const tableData = [
            {
                id: 'nationality',
                type: 'image',
                data: nationalityMetadata.flagURL,
                metadata: { sortValue: nationalityMetadata.countryName }
            },
            { id: 'role', type: 'string', data: faker.name.jobType() },
            { id: 'wages', type: 'string', data: faker.finance.amount(100000, 1000000, 0, '$', true)},
            { id: 'form', type: 'chart', data: chartData, metadata: { sortValue: faker.datatype.number(10, 1) } },
            { id: 'morale', type: 'icon', data: moraleEntity.icon, metadata: { sortValue: moraleEntity.morale } },
            { id: 'current_ability', type: 'number', data: faker.datatype.number({ max: MAX_OVERALL_VALUE, min: 1 }) }
        ];
        const nameColumnData = withLink
            ? { id: 'name', type: 'link', data: faker.name.findName(), metadata: { playerId: faker.datatype.uuid() } }
            : { id: 'name', type: 'string', data: faker.name.findName() };

        return [
            nameColumnData,
            ...tableData
        ];
    })
});

export const getMatchPerformanceTableData = (numCompetitions) => ({
    headers: allMatchPerformanceTableHeaders,
    rows: [ ...Array(numCompetitions) ].map(() => {
        return [
            { id: 'competition', type: 'string', data: faker.commerce.productName() },
            { id: 'apps', type: 'number', data: faker.datatype.number(30) },
            { id: 'goals', type: 'number', data: faker.datatype.number(25) },
            { id: 'pens', type: 'number', data: faker.datatype.number(25) },
            { id: 'assts', type: 'number', data: faker.datatype.number(25) },
            { id: 'pom', type: 'number', data: faker.datatype.number(25) },
            { id: 'yel', type: 'number', data: faker.datatype.number(25) },
            { id: 'red', type: 'number', data: faker.datatype.number(25) },
            { id: 'tck', type: 'number', data: faker.datatype.number(25) },
            { id: 'pas%', type: 'string', data: faker.datatype.number(25) + '%' },
            { id: 'drb', type: 'number', data: faker.datatype.number(25) },
            { id: 'fouls', type: 'number', data: faker.datatype.number(25) },
            { id: 'avr', type: 'number', data: faker.datatype.number(25) }
        ];
    })
});

export const getSquadHubPlayerData = (numPlayers, moraleList) => {
    return {
        players: [...Array(numPlayers)].map(() => ({
            playerId: faker.datatype.uuid(),
            name: faker.name.findName(),
            nationality: _.sample(countryFlagMetadata.map(flagMetadata => ({
                countryName: flagMetadata.name,
                flagURL: flagMetadata.countryFlagUrl
            }))),
            role: faker.name.jobType(),
            wages: faker.datatype.number({ max: 1000, min: 100 }),
            form: [...Array(5)].map(() => faker.datatype.number({ min: 5, max: 10 })),
            morale: _.sample(moraleList),
            current_ability: faker.datatype.number({ max: MAX_OVERALL_VALUE, min: 1 })
        }))
    };
};

export const getMatchPerformanceBreakDown = (numCompetitions, numMatches = 0) => ({
    competitions: [ ...Array(numCompetitions) ].map(() => {
        const competitionData = {
            id: faker.commerce.productName(), // this is the competition name being used as and ID
            appearances: faker.datatype.number(30),
            goals: faker.datatype.number(30),
            penalties: faker.datatype.number(25),
            assists: faker.datatype.number(25),
            playerOfTheMatch: faker.datatype.number(10),
            yellowCards: faker.datatype.number(25),
            redCards: faker.datatype.number(25),
            tackles: faker.datatype.number(25),
            passCompletionRate: faker.datatype.number(25),
            dribbles: faker.datatype.number(25),
            fouls: faker.datatype.number(25)
        };

        return numMatches === 0 ? {
            ...competitionData,
            averageRating: faker.datatype.number(10),
        } : {
            ...competitionData,
            matchRatingHistory: [ ...Array(numMatches) ].map(() => faker.datatype.number(10)),
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
        gamesPlayed: faker.datatype.number({ max: 35, min: 30 }),
        goalsFor: faker.datatype.number({ max: 30, min: 10 }),
        goalsAgainst: faker.datatype.number({ max: 30, min: 10 }),
        points: faker.datatype.number({ max: 80, min: 50 })
    }));
};

export const getClubData = () => ({
    id: faker.datatype.uuid(),
    name: faker.company.companyName(),
    transferBudget: faker.datatype.number({ max: 500000, min: 10000000 }),
    wageBudget: faker.datatype.number({ max: 500000, min: 10000000 }),
    income: faker.datatype.number({ max: 500000, min: 10000000 }),
    expenditure: faker.datatype.number({ max: 500000, min: 10000000 }),
    createdDate: faker.date.recent().toLocaleDateString()
});

export const getClubSummaryData = numClubs => {
    return [ ...Array(numClubs) ].map(() => ({
        clubId: faker.datatype.uuid(),
        name: faker.company.companyName(),
        createdDate: faker.date.recent().toLocaleDateString()
    }));
};

export const getBoardObjectives = (numObjectives = 5) =>
    [...Array(numObjectives)].map(() => ({
        id: faker.datatype.uuid(),
        title: faker.lorem.sentence(),
        description: faker.lorem.paragraph(),
        isCompleted: false
    }));