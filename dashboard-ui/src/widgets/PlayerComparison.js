import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';

import PlayerProgressionView from '../views/PlayerProgressionView';
import PlayerAttributesTable from './PlayerAttributesTable';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';
import { playerAttributes } from '../utils';

// TODO: refactor this to a util fn to encapsulate common code (dupe is in PlayerProgressionView)
const buildAttributeComparisonItemData = (attributes, playerName, isBasePlayer) => {
    const attributeItems = playerAttributes.CATEGORIES.map(category =>
        attributes
            .filter(attribute => attribute.category === category)
            .map(attribute => ({
                attrComparisonItem: {
                    attrValues: [{ name: playerName, data: [(isBasePlayer ? -1 : 1) * attribute.value] }],
                    label: attribute.name
                }
            }))
    );

    const maxRows = Math.max(...attributeItems.map(row => row.length));

    const rows = [...Array(maxRows)].map((_, i) =>
        [...Array(playerAttributes.CATEGORIES.length)].map((_, j) =>
            i >= attributeItems[j].length ? null : attributeItems[j][i]
        )
    );

    return {
        headers: playerAttributes.CATEGORIES,
        rows
    };
};

const mergeAttributeComparisonItems = attributeComparisonItems => {
    return attributeComparisonItems.reduce((mergedAttributeComparisonItems, attributeComparisonItemsToMerge) => {
        if (mergedAttributeComparisonItems.length === 0) {
            return attributeComparisonItemsToMerge;
        } else {
            return mergedAttributeComparisonItems.map((attributeItemRow, i) =>
                attributeItemRow.map((attributeItem, j) => {
                    const attributeItemToMerge = attributeComparisonItemsToMerge[i][j];
                    if (attributeItem === null && attributeItemToMerge === null) {
                        return null;
                    } else if (attributeItemToMerge === null) {
                        return attributeItem;
                    } else if (attributeItem === null) {
                        return attributeItemToMerge;
                    } else {
                        return {
                            ...attributeItem,
                            attrComparisonItem: {
                                ...attributeItem.attrComparisonItem,
                                attrValues: [
                                    ...attributeItem.attrComparisonItem.attrValues,
                                    ...attributeItemToMerge.attrComparisonItem.attrValues
                                ]
                            }
                        };
                    }
                })
            );
        }
    }, []);
};
export default function PlayerComparison({ players }) {
    const [tabValue, setTabValue] = React.useState(0);

    const handleTabChange = (_, newTabValue) => {
        setTabValue(newTabValue);
    };

    const attributeComparisonItemDataForPlayers = players.map((player, idx) =>
        buildAttributeComparisonItemData(player.playerAttributes, player.playerMetadata.name, idx === 0)
    );

    const attributeComparisonTableData = {
        roles: players.map(player => player.playerRoles).flat(),
        headers: attributeComparisonItemDataForPlayers[0].headers,
        rows: mergeAttributeComparisonItems(
            attributeComparisonItemDataForPlayers.map(attributeComparisonItemData => attributeComparisonItemData.rows)
        )
    };

    const attributePolarPlotData = {
        playersWithAttributes: players.map(player => ({
            name: player.playerMetadata.name,
            attributes: Object.entries(_.groupBy(player.playerAttributes, attribute => attribute.group)).map(
                ([groupName, attributes]) => ({
                    groupName,
                    attributesInGroup: attributes.map(attribute => attribute.value)
                })
            )
        }))
    };

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <CustomizableTabs
                    onTabChange={handleTabChange}
                    tabValue={tabValue}
                    isFullWidth={true}
                    ariaLabel='Player Attributes Comparison Tabs'
                    tabLabels={['Overview', 'Attributes']}
                >
                    <TabPanel value={tabValue} index={0}>
                        <AttributeComparisonPolarPlot {...attributePolarPlotData} />
                    </TabPanel>
                    <TabPanel value={tabValue} index={1}>
                        <PlayerAttributesTable {...attributeComparisonTableData}>
                            <AttributeComparisonItem />
                        </PlayerAttributesTable>
                    </TabPanel>
                </CustomizableTabs>
            </Grid>
        </Grid>
    );
}

PlayerComparison.propTypes = {
    players: PropTypes.arrayOf(PropTypes.shape(PlayerProgressionView.propTypes))
};
