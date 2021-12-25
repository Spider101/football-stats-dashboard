import PropTypes from 'prop-types';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Divider from '@material-ui/core/Divider';

import { formSubmission } from '../utils';

export default function DialogForm({
    children,
    isOpen,
    dialogTitle,
    handleSubmit,
    handleClose,
    submitStatus,
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
                <Button
                    disabled={submitStatus !== formSubmission.READY}
                    onClick={activeStep < numSteps ? handleNext : handleSubmit}
                    color='primary'
                >
                    {activeStep < numSteps ? 'Next' : 'Submit'}
                </Button>
            </DialogActions>
        </Dialog>
    );
}

DialogForm.propTypes = {
    children: PropTypes.arrayOf(PropTypes.node),
    isOpen: PropTypes.bool,
    dialogTitle: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.object,
    ]),
    handleSubmit: PropTypes.func,
    handleClose: PropTypes.func,
    submitStatus: PropTypes.string,
    handleNext: PropTypes.func,
    handleBack: PropTypes.func,
    activeStep: PropTypes.number,
    numSteps: PropTypes.number
};