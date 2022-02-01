import { useState } from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';

import PlayerProgressionView from '../views/PlayerProgressionView';
import PlayerAttributesTable from '../components/PlayerAttributesTable';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';
import { transformIntoTabularData } from '../utils';
import { playerAttributes } from '../utils';

export default function PlayerComparison({ players }) {
    const [tabValue, setTabValue] = useState(0);

    const handleTabChange = (_, newTabValue) => {
        setTabValue(newTabValue);
    };

    const filterByCategory = (attribute, categoryName) => attribute.category === categoryName;

    const getComparedPlayerAttributeItemData = (comparedPlayerAttributeData, basePlayerAttributeName, categoryName) =>
        comparedPlayerAttributeData
            .filter(attribute => filterByCategory(attribute, categoryName))
            .find(attribute => attribute.name === basePlayerAttributeName);

    const getAttributeComparisonDataFn = (playerNames, [comparedPlayerAttributes]) => basePlayerAttribute => {
        const [basePlayerName, ...otherPlayerNames] = playerNames;

        // assuming the playerMetadata and playerAttributes data is in sync
        const comparedPlayerName = otherPlayerNames.length > 0 && otherPlayerNames[0];
        const comparedPlayerAttributeItemData = otherPlayerNames.length > 0 &&
            getComparedPlayerAttributeItemData(
                comparedPlayerAttributes,
                basePlayerAttribute.name,
                basePlayerAttribute.category
            );
        return {
            attrComparisonItem: {
                attrValues: [
                    { name: basePlayerName, data: [-1 * basePlayerAttribute.value] },
                    ...(comparedPlayerAttributeItemData ? [
                        { name: comparedPlayerName, data: [comparedPlayerAttributeItemData.value] }
                    ] : [])
                ],
                label: basePlayerAttribute.name
            }
        };
    };

    const [basePlayerAttributes, ...otherPlayerAttributes] = players.map(player => player.playerAttributes);
    const playerComparisonTableData = transformIntoTabularData(
        basePlayerAttributes,
        playerAttributes.CATEGORIES,
        filterByCategory,
        getAttributeComparisonDataFn(players.map(player => player.playerMetadata.name), otherPlayerAttributes)
    );
    const attributeComparisonTableData = {
        roles: players.map(player => player.playerRoles).flat(),
        ...playerComparisonTableData
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
