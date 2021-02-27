import React from 'react'
import PropTypes from 'prop-types'

import SpeedDial from '@material-ui/lab/SpeedDial';
import SpeedDialIcon from '@material-ui/lab/SpeedDialIcon';
import SpeedDialAction from '@material-ui/lab/SpeedDialAction';
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
    speedDial: {
        position: 'fixed',
        '&.MuiSpeedDial-directionUp, &.MuiSpeedDial-directionLeft': {
            bottom: theme.spacing(2),
            right: theme.spacing(2),
        },
        '&.MuiSpeedDial-directionDown, &.MuiSpeedDial-directionRight': {
            top: theme.spacing(2),
            left: theme.spacing(2),
        },
    },
}));

export default function PageActions({ actions }) {
    const classes = useStyles();
    const [open, setOpen] = React.useState(false);

    const handleClose = () => {
        setOpen(false);
    };

    const handleOpen = () => {
        setOpen(true);
    };
    return (
        <SpeedDial
            ariaLabel="Page Actions"
            className={classes.speedDial}
            icon={<SpeedDialIcon />}
            onClose={handleClose}
            onOpen={handleOpen}
            open={open}
        >
        {
            actions.map((action) => (
                <SpeedDialAction
                    key={action.name}
                    icon={action.icon}
                    tooltipTitle={action.name}
                    onClick={action.clickHandler}
                />
            ))
        }
        </SpeedDial>
    );
}

PageActions.propTypes = {
    actions: PropTypes.arrayOf(PropTypes.shape({
        icon: PropTypes.object,
        name: PropTypes.string
    }))
}