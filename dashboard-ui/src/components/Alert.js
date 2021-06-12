import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';

import { fade, makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

const useStyles = makeStyles((theme) => ({
    root: {
        marginTop: theme.spacing(1),
        padding: theme.spacing(2),
        borderRadius: '4px',
        textAlign: 'center'
    },
    success: {
        color: theme.palette.success.dark,
        backgroundColor: fade(theme.palette.success.light, 0.3),
        border: `1px solid ${theme.palette.success.dark}`
    },
    error: {
        color: theme.palette.error.dark,
        backgroundColor: fade(theme.palette.error.light, 0.3),
        border: `1px solid ${theme.palette.error.dark}`
    }
}));

export default function Alert({ severity, text }) {
    const classes = useStyles();
    return (
        <div className={clsx(classes.root, {
            [classes.success]: severity === 'success',
            [classes.error]: severity === 'error' })}>
            <Typography component='p'>
                { text }
            </Typography>
        </div>
    );
}

Alert.propTypes = {
    severity: PropTypes.string,
    text: PropTypes.string
};
