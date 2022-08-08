import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import PlayerAttributesTable from '../components/PlayerAttributesTable';
import AttributeItem from '../components/AttributeItem';
import PlayerBioCard from '../components/PlayerBioCard';
import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';
import { transformIntoTabularData } from '../utils';

const getGrowthIndicator = history => {
    const value = history[history.length - 1];
    const penultimateValue = history.length === 1 ? value : history[history.length - 2];
    if (value > penultimateValue) {
        return 'up';
    } else if (value < penultimateValue) {
        return 'down';
    } else {
        return 'flat';
    }
};

const buildAttributeProgressChartData = attributeData => ({
    attributeData: attributeData.map(({ name, history }) => ({ name, history }))
});

const buildAbilityProgressChartData = ({ history }) => ({
    abilityData: { name: 'Player Ability', history }
});

export default function PlayerProgressionView({ playerMetadata, playerRoles, playerAbility, playerAttributes }) {
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
        playerAbilityProgressData: buildAbilityProgressChartData(playerAbility)
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
    playerAbility: PropTypes.shape({
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
