import { useState } from 'react';
import PropTypes from 'prop-types';

import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';
import AttributeProgressChart from '../components/AttributeProgressChart';
import AbilityProgressChart from '../components/AbilityProgressChart';

export default function PlayerProgressionCharts({ playerAttributeProgressData, playerAbilityProgressData }) {
    const [ tabValue, setTabValue ] = useState(0);

    const handleTabChange = (_, newTabValue) => {
        setTabValue(newTabValue);
    };

    return (
        <div>
            <CustomizableTabs
                onTabChange={ handleTabChange }
                tabValue= { tabValue }
                isFullWidth={ false }
                ariaLabel="Player Progression Chart Tabs"
                tabLabels={['Attribute Progress', 'Ability Progress']}
            >
                <TabPanel value={ tabValue } index={0} panelLabel='Attribute Progress Chart'>
                    <AttributeProgressChart { ...playerAttributeProgressData } />
                </TabPanel>
                <TabPanel value={ tabValue } index={1} panelLabel='Ability Progress Chart'>
                    <AbilityProgressChart { ...playerAbilityProgressData } />
                </TabPanel>
            </CustomizableTabs>
        </div>
    );
}

PlayerProgressionCharts.propTypes = {
    playerAttributeProgressData: PropTypes.shape(AttributeProgressChart.propTypes.attributeData),
    playerAbilityProgressData: PropTypes.shape(AbilityProgressChart.propTypes.abilityData)
};