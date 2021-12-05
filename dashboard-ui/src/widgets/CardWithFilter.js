import PropTypes from 'prop-types';

import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';

import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
    emptyCard: {
        display: 'flex',
        justifyContent: 'center',
        backgroundColor: theme.palette.action.selected,
        borderStyle: 'dashed',
        height: '100%'
    }
}));

export default function CardWithFilter({ filterControl }) {
    const classes = useStyles();

    return (
        <Card className={ classes.emptyCard } variant='outlined'>
            <CardContent>
                { filterControl }
            </CardContent>
        </Card>
    );
}

CardWithFilter.propTypes = {
    filterControl: PropTypes.node,
};