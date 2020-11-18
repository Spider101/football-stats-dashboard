import React from 'react';
import PropTypes from 'prop-types';

import Box from '@material-ui/core/Box';
import AppBar from '@material-ui/core/AppBar';
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

export default function SimpleFixedTabs({ children, onTabChange, tabValue }) {
    return (
        <div>
            <AppBar position="static">
                <Tabs
                    value={ tabValue }
                    onChange={ onTabChange }
                    aria-label="player attributes comparison tabs"
                    variant="fullWidth"
                >
                    <Tab label="Overview" { ...a11yProps(0) } />
                    <Tab label="Attributes" { ...a11yProps(1) } />
                </Tabs>
            </AppBar>
            { children }
        </div>
    );
}

SimpleFixedTabs.propTypes = {
    children: PropTypes.arrayOf(PropTypes.node),
    onTabChange: PropTypes.func,
    tabValue: PropTypes.number
};