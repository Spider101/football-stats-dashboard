import React from 'react';

import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';

import FilterControl from '../components/FilterControl';
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
    emptyCard: {
        display: 'flex',
        justifyContent: 'center',
        backgroundColor: theme.palette.grey[100],
        borderStyle: 'dashed',
    }
}));

export default function CardWithFilter({ filterControl }) {
    const classes = useStyles();

    return (
        <Card className={ classes.emptyCard } variant='outlined'>
            <CardContent>
                <FilterControl { ...filterControl } />
            </CardContent>
        </Card>
    );
}

CardWithFilter.propTypes = {
    filterControl: FilterControl.propTypes
};