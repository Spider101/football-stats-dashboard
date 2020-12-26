import faker from 'faker';
import _ from 'lodash';
import { allSquadHubTableHeaders } from '../../utils';

const GROWTH_INDICATOR_LIST = ['up', 'flat', 'down'];
export const MAX_ATTR_VALUE = 20;
export const MAX_OVERALL_VALUE = 100;
const NUM_MONTHS = 6;

const getRandomNumberInRange = (upper, lower = 0) => Math.round(Math.random() * upper) + lower;

export const getAttributeItemData = (attributeName, highlightedAttributes = []) => ({
    attributeName,
    attributeValue: getRandomNumberInRange(MAX_ATTR_VALUE),
    highlightedAttributes,
    growthIndicator: _.sample(GROWTH_INDICATOR_LIST)
});

export const getAttrComparisonItemData = (attributeName, highlightedAttributes = []) => ({
    attrComparisonItem: {
        attrValues: [{
            name: faker.name.lastName(1),
            data: [ getRandomNumberInRange(MAX_ATTR_VALUE) ]
        }, {
            name: faker.name.lastName(1),
            data: [ -1 * getRandomNumberInRange(MAX_ATTR_VALUE) ]
        }],
        label: attributeName
    },
    highlightedAttributes
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
    let roleAttributeMap = {};
    roles.forEach(role => {
        roleAttributeMap[role] = _.sampleSize(attributeList, 6);
    });
    return roleAttributeMap;
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
    dob: faker.date.past().toJSON(),
    club: faker.company.companyName(),
    country: faker.address.country(),
    photo: `${faker.image.people()}?random=${getRandomNumberInRange(20)}`,
    clubLogo: `${faker.image.abstract()}?random=${getRandomNumberInRange(20)}`,
    countryLogo: `${faker.image.avatar()}?random=${getRandomNumberInRange(20)}`,
    age:  ' (' + faker.random.number({ 'min': 16, 'max': 35 }) + ' years old)'
});


const getAttributesInCategory = (numAttributes, attributesList, hasHistory) => (
    [ ...Array(numAttributes)].map((_, i) => {
        let attributeMap = {
            name: attributesList[i],
            value: getRandomNumberInRange(MAX_ATTR_VALUE)
        };

        if (hasHistory) {
            const attributeHistory = [ ...Array(NUM_MONTHS -1) ].map(() => getRandomNumberInRange(MAX_ATTR_VALUE));
            attributeMap = {
                ...attributeMap,
                history: [ ...attributeHistory, attributeMap.value ]
            };
        }

        return attributeMap;
    })
);

const  getPlayerAttributeCategoryData = (attributeNamesList, hasHistory) => ([
    {
        categoryName: 'Technical',
        attributesInCategory: getAttributesInCategory(10, attributeNamesList.slice(0, 10), hasHistory)
    }, {
        categoryName: 'Physical',
        attributesInCategory: getAttributesInCategory(10, attributeNamesList.slice(10, 20), hasHistory)
    }, {
        categoryName: 'Mental',
        attributesInCategory: getAttributesInCategory(10, attributeNamesList.slice(20, 30), hasHistory)
    }
]);

const getPlayerAttributeGroupData = (numAttributes) => ([
    {
        groupName: 'Defending',
        attributesInGroup: [ ...Array(numAttributes)].map(() => getRandomNumberInRange(MAX_ATTR_VALUE))
    }, {
        groupName: 'Speed',
        attributesInGroup: [ ...Array(numAttributes)].map(() => getRandomNumberInRange(MAX_ATTR_VALUE))
    }, {
        groupName: 'Vision',
        attributesInGroup: [ ...Array(numAttributes)].map(() => getRandomNumberInRange(MAX_ATTR_VALUE))
    }, {
        groupName: 'Attacking',
        attributesInGroup: [ ...Array(numAttributes)].map(() => getRandomNumberInRange(MAX_ATTR_VALUE))
    }, {
        groupName: 'Aerial',
        attributesInGroup: [ ...Array(numAttributes)].map(() => getRandomNumberInRange(MAX_ATTR_VALUE))
    }
]);

export const getPlayerData = (attributeNamesList, orientation = '',  hasHistory = false) => {
    const currentPlayerOverall = getRandomNumberInRange(MAX_OVERALL_VALUE);
    const playerOverallHistory = [ ...Array(NUM_MONTHS - 1) ].map(() => getRandomNumberInRange(MAX_OVERALL_VALUE));

    return {
        isSelected: true,
        orientation: orientation,
        playerMetadata: getPlayerMetadata(),
        playerRoles: getPlayerRolesMap(3, attributeNamesList),
        playerOverall: {
            currentValue: currentPlayerOverall,
            history: [ ...playerOverallHistory, currentPlayerOverall ],
        },
        playerAttributes: {
            attributeCategories: getPlayerAttributeCategoryData(attributeNamesList, hasHistory),
            attributeGroups: getPlayerAttributeGroupData(10)
        }
    };
};

export const getPlayerProgressionData = (numAttributes, keyName, maxValue) => {
    return [ ...Array(numAttributes) ].map(() => ({
        name: keyName || faker.hacker.noun(),
        data: [ ...Array(6) ].map(() => getRandomNumberInRange(maxValue))
    }));
};

export const getSquadHubTableData = (numRows, nationalityFlagMap, moraleIconsMap) => ({
    headers: allSquadHubTableHeaders,
    rows: [ ...Array(numRows) ].map(() => {
        const country = _.sample(nationalityFlagMap);
        const moraleEntity = _.sample(moraleIconsMap);
        const chartData = {
            type: 'bar',
            series: [{
                name: 'form',
                data: [ ...Array(5) ].map(() => (Math.random() * 10).toFixed(2) + 1)
            }]
        };

        return [
            { id: 'name', type: 'string', data: faker.name.findName() },
            { id: 'nationality', type: 'image', data: country.flag, metadata: { sortValue: country.nationality } },
            { id: 'role', type: 'string', data: faker.hacker.noun() },
            { id: 'wages', type: 'string', data: '$' + getRandomNumberInRange(1000, 100) + 'K'},
            { id: 'form', type: 'chart', data: chartData, metadata: { sortValue: getRandomNumberInRange(10, 1) } },
            { id: 'morale', type: 'icon', data: moraleEntity.icon, metadata: { sortValue: moraleEntity.morale } },
            { id: 'current ability', type: 'number', data: getRandomNumberInRange(100, 1) }
        ];
    })
});