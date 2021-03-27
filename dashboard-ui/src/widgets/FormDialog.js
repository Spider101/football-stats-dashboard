import React from 'react';
import PropTypes from 'prop-types';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

export default function FormDialog({ open, handleClose, dialogTitle, formData, useFormBuilder }) {

    const { form, handleSubmit } = useFormBuilder(formData);
    return (
        <>
            <Dialog open={ open } onClose={ handleClose } aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">{ dialogTitle }</DialogTitle>
                <DialogContent>
                    { form }
                </DialogContent>
                <DialogActions>
                    <Button onClick={ handleClose } color="primary">Cancel</Button>
                    <Button onClick={ handleSubmit } color="primary">Submit</Button>
                </DialogActions>
            </Dialog>
        </>
    );
}

FormDialog.propTypes = {
    open: PropTypes.bool,
    handleClose: PropTypes.func,
    dialogTitle: PropTypes.string,
    formData: PropTypes.object,
    useFormBuilder: PropTypes.func,
};

