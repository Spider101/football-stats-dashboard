import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core';
import CircularProgress from '@material-ui/core/CircularProgress';

const styles = {
    wrapper: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        flexGrow: 1
    },
    root: {
        alignSelf: 'center',
        margin: '25vh'
    }
};

const LoadingCircleWrapper = ({ classes }) => (
    <div className={classes.wrapper}>
        <CircularProgress size={200} className={ classes.root }/>
    </div>
);
LoadingCircleWrapper.propTypes = {
    classes: PropTypes.object
};

export default withStyles(styles)(LoadingCircleWrapper);