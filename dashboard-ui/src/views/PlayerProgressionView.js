import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import PlayerAttributesTable from '../widgets/PlayerAttributesTable';
import AttributeItem from '../components/AttributeItem';
import AttributeProgressChart from '../components/AttributeProgressChart';
import PlayerBioCard from '../components/PlayerBioCard';

const getGrowthIndicator = ({ value, history }) => {
    const penultimateValue = history[history.length - 2];
    if (value > penultimateValue) {
        return 'up';
    } else if (value < penultimateValue) {
        return 'down';
    } else {
        return 'flat';
    }
};

const buildAttributeTableData = (attributeCategoryData) => {
    const maxRows = Math.max(
        ...attributeCategoryData.map(category => category.attributesInCategory.length)
    );

    const numCategories = attributeCategoryData.length;
    const rows = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(numCategories) ].map((_, j) => {
            const currentCategory = attributeCategoryData[j];
            return i > currentCategory.attributesInCategory.length ? null : {
                attributeName: currentCategory.attributesInCategory[i].name,
                attributeValue: currentCategory.attributesInCategory[i].value,
                highlightedAttributes: [],
                growthIndicator: getGrowthIndicator(currentCategory.attributesInCategory[i])

            };
        })
    ));

    return {
        headers: attributeCategoryData.map(category => category.categoryName),
        rows
    };
};

const buildAttributeProgressChartData = (attributeCategoryData) => {
    const attributeProgressChartData = attributeCategoryData.map(category => category.attributesInCategory)
        .flat()
        .map(attributeData => ({ name: attributeData.name, data: attributeData.history }));
    return { attributeData: attributeProgressChartData };

};

export default function PlayerProgressionView({ playerMetadata, playerRoles, 
    playerAttributes: { attributeCategories } }) {

    const attributeTableData = {
        ...buildAttributeTableData(attributeCategories),
        roles: playerRoles
    };

    return (
        <div>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <PlayerBioCard { ...playerMetadata } />
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs spacing={2}>
                    <AttributeProgressChart { ...buildAttributeProgressChartData(attributeCategories) } />
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs spacing={2}>
                    <PlayerAttributesTable { ...attributeTableData } >
                        <AttributeItem />
                    </PlayerAttributesTable>
                </Grid>
            </Grid>
        </div>
        
    );
}

PlayerProgressionView.propTypes = {
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
};