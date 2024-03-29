import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import { capitalizeLabel } from '../utils';
import { caseFormat } from '../constants';

const styles = theme => ({
    toolTip: {
        backgroundColor: theme.palette.background.default,
        color: theme.palette.text.secondary,
        textAlign: 'center',
        boxShadow: theme.shadows[10],
        borderRadius: '5px',
        padding: theme.spacing(0.25, 1, 0.25, 1),
        '&>p:first-child': {
            borderBottomColor: theme.palette.divider,
            borderBottom: '1px solid'
        }
    }
});
const StyledToolTip = withStyles(styles)(({ classes, children }) => (
    <div className={classes.toolTip}>
        {children}
    </div>
));

const CustomToolTip = ({ active, payload, label, shouldShowNegativeValues = true }) => {
    if (active) {
        return (
            <StyledToolTip>
                <p>{label}</p>
                {payload.map(item => (
                    <p style={{ color: `${item.stroke || item.fill}` }} key={item.name}>
                        {`${capitalizeLabel(item.name, caseFormat.CAMEL_CASE)} : ${
                            shouldShowNegativeValues ? item.value : Math.abs(item.value)
                        }`}
                    </p>
                ))}
            </StyledToolTip>
        );
    }
    return null;
};
CustomToolTip.propTypes = {
    active: PropTypes.bool,
    payload: PropTypes.array,
    label: PropTypes.string,
    shouldShowNegativeValues: PropTypes.bool
};
export default CustomToolTip;