import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';

import { makeStyles } from '@material-ui/core/styles';

import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Divider from '@material-ui/core/Divider';

const useStyles = makeStyles(theme => ({
    fab: {
        position: 'fixed',
        bottom: theme.spacing(4),
        right: theme.spacing(4)
    },
    clubs: {
        margin: theme.spacing(2)
    }
}));

export default function HomePageView({ clubs, addClubWidget }) {
    const classes = useStyles();

    const noClubsView = (
        <Typography variant='h5' style={{ textAlign: 'center', padding: '5%' }}>
            No clubs have been created yet! Please create a club to proceed.
        </Typography>
    );

    const allCLubsView = (
        <List className={classes.clubs}>
            {clubs.map(club => {
                return (
                    <ListItem key={club.id} button divider component={Link} to={`/club/${club.id}`}>
                        <ListItemText primary={club.name} />
                    </ListItem>
                );
            })}
        </List>
    );

    return (
        <Container>
            <Typography variant='h2'>Clubs</Typography>
            <Divider />
            {clubs.length === 0 ? noClubsView : allCLubsView}
            <div className={classes.fab}>
                {addClubWidget}
            </div>
        </Container>
    );
}

HomePageView.propTypes = {
    clubs: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.string,
            name: PropTypes.string,
            transferBudget: PropTypes.string,
            wageBudget: PropTypes.string,
            income: PropTypes.string,
            expenditure: PropTypes.string
        })
    ),
    addClubWidget: PropTypes.node
};