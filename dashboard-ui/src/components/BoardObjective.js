import PropTypes from 'prop-types';

import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Checkbox from '@material-ui/core/Checkbox';
import Typography from '@material-ui/core/Typography';

export default function BoardObjective({ objective, hasDivider, handleClickFn }) {
    const objectiveTitle = (
        <Typography variant='h6' style={{ textDecoration: objective.isCompleted && 'line-through' }}>
            {objective.title}
        </Typography>
    );
    return (
        <ListItem button divider={hasDivider} onClick={handleClickFn}>
            <ListItemIcon>
                <Checkbox edge='start' checked={objective.isCompleted} />
            </ListItemIcon>
            <ListItemText primary={objectiveTitle} secondary={!objective.isCompleted && objective.description}/>
        </ListItem>
    );
}

BoardObjective.propTypes = {
    objective: PropTypes.shape({
        id: PropTypes.string, // TODO: update the type for this prop when finalized on server
        title: PropTypes.string,
        description: PropTypes.string,
        isCompleted: PropTypes.bool
    }),
    hasDivider: PropTypes.bool,
    handleClickFn: PropTypes.func
};