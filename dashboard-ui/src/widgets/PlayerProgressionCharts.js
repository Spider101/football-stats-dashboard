import { useState } from 'react';
import PropTypes from 'prop-types';

import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';
import AttributeProgressChart from '../components/AttributeProgressChart';
import OverallProgressChart from '../components/OverallProgressChart';

export default function PlayerProgressionCharts({ playerAttributeProgressData, playerOverallProgressData }) {
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
                tabLabels={['Attribute Progress', 'Overall Progress']}
            >
                <TabPanel value={ tabValue } index={0} panelLabel='Attribute Progress Chart'>
                    <AttributeProgressChart { ...playerAttributeProgressData } />
                </TabPanel>
                <TabPanel value={ tabValue } index={1} panelLabel='Overall Progress Chart'>
                    <OverallProgressChart { ...playerOverallProgressData } />
                </TabPanel>
            </CustomizableTabs>
        </div>
    );
}

PlayerProgressionCharts.propTypes = {
    playerAttributeProgressData: PropTypes.shape(AttributeProgressChart.propTypes.attributeData),
    playerOverallProgressData: PropTypes.shape(OverallProgressChart.propTypes.overallData)
};