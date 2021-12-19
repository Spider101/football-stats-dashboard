import PropTypes from 'prop-types';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Divider from '@material-ui/core/Divider';

export default function DialogForm({
    children,
    isOpen,
    dialogTitle,
    handleSubmit,
    handleClose,
    handleNext = null,
    handleBack = null,
    numSteps = 1,
    activeStep = 0
}) {
    return (
        <Dialog open={isOpen} onClose={handleClose} aria-labelledby='dialog-form-title'>
            <DialogTitle id='dialog-form-title'>{dialogTitle}</DialogTitle>
            <Divider variant='middle' />
            <DialogContent>{children}</DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color='primary'>
                    Cancel
                </Button>
                {activeStep !== 0 && (
                    <Button onClick={handleBack} color='primary'>
                        Back
                    </Button>
                )}
                <Button onClick={activeStep < numSteps - 1 ? handleNext : handleSubmit} color='primary'>
                    {activeStep < numSteps - 1 ? 'Next' : 'Submit'}
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
    handleClose: PropTypes.func,
    handleNext: PropTypes.func,
    handleBack: PropTypes.func,
    activeStep: PropTypes.number,
    numSteps: PropTypes.number
};