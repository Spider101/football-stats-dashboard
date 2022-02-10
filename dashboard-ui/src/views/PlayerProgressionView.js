import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import PlayerAttributesTable from '../components/PlayerAttributesTable';
import AttributeItem from '../components/AttributeItem';
import PlayerBioCard from '../components/PlayerBioCard';
import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';
import { transformIntoTabularData } from '../utils';

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

// TODO: 07/02/22 change how the data is being built once the rechart component is ready
const buildAttributeProgressChartData = attributeData => {
    const attributeProgressChartData = attributeData
        .map(attribute => ({ name: attribute.name, history: attribute.history }));
    return { attributeData: attributeProgressChartData };
};

const buildOverallProgressChartData = ({ history: overallHistory }) => ({
    overallData: [{ name: 'Player Ability', data: overallHistory }]
});

export default function PlayerProgressionView({ playerMetadata, playerRoles, playerOverall, playerAttributes }) {
    const categories = Array.from(new Set(playerAttributes.map(attribute => attribute.category)));
    const buildAttributeData = attribute => ({
        attributeName: attribute.name,
        attributeValue: attribute.value,
        highlightedAttributes: [],
        growthIndicator: getGrowthIndicator(attribute.history)
    });
    const filterByCategory = (attribute, categoryName) => attribute.category === categoryName;
    const attributeTableData = {
        ...transformIntoTabularData(playerAttributes, categories, filterByCategory, buildAttributeData),
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
