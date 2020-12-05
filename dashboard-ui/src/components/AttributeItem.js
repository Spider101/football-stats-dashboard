import React from 'react';
import PropTypes from 'prop-types';

import { fade, makeStyles } from '@material-ui/core';
import Typography from '@material-ui/core/Typography';
import clsx from 'clsx';

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
        justifyContent: 'space-between',
        padding: 5
    },
    highlighted: {
        borderLeft: '2px solid',
        borderLeftColor: theme.palette.primary.main,
        backgroundColor: fade(theme.palette.primary.main, 0.15)
    }
}));

export default function AttributeItem({ attributeName, attributeValue, isHighlighted}) {
    const classes = useStyles();
    return (
        <div className={ clsx(classes.root, {
            [classes.highlighted]: isHighlighted
        })}>
            <Typography component='p' variant='body1'>{ attributeName }</Typography>
            <Typography component='p' variant='body1'>{ attributeValue }</Typography>
        </div>
    );
}

AttributeItem.propTypes = {
    attributeName: PropTypes.string,
    attributeValue: PropTypes.number,
    isHighlighted: PropTypes.bool
};