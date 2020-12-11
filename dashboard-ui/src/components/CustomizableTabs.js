import React from 'react';
import PropTypes from 'prop-types';

import Box from '@material-ui/core/Box';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';

export const TabPanel = ({ children, value, index, ...other }) => {
    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box p={3}>
                    { children }
                </Box>
            )}
        </div>
    );
};

TabPanel.propTypes = {
    children: PropTypes.node,
    index: PropTypes.any.isRequired,
    value: PropTypes.any.isRequired,
};

const a11yProps = (index) => ({
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`,
});

export default function CustomizableTabs({ children, onTabChange, tabValue, isFullWidth, ariaLabel, tabLabels }) {
    return (
        <div>
            <Tabs
                value={ tabValue }
                onChange={ onTabChange }
                aria-label={ ariaLabel }
                variant={ isFullWidth ? 'fullWidth' : 'standard' }
            >
                {
                    tabLabels.map((tabLabel, _idx) => (
                        <Tab label={ tabLabel } key={ _idx } { ...a11yProps(_idx)} />
                    ))
                }
            </Tabs>
            { children }
        </div>
    );
}

CustomizableTabs.propTypes = {
    children: PropTypes.arrayOf(PropTypes.node),
    onTabChange: PropTypes.func,
    tabValue: PropTypes.number,
    isFullWidth: PropTypes.bool,
    ariaLabel: PropTypes.string,
    tabLabels: PropTypes.arrayOf(PropTypes.string)
};