import React from 'react';
import PropTypes from 'prop-types';

import Typography from '@material-ui/core/Typography';
import TrendingUpIcon from '@material-ui/icons/TrendingUp';
import TrendingDownIcon from '@material-ui/icons/TrendingDown';
import TrendingFlatIcon from '@material-ui/icons/TrendingFlat';

import { fade, makeStyles } from '@material-ui/core';
import clsx from 'clsx';

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
        justifyContent: 'space-between',
        padding: 5,
        '& :nth-child(1)': {
            flexGrow: 2
        },
        '& :nth-child(2)': {
            flexGrow: 2
        }
    },
    highlighted: {
        borderLeft: '2px solid',
        borderLeftColor: theme.palette.primary.main,
        backgroundColor: fade(theme.palette.primary.main, 0.15)
    }
}));

const growthIndicatorMap = {
    'up': <TrendingUpIcon />,
    'flat': <TrendingFlatIcon />,
    'down': <TrendingDownIcon />
};

export default function AttributeItem({ attributeName, attributeValue, isHighlighted, growthIndicator }) {
    const classes = useStyles();
    return (
        <div className={ clsx(classes.root, {
            [classes.highlighted]: isHighlighted
        })}>
            <Typography component='p' variant='body1'>{ attributeName }</Typography>
            { growthIndicatorMap[growthIndicator] }
            <Typography component='p' variant='body1'>{ attributeValue }</Typography>
        </div>
    );
}

AttributeItem.propTypes = {
    attributeName: PropTypes.string,
    attributeValue: PropTypes.number,
    isHighlighted: PropTypes.bool,
    growthIndicator: PropTypes.string
};