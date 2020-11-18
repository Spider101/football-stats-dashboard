import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import PlayerBioCard from '../components/PlayerBioCard';
import AttributeComparisonTable from '../widgets/AttributeComparisonTable';
import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import SimpleFixedTabs, { TabPanel } from '../components/SimpleFixedTabs';

const createAttributeComparisonData = (attributeCategoryList1, attributeCategoryList2, playerNames) => {

    const maxRows = Math.max( ...attributeCategoryList1.map(attrCategory => attrCategory.attributesInCategory.length));

    let tabularAttributeData = [ ...Array(maxRows) ].map((_, i) => (
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
                },
                isHighlighted: false
            } : null;
        })
    ));

    return {
        headers: attributeCategoryList1.map(attrCategory => attrCategory.categoryName),
        rows: tabularAttributeData
    };
};

export default function PlayerComparisonView({ players }) {

    const [ tabValue, setTabValue ] = React.useState(0);

    const handleTabChange = (_, newTabValue) => {
        setTabValue(newTabValue);
    };

    const playerOnLeft = players.find(player => player.isSelected && player.orientation == 'LEFT');
    
    const playerOnRight = players.find(player => player.isSelected && player.orientation == 'RIGHT');

    const attributeComparisonData = createAttributeComparisonData(playerOnLeft.playerAttributes.attributeCategories,
        playerOnRight.playerAttributes.attributeCategories,
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
                    <SimpleFixedTabs onTabChange={ handleTabChange } tabValue={ tabValue }>
                        <TabPanel value={ tabValue } index={0}>
                            <AttributeComparisonPolarPlot { ...attributePolarPlotData } />
                        </TabPanel>
                        <TabPanel value={ tabValue }  index={1}>
                            <AttributeComparisonTable { ...attributeComparisonData } />
                        </TabPanel>
                    </SimpleFixedTabs>
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
