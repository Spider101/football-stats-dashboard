import PropTypes from 'prop-types';
import { useCallback } from 'react';

import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Divider from '@material-ui/core/Divider';

import Alert from '../components/Alert';
import DialogForm from '../components/DialogForm';
import PageAction from '../components/PageAction';

import { capitalizeLabel, convertCamelCaseToSnakeCase } from '../utils';
import useFormWithSteps from '../hooks/useFormWithSteps';
import AddPlayerForm, { getAddPlayerFormSchema, getStepper } from './AddPlayerForm';

export default function AddPlayer({ addPlayerAction }) {
    const {
        formData: nestedFormData,
        activeStep,
        formSubmissionResponse,
        handleSubmitFn,
        handleNextFn,
        handleBackFn,
        getFormMetadataAtStep,
        getSubmitStatusAtStep
    } = useFormWithSteps(getAddPlayerFormSchema(),
        useCallback(newPlayerData => addPlayerAction(newPlayerData), []),
        'Player'
    );

    const { stepper: dialogTitle, numSteps } = getStepper(activeStep);
    const currentSubmitStatus = getSubmitStatusAtStep(activeStep);
    const addNewPlayerDialogForm = (
        <DialogForm
            dialogTitle={dialogTitle}
            handleSubmit={handleSubmitFn}
            submitStatus={currentSubmitStatus}
            handleNext={handleNextFn}
            handleBack={handleBackFn}
            numSteps={numSteps}
            activeStep={activeStep}
        >
            <div style={{ width: '100%', marginBottom: '8px' }}>
                {Object.keys(formSubmissionResponse).length !== 0 && (
                    <Alert severity={formSubmissionResponse.severity} text={formSubmissionResponse.message} />
                )}
            </div>
            {activeStep === numSteps ? (
                <>
                    <Typography variant='h5' align='center'>Confirm Details</Typography>
                    {Object.entries(nestedFormData).map(([sectionName, formDataInSection]) => {
                        const sectionTitle = capitalizeLabel(convertCamelCaseToSnakeCase(sectionName));
                        return (
                            <>
                                <Typography variant='h6' color='textSecondary'>{sectionTitle}</Typography>
                                <Divider />
                                <List key={sectionName}>
                                    {Object.entries(formDataInSection).map(([fieldName, fieldValue]) => {
                                        const formDataText = `${capitalizeLabel(
                                            convertCamelCaseToSnakeCase(fieldName)
                                        )}: ${fieldValue}`;
                                        return (
                                            // TODO: figure out how to display this as field name aligned to the left
                                            // and field value aligned to the right
                                            <ListItem key={fieldName}>
                                                <ListItemText primary={formDataText} />
                                            </ListItem>
                                        );
                                    })}
                                </List>
                            </>
                        );
                    })}
                </>
            ) : (
                <AddPlayerForm getFormMetadataAtStep={getFormMetadataAtStep} stepIdx={activeStep} />
            )}
        </DialogForm>
    );

    return <PageAction actionType='add' dialog={addNewPlayerDialogForm} />;
}

AddPlayer.propTypes = {
    addPlayerAction: PropTypes.func
};