import faker from 'faker';

export const getAttributeItemData = (isHighlighted = false) => ({
    attributeName: faker.hacker.noun(),
    attributeValue: Math.round(Math.random() * 20),
    isHighlighted
});

export const getAttrComparisonItemData = () => ({
    attrComparisonItem: {
        attrValues: [{
            name: faker.name.lastName(1),
            data: [ Math.round(Math.random() * 20) ]
        }, {
            name: faker.name.lastName(1),
            data: [ -1 * Math.round(Math.random() * 20) ]
        }],
        label: faker.hacker.noun()
    },
    isHighlighted: false
});

const getAttrComparisonTableMetaData = (numGroups) => ({
    groups: [ ...Array(numGroups) ].map(() => ({
        name: faker.lorem.word(),
        numAttr: Math.round(Math.random() * 9) + 1
    }))
});

export const getAttributeComparisonTableData = (getAttrItemData, shouldHighlightAttr = false) => {
    const numGroups = 3;
    const attributeComparisonTableMetadata = getAttrComparisonTableMetaData(numGroups);
    const maxRows = Math.max(
        ...attributeComparisonTableMetadata.groups.map(group => group.numAttr)
    );
    return {
        headers: attributeComparisonTableMetadata.groups.map(group => group.name),
        rows: [ ...Array(maxRows) ].map((_, i) => (
            [ ...Array(numGroups) ].map((_, j) => {
                const currGroup = attributeComparisonTableMetadata.groups[j];
                return i > currGroup.numAttr ? null : {
                    ...getAttrItemData(),
                    isHighlighted: shouldHighlightAttr && Math.random() >= 0.5
                };
            })
        ))
    };
};

export const getAttrGroupData = (numGroups) => (
    [ ...Array(numGroups) ].map(() => ({
        groupName: '',
        attributesInGroup: [ ...Array(10) ].map(() => Math.round(Math.random() * 19) + 1)
    }))
);
