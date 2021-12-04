import { useState, cloneElement } from 'react';
import PropTypes from 'prop-types';

import Fab from '@material-ui/core/Fab';
import EditIcon from '@material-ui/icons/Edit';
import AddIcon from '@material-ui/icons/Add';
import DeleteIcon from '@material-ui/icons/Delete';

const actionTypeToIconMap = {
    edit: <EditIcon />,
    add: <AddIcon />,
    delete: <DeleteIcon />
};

export default function PageAction({ dialog, actionType }) {
    const [isFormOpen, setIsFormOpen] = useState(false);

    const handleFormOpen = () => {
        setIsFormOpen(true);
    };

    const handleFormClose = () => {
        setIsFormOpen(false);
    };

    return (
        <>
            <Fab color='secondary' aria-label={actionType} onClick={handleFormOpen}>
                {actionTypeToIconMap[actionType]}
            </Fab>
            {cloneElement(dialog, { isOpen: isFormOpen, handleClose: handleFormClose })}
        </>
    );
}

PageAction.propTypes = {
    actionType: PropTypes.string,
    dialog: PropTypes.node
};
