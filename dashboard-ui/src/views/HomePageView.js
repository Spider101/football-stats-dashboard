import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';

import { makeStyles } from '@material-ui/core/styles';

import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';
import Avatar from '@material-ui/core/Avatar';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';

import { getImageDownloadURI } from '../clients/FileUploadClient';

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

export default function HomePageView({ clubSummaries, addClubWidget }) {
    const classes = useStyles();

    const noClubsView = (
        <Typography variant='h5' style={{ textAlign: 'center', padding: '5%' }}>
            No clubs have been created yet! Please create a club to proceed.
        </Typography>
    );

    const allCLubsView = (
        <List className={classes.clubs}>
            {clubSummaries.map(clubSummary => {
                return (
                    <ListItem key={clubSummary.clubId} divider>
                        <ListItemAvatar>
                            <Avatar src={`${getImageDownloadURI(clubSummary.logo)}`} />
                        </ListItemAvatar>
                        <ListItemText
                            primary={clubSummary.name}
                            primaryTypographyProps={{ variant: 'h5' }}
                            secondary={`Created: ${clubSummary.createdDate}`}
                        />
                        <ListItemSecondaryAction>
                            <Button variant='outlined' component={Link} to={`/club/${clubSummary.clubId}`}>
                                Open
                            </Button>
                        </ListItemSecondaryAction>
                    </ListItem>
                );
            })}
        </List>
    );

    return (
        <Container>
            <Typography variant='h2'>Clubs</Typography>
            <Divider />
            {clubSummaries.length === 0 ? noClubsView : allCLubsView}
            <div className={classes.fab}>
                {addClubWidget}
            </div>
        </Container>
    );
}

HomePageView.propTypes = {
    clubSummaries: PropTypes.arrayOf(
        PropTypes.shape({
            clubId: PropTypes.string,
            name: PropTypes.string,
            createdDate: PropTypes.string
        })
    ),
    addClubWidget: PropTypes.element
};