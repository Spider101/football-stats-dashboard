import React from 'react';

import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';
import AttributeProgressChart from '../components/AttributeProgressChart';
import OverallProgressChart from '../components/OverallProgressChart';

export default function PlayerProgressionCharts({ playerAttributeProgressData, playerOverallProgressData }) {
    const [ tabValue, setTabValue ] = React.useState(0);

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
                <TabPanel value={ tabValue } index={0}>
                    <AttributeProgressChart { ...playerAttributeProgressData } />
                </TabPanel>
                <TabPanel value={ tabValue } index={1}>
                    <OverallProgressChart { ...playerOverallProgressData } />
                </TabPanel>
            </CustomizableTabs>
        </div>
    );
}

PlayerProgressionCharts.propTypes = {
    playerAttributeProgressData: AttributeProgressChart.propTypes.attributeData,
    playerOverallProgressData: AttributeProgressChart.propTypes.attributeData
};