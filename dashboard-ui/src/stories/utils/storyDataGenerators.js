import faker from 'faker';
import _ from 'lodash';

const GROWTH_INDICATOR_LIST = ['up', 'flat', 'down'];
const MAX_ATTR_VALUE = 20;

export const getAttributeItemData = (attributeName, highlightedAttributes = []) => ({
    attributeName,
    attributeValue: Math.round(Math.random() * 20),
    highlightedAttributes,
    growthIndicator: GROWTH_INDICATOR_LIST[Math.floor(Math.random() * 3)]
});

export const getAttrComparisonItemData = (attributeName, highlightedAttributes = []) => ({
    attrComparisonItem: {
        attrValues: [{
            name: faker.name.lastName(1),
            data: [ Math.round(Math.random() * 20) ]
        }, {
            name: faker.name.lastName(1),
            data: [ -1 * Math.round(Math.random() * 20) ]
        }],
        label: attributeName
    },
    highlightedAttributes
});

const getAttrComparisonTableMetaData = (numGroups) => ({
    groups: [ ...Array(numGroups) ].map(() => ({
        name: faker.lorem.word(),
        numAttr: Math.round(Math.random() * 9) + 1
    }))
});

const getAttributeNamesList = (totalNumOfAttributes) =>
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
        attributesInGroup: [ ...Array(10) ].map(() => Math.round(Math.random() * 19) + 1)
    }))
);

export const getPlayerMetadata = () => ({
    name: faker.name.findName(),
    dob: faker.date.past(),
    club: faker.company.companyName(),
    country: faker.address.country(),
    photo: `${faker.image.people()}?random=${Math.round(Math.random() * 20)}`,
    clubLogo: `${faker.image.abstract()}?random=${Math.round(Math.random() * 20)}`,
    countryLogo: `${faker.image.avatar()}?random=${Math.round(Math.random() * 20)}`,
    age:  ' (' + faker.random.number({ 'min': 16, 'max': 35 }) + ' years old)'
});


const getAttributesInCategory = (numAttributes, attributesList) => (
    [ ...Array(numAttributes)].map((_, i) => (
        { name: attributesList[i], value: Math.round(Math.random() * MAX_ATTR_VALUE) }
    ))
);

const  getPlayerAttributeCategoryData = (attributeNamesList) => ([
    {
        categoryName: 'Technical',
        attributesInCategory: getAttributesInCategory(10, attributeNamesList.slice(0, 10))
    }, {
        categoryName: 'Physical',
        attributesInCategory: getAttributesInCategory(10, attributeNamesList.slice(10, 20))
    }, {
        categoryName: 'Mental',
        attributesInCategory: getAttributesInCategory(10, attributeNamesList.slice(20, 30))
    }
]);

const getPlayerAttributeGroupData = (numAttributes) => ([
    {
        groupName: 'Defending',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Speed',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Vision',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Attacking',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }, {
        groupName: 'Aerial',
        attributesInGroup: [ ...Array(numAttributes)].map(() => Math.round(Math.random() * MAX_ATTR_VALUE))
    }
]);

export const getPlayerData = () => {
    const attributeNamesList = getAttributeNamesList(3 * 10);

    return {
        players: [{
            isSelected: true,
            orientation: 'LEFT',
            playerMetadata: getPlayerMetadata(),
            playerRoles: getPlayerRolesMap(3, attributeNamesList),
            playerAttributes: {
                attributeCategories: getPlayerAttributeCategoryData(attributeNamesList),
                attributeGroups: getPlayerAttributeGroupData(10)
            }
        }, {
            isSelected: true,
            orientation: 'RIGHT',
            playerMetadata: getPlayerMetadata(),
            playerRoles: getPlayerRolesMap(3, attributeNamesList),
            playerAttributes: {
                attributeCategories: getPlayerAttributeCategoryData(attributeNamesList),
                attributeGroups: getPlayerAttributeGroupData(10)
            }
        }]
    };
};