import PropTypes from 'prop-types';

import Typography from '@material-ui/core/Typography';
import Card from '@material-ui/core/Card';
import CardMedia from '@material-ui/core/CardMedia';
import CardContent from '@material-ui/core/CardContent';
import Avatar from '@material-ui/core/Avatar';

import { makeStyles } from '@material-ui/core/styles';

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
    logo: {
        marginRight: 5,
        height: 25,
        width: 25
    },
    media: {
        minWidth: 151,
    }
});

const renderPlayerDOB = (dob, age) => `${dob.slice(0, 10).split`-`.join`/`} (${age} years old)`;

export default function PlayerBioCard({  name, club, clubLogo, dateOfBirth, age, country, countryLogo, photo }) {
    const classes = useStyles();

    return (
        <Card className={classes.root}>
            <CardMedia
                className={classes.media}
                image={ photo }
            />
            <CardContent className={classes.content}>
                <Typography component="h5" variant="h5">
                    { name }
                </Typography>
                <div className={ classes.avatarGroup }>
                    <Avatar className={classes.logo} src={ clubLogo } />
                    <Typography variant="subtitle1" color="textSecondary">
                        {  club }
                    </Typography>
                </div>
                <Typography variant="subtitle1" color="textSecondary">
                    { renderPlayerDOB(dateOfBirth, age) }
                </Typography>
                <div className={ classes.avatarGroup }>
                    <Avatar className={classes.logo} src={ countryLogo } />
                    <Typography variant="subtitle1" color="textSecondary">
                        {  country }
                    </Typography>
                </div>
            </CardContent>
        </Card>
    );
}

PlayerBioCard.propTypes = {
    name: PropTypes.string,
    club: PropTypes.string,
    clubLogo: PropTypes.string,
    dateOfBirth: PropTypes.string,
    age: PropTypes.number,
    country:  PropTypes.string,
    countryLogo: PropTypes.string,
    photo: PropTypes.string
};