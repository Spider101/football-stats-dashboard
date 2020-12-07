import faker from 'faker';
import _ from 'lodash';

const growthIndicatorList = ['up', 'flat', 'down'];

export const getAttributeItemData = (attributeName, highlightedAttributes = []) => ({
    attributeName,
    attributeValue: Math.round(Math.random() * 20),
    highlightedAttributes,
    growthIndicator: growthIndicatorList[Math.floor(Math.random() * 3)]
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

export const getPlayerRolesMap = (numOfRoles, attributeList) => {
    const roles = faker.lorem.words(numOfRoles).split(" ");
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

    const attributeNameList = [ ...Array(totalNumOfAttributes) ].map(() => faker.hacker.noun());
    const rows = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(numGroups) ].map((_, j) => {
            const currGroup = attributeComparisonTableMetadata.groups[j];
            return i > currGroup.numAttr ? null : {
                ...getAttrItemData(attributeNameList[i+j])
            };
        })
    ));

    const playerRolesMap = getPlayerRolesMap(4, attributeNameList);

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
