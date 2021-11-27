import React from 'react';
import PropTypes from 'prop-types';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

export default function DialogForm({ children, isOpen, dialogTitle, handleSubmit, handleClose }) {
    return (
        <Dialog open={isOpen} onClose={handleClose} aria-labelledby="dialog-form-title">
            <DialogTitle id="dialog-form-title">{dialogTitle}</DialogTitle>
            <DialogContent>{children}</DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color="primary">
                    Cancel
                </Button>
                <Button onClick={handleSubmit} color="primary">
                    Submit
                </Button>
            </DialogActions>
        </Dialog>
    );
}

DialogForm.propTypes = {
    children: PropTypes.arrayOf(PropTypes.node),
    isOpen: PropTypes.bool,
    dialogTitle: PropTypes.string,
    handleSubmit: PropTypes.func,
    handleClose: PropTypes.func
};