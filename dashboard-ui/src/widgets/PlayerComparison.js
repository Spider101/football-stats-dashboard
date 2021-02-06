import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';

import PlayerBioCard from '../components/PlayerBioCard';
import PlayerAttributesTable from './PlayerAttributesTable';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';

const buildAttributeComparisonData = (attributeCategories, playerNames) => {
    // number of attributes in each category will be the same for both players,
    // so we run the max function on the first player's data
    const maxRows = Math.max( ...attributeCategories[0].map(category => category.attributesInCategory.length));

    const tabularAttributeData = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(attributeCategories[0].length) ].map((_, j) => {
            const data = [];
            const attributeList1 = attributeCategories[0][j].attributesInCategory;
            const attributeList2 = attributeCategories.length > 1 ? attributeCategories[1][j].attributesInCategory : [];

            // skip adding an entry if there is no attribute data for the ith row
            if (i > attributeList1.length) {
                return null;
            }

            // TODO: find a better way to do this
            data.push({
                name: playerNames[0],
                data: [ -1 * attributeList1[i]['value'] ]
            });

            if (attributeList2.length !== 0) {
                data.push({
                    name: playerNames[1],
                    data: [ attributeList1[i]['value'] ]
                });
            }

            return {
                attrComparisonItem: {
                    attrValues: data,
                    label: attributeList1[i]['name']
                }
            };
        })
    ));

    return {
        headers: attributeCategories[0].map(category => category.categoryName),
        rows: tabularAttributeData
    };
};

export default function PlayerComparison({ players }) {

    const [ tabValue, setTabValue ] = React.useState(0);

    const handleTabChange = (_, newTabValue) => {
        setTabValue(newTabValue);
    };

    const attributeCategories = players.map(player => player.playerAttributes.attributeCategories);
    const attributeComparisonTableData = {
        roles: _.fromPairs(...players.map(player => Object.entries(player.playerRoles))),
        ...buildAttributeComparisonData(attributeCategories, players.map(player => player.playerMetadata.name))
    };

    const attributePolarPlotData = {
        playerAttributes: players.map(player => ({
            name: player.playerMetadata.name,
            attributes: player.playerAttributes.attributeGroups
        }))
    };

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <CustomizableTabs
                    onTabChange={ handleTabChange }
                    tabValue={ tabValue }
                    isFullWidth={ true }
                    ariaLabel="Player Attributes Comparison Tabs"
                    tabLabels={['Overview', 'Attributes']}
                >
                    <TabPanel value={ tabValue } index={0}>
                        <AttributeComparisonPolarPlot { ...attributePolarPlotData } />
                    </TabPanel>
                    <TabPanel value={ tabValue }  index={1}>
                        <PlayerAttributesTable { ...attributeComparisonTableData }>
                            <AttributeComparisonItem />
                        </PlayerAttributesTable>
                    </TabPanel>
                </CustomizableTabs>
            </Grid>
        </Grid>
    );
}

PlayerComparison.propTypes = {
    players: PropTypes.arrayOf(
        PropTypes.shape({
            playerMetadata: PropTypes.shape(PlayerBioCard.propTypes),
            playerRoles: PropTypes.object,
            playerOverall: PropTypes.shape({
                currentValue: PropTypes.number,
                history: PropTypes.arrayOf(PropTypes.number)
            }),
            playerAttributes: PropTypes.shape({
                attributeCategories: PropTypes.arrayOf(
                    PropTypes.shape({
                        categoryName: PropTypes.string,
                        attributesInCategory: PropTypes.arrayOf(
                            PropTypes.shape({
                                name: PropTypes.string,
                                value: PropTypes.number
                            })
                        )
                    })
                ),
                attributeGroups: PropTypes.arrayOf(PropTypes.shape({
                    groupName: PropTypes.string,
                    attributesInGroup: PropTypes.array
                }))
            })
        })
    )
};
