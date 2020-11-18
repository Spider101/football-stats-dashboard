import React from 'react';
import AttributeComparisonTable from '../widgets/AttributeComparisonTable';
import { getAttrComparisonItemData } from './AttributeComparisonItem.stories';
import faker from 'faker';

export default {
    component: AttributeComparisonTable,
    title: 'Widgets/PlayerComparisonView/AttributeComparisonTable',
    excludeStories: /.*Data$/,
};

const getAttrComparisonTableMetaData = (numGroups) => ({
    groups: [ ...Array(numGroups) ].map(() => ({
        name: faker.lorem.word(),
        numAttr: Math.round(Math.random() * 9) + 1
    }))
});

const getAttributeComparisonTableData = (shouldHighlightAttr = false) => {
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
                    ...getAttrComparisonItemData(),
                    isHighlighted: shouldHighlightAttr && Math.random() >= 0.5
                };
            })
        ))
    };
};

export const Default = () => <AttributeComparisonTable { ...getAttributeComparisonTableData() } />;

export const Highlighted = () => <AttributeComparisonTable { ...getAttributeComparisonTableData(true) } />;
