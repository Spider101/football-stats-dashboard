import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import AttributeComparisonTable from '../widgets/AttributeComparisonTable';
import { Card, Typography, CardContent, CardMedia, makeStyles, Avatar, Box, AppBar, Tabs, Tab } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex'
    },
    content: {
        display: 'flex',
        flexDirection: 'column'
    },
    avatarGroup: {
        display: 'flex',
        flexDirection: 'row'
    },
    clubLogo: {
        marginRight: 5,
        height: 25,
        width: 25
    },
    media: {
        minWidth: 151,
    },
    attrComparisonTabs: {
        flexGrow: 1,
        backgroundColor: theme.palette.background.paper,
    }
}));

const createAttributeComparisonData = (attributeGroupList1, attributeGroupList2, playerNames) => {

    const maxRows = Math.max( ...attributeGroupList1.map(attrGroup => attrGroup.attributesInGroup.length));

    let tabularAttributeData = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(attributeGroupList1.length) ].map((_, j) => {
            const currentAttributeGroup1 = attributeGroupList1[j].attributesInGroup;
            const currentAttributeGroup2 = attributeGroupList2[j].attributesInGroup;

            return i <= currentAttributeGroup1.length ? {
                attrComparisonItem: {
                    attrValues: [{
                        name: playerNames[0].split(' ')[1],
                        data: [ -1 * currentAttributeGroup1[i]['value'] ]
                    }, {
                        name: playerNames[1].split(' ')[1],
                        data: [ currentAttributeGroup2[i]['value'] ]
                    }],
                    label: currentAttributeGroup1[i]['name']
                },
                isHighlighted: false
            } : null;
        })
    ));

    return {
        headers: attributeGroupList1.map(attrGroup => attrGroup.groupName),
        rows: tabularAttributeData
    };
};

const TabPanel = ({ children, value, index, ...other }) => {
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

export default function PlayerComparisonView({ players }) {
    const classes = useStyles();

    const [ value, setValue ] = React.useState(0);

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    const playerA = players.find(player => player.isSelected && player.orientation == 'LEFT');
    
    const playerB = players.find(player => player.isSelected && player.orientation == 'RIGHT');

    const attributeComparisonData = createAttributeComparisonData(playerA.playerAttributes, playerB.playerAttributes,
        [ playerA.playerMetadata.name, playerB.playerMetadata.name ]);

    return (
        <div>
            <Grid container spacing={2}>
                <Grid item xs={6}>
                    {/* TODO: refactor this into its own component to reduce code duplication */}
                    <Card className={classes.root}>
                        <CardMedia
                            className={classes.media}
                            image={ playerA.playerMetadata.photo }
                        />
                        <CardContent className={classes.content}>
                            <Typography component="h5" variant="h5">
                                { playerA.playerMetadata.name }
                            </Typography>
                            <div className={ classes.avatarGroup }>
                                <Avatar className={classes.clubLogo} src={ playerA.playerMetadata.clubLogo } />
                                <Typography variant="subtitle1" color="textSecondary">
                                    {  playerA.playerMetadata.club }
                                </Typography>
                            </div>
                            <Typography variant="subtitle1" color="textSecondary">
                                { renderPlayerDOB(playerA.playerMetadata) }
                            </Typography>
                            <div className={ classes.avatarGroup }>
                                <Avatar className={classes.clubLogo} src={ playerA.playerMetadata.countryLogo } />
                                <Typography variant="subtitle1" color="textSecondary">
                                    {  playerA.playerMetadata.country }
                                </Typography>
                            </div>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={6}>
                    <Card className={classes.root}>
                        <CardMedia
                            className={classes.media}
                            image={ playerB.playerMetadata.photo }
                        />
                        <CardContent className={classes.content}>
                            <Typography component="h5" variant="h5">
                                { playerB.playerMetadata.name }
                            </Typography>
                            <div className={ classes.avatarGroup }>
                                <Avatar className={classes.clubLogo} src={ playerB.playerMetadata.clubLogo } />
                                <Typography variant="subtitle1" color="textSecondary">
                                    {  playerB.playerMetadata.club }
                                </Typography>
                            </div>
                            <Typography variant="subtitle1" color="textSecondary">
                                { renderPlayerDOB(playerB.playerMetadata) }
                            </Typography>
                            <div className={ classes.avatarGroup }>
                                <Avatar className={classes.clubLogo} src={ playerB.playerMetadata.countryLogo } />
                                <Typography variant="subtitle1" color="textSecondary">
                                    {  playerB.playerMetadata.country }
                                </Typography>
                            </div>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <AppBar position="static">
                        <Tabs
                            value={ value }
                            onChange={ handleChange }
                            aria-label="player attributes comparison tabs"
                            variant="fullWidth"
                        >
                            <Tab label="Overview" { ...a11yProps(0) } />
                            <Tab label="Attributes" { ...a11yProps(1) } />
                        </Tabs>
                    </AppBar>
                    <TabPanel value={ value } index={0}>
                        <Typography variant="h2" component="h2">
                            Work in Progress
                        </Typography>
                    </TabPanel>
                    <TabPanel value={ value }  index={1}>
                        <AttributeComparisonTable { ...attributeComparisonData } />
                    </TabPanel>
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
            playerMetadata: PropTypes.shape({
                name: PropTypes.string,
                club: PropTypes.string,
                clubLogo: PropTypes.string,
                dob: PropTypes.string,
                age: PropTypes.number,
                country:  PropTypes.string,
                countryLogo: PropTypes.string,
                image: PropTypes.string,
            }),
            playerAttributes: PropTypes.arrayOf(
                PropTypes.shape({
                    groupName: PropTypes.string,
                    attributesInGroup: PropTypes.arrayOf(
                        PropTypes.shape({
                            name: PropTypes.string,
                            value: PropTypes.number
                        })
                    )
                })
            )
        })
    )
};

const a11yProps = (index) => ({
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`,
});

const renderPlayerDOB = (playerMetadata) =>
    playerMetadata.dob.toJSON().slice(0, 10).split`-`.join`/` + playerMetadata.age;
