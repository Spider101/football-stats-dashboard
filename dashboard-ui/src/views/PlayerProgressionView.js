import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import PlayerAttributesTable from '../components/PlayerAttributesTable';
import AttributeItem from '../components/AttributeItem';
import PlayerBioCard from '../components/PlayerBioCard';
import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';

const getGrowthIndicator = history => {
    const value = history[history.length - 1];
    const penultimateValue = history[history.length - 2];
    if (value > penultimateValue) {
        return 'up';
    } else if (value < penultimateValue) {
        return 'down';
    } else {
        return 'flat';
    }
};

/**
 * convert the attributes collection into a format that can be fed into a table DOM structure
 * @param {array} attributes
 * @returns
 */
const buildAttributeTableData = attributes => {
    const categories = Array.from(new Set(attributes.map(attribute => attribute.category)));
    const data = categories.map(category =>
        attributes
            .filter(attribute => attribute.category === category)
            .map(attribute => ({
                attributeName: attribute.name,
                attributeValue: attribute.value,
                highlightedAttributes: [],
                growthIndicator: getGrowthIndicator(attribute.history)
            }))
    );
    const maxRows = Math.max(...data.map(row => row.length));

    // transpose the attribute data so that each column corresponds to a given attribute category
    const rows = [...Array(maxRows)].map((_, i) =>
        [...Array(categories.length)].map((_, j) => (i > data[j].length ? null : data[j][i]))
    );
    return {
        headers: categories,
        rows
    };
};

const buildAttributeProgressChartData = attributeData => {
    const attributeProgressChartData = attributeData
        .map(attribute => ({ name: attribute.name, data: attribute.history }));
    return { attributeData: attributeProgressChartData };
};

const buildOverallProgressChartData = ({ history: overallHistory }) => ({
    overallData: [{ name: 'Player Ability', data: overallHistory }]
});

export default function PlayerProgressionView({ playerMetadata, playerRoles, playerOverall, playerAttributes }) {
    const attributeTableData = {
        ...buildAttributeTableData(playerAttributes),
        roles: playerRoles
    };

    const playerProgressionChartData = {
        playerAttributeProgressData: buildAttributeProgressChartData(playerAttributes),
        playerOverallProgressData: buildOverallProgressChartData(playerOverall)
    };

    return (
        <>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <PlayerBioCard {...playerMetadata} />
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs>
                    <PlayerProgressionCharts {...playerProgressionChartData} />
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs>
                    <PlayerAttributesTable {...attributeTableData}>
                        <AttributeItem />
                    </PlayerAttributesTable>
                </Grid>
            </Grid>
        </>
    );
}

PlayerProgressionView.propTypes = {
    playerMetadata: PropTypes.shape(PlayerBioCard.propTypes),
    playerRoles: PropTypes.array,
    playerOverall: PropTypes.shape({
        currentValue: PropTypes.number,
        history: PropTypes.arrayOf(PropTypes.number)
    }),
    playerAttributes: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string,
            group: PropTypes.string,
            category: PropTypes.string,
            value: PropTypes.number,
            history: PropTypes.arrayOf(PropTypes.number)
        })
    )
};
