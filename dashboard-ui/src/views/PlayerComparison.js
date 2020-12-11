import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import PlayerBioCard from '../components/PlayerBioCard';
import PlayerAttributesTable from '../widgets/PlayerAttributesTable';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import AttributeComparisonItem from '../components/AttributeComparisonItem';
import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';

const createAttributeComparisonData = (attributeCategoryList1, attributeCategoryList2, playerRoles1, playerRoles2,
    playerNames) => {

    const maxRows = Math.max( ...attributeCategoryList1.map(attrCategory => attrCategory.attributesInCategory.length));

    const tabularAttributeData = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(attributeCategoryList1.length) ].map((_, j) => {
            const currentAttributeCategory1 = attributeCategoryList1[j].attributesInCategory;
            const currentAttributeCategory2 = attributeCategoryList2[j].attributesInCategory;

            return i <= currentAttributeCategory1.length ? {
                attrComparisonItem: {
                    attrValues: [{
                        name: playerNames[0].split(' ')[1],
                        data: [ -1 * currentAttributeCategory1[i]['value'] ]
                    }, {
                        name: playerNames[1].split(' ')[1],
                        data: [ currentAttributeCategory2[i]['value'] ]
                    }],
                    label: currentAttributeCategory1[i]['name']
                }
            } : null;
        })
    ));

    return {
        roles: { ...playerRoles1, ...playerRoles2 },
        headers: attributeCategoryList1.map(attrCategory => attrCategory.categoryName),
        rows: tabularAttributeData
    };
};

export default function PlayerComparisonView({ players }) {

    const [ tabValue, setTabValue ] = React.useState(0);

    const handleTabChange = (_, newTabValue) => {
        setTabValue(newTabValue);
    };

    const playerOnLeft = players.find(player => player.isSelected && player.orientation === 'LEFT');
    
    const playerOnRight = players.find(player => player.isSelected && player.orientation === 'RIGHT');

    const attributeComparisonData = createAttributeComparisonData(playerOnLeft.playerAttributes.attributeCategories,
        playerOnRight.playerAttributes.attributeCategories, playerOnLeft.playerRoles, playerOnRight.playerRoles,
        [ playerOnLeft.playerMetadata.name, playerOnRight.playerMetadata.name ]);

    const attributePolarPlotData = {
        playerAttributes: [{
            name: playerOnLeft.playerMetadata.name,
            attributes: playerOnLeft.playerAttributes.attributeGroups
        }, {
            name: playerOnRight.playerMetadata.name,
            attributes: playerOnRight.playerAttributes.attributeGroups
        }]
    };

    return (
        <div>
            <Grid container spacing={2}>
                <Grid item xs={6}>
                    <PlayerBioCard { ...playerOnLeft.playerMetadata } />
                </Grid>
                <Grid item xs={6}>
                    <PlayerBioCard { ...playerOnRight.playerMetadata } />
                </Grid>
            </Grid>
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
                            <PlayerAttributesTable { ...attributeComparisonData }>
                                <AttributeComparisonItem />
                            </PlayerAttributesTable>
                        </TabPanel>
                    </CustomizableTabs>
                </Grid>
            </Grid>
        </div>
    );
}

PlayerComparisonView.propTypes = {
    players: PropTypes.arrayOf(
        PropTypes.shape({
            isSelected: PropTypes.bool,
            orientation: PropTypes.string,
            playerMetadata: PropTypes.shape(PlayerBioCard.propTypes),
            playerRoles: PropTypes.object,
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
