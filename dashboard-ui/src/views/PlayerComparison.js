import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import AttributeComparisonTable from '../widgets/AttributeComparisonTable';
import { Card, Typography, CardContent, CardMedia, makeStyles, Avatar } from '@material-ui/core';

const useStyles = makeStyles({
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
});

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

export default function PlayerComparisonView({ players }) {
    const classes = useStyles();
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
                    <AttributeComparisonTable { ...attributeComparisonData } />
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

function renderPlayerDOB(playerMetadata) {
    return playerMetadata.dob.toJSON().slice(0, 10).split`-`.join`/` + playerMetadata.age;
}
