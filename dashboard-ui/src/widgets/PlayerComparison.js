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
import { playerAttributes } from '../constants';

export default function PlayerComparison({ basePlayerData, comparedPlayerData, playerRoles }) {
    const [tabValue, setTabValue] = useState(0);

    const handleTabChange = (_, newTabValue) => {
        setTabValue(newTabValue);
    };

    const filterByCategory = (attribute, categoryName) => attribute.category === categoryName;

    const getComparedPlayerAttributeItemData = (comparedPlayerAttributeData, basePlayerAttributeName, categoryName) =>
        comparedPlayerAttributeData
            .filter(attribute => filterByCategory(attribute, categoryName))
            .find(attribute => attribute.name === basePlayerAttributeName);

    const getAttributeComparisonDataFn = (basePlayerName, comparedPlayerName, comparedPlayerAttributes) =>
        basePlayerAttribute => {
            const comparedPlayerAttributeItemData = comparedPlayerAttributes &&
                getComparedPlayerAttributeItemData(
                    comparedPlayerAttributes,
                    basePlayerAttribute.name,
                    basePlayerAttribute.category
                );
            return {
                attrComparisonItem: {
                    attrValues: [
                        { name: basePlayerName, data: [-1 * basePlayerAttribute.value] },
                        ...(comparedPlayerAttributeItemData
                            ? [{ name: comparedPlayerName, data: [comparedPlayerAttributeItemData.value] }]
                            : [])
                    ],
                    label: basePlayerAttribute.name
                }
            };
        };

    const playerComparisonTableData = transformIntoTabularData(
        basePlayerData.attributes,
        playerAttributes.CATEGORIES,
        filterByCategory,
        getAttributeComparisonDataFn(basePlayerData.name, comparedPlayerData?.name, comparedPlayerData?.attributes)
    );
    const attributeComparisonTableData = {
        roles: playerRoles,
        ...playerComparisonTableData
    };

    const playerAttributeData = [
        { name: basePlayerData.name, attributeData: basePlayerData.attributes },
        ...(comparedPlayerData ? [{ name: comparedPlayerData.name, attributeData: comparedPlayerData.attributes }] : [])
    ];
    const attributePolarPlotData = {
        playersWithAttributes: playerAttributeData.map(playerData => ({
            name: playerData.name,
            attributes: Object.entries(_.groupBy(playerData.attributeData, attribute => attribute.group)).map(
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
    basePlayerData: PropTypes.shape({
        name: PropTypes.string,
        attributes: PlayerProgressionView.propTypes.playerAttributes
    }),
    comparedPlayerData: PropTypes.shape({
        name: PropTypes.string,
        attributes: PlayerProgressionView.propTypes.playerAttributes
    }),
    playerRoles: PlayerProgressionView.propTypes.playerRoles
};
