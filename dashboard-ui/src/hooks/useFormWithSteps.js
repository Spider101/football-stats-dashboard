import { useEffect, useState } from 'react';

import { formSubmission } from '../constants';
import useForm from './useForm';

const useFormWithSteps = (initialValues, callback, entity) => {
    const [activeStep, setActiveStep] = useState(0);
    const [globalFormData, setGlobalFormData] = useState(initialValues);
    const [formSubmissionResponse, setformSubmissionResponse] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    // assuming initialValues is a nested object with a nesting level of 1
    const localStates = Object.entries(initialValues).map(([key, value]) => {
        const { handleChangeFn, formData, formValidations, submitStatus } = useForm(value, null);
        return { id: key, handleChangeFn, formData, formValidations, submitStatus };
    });
    const updateGlobalFormData = () => {
        const localState = localStates[activeStep];
        setGlobalFormData({
            ...globalFormData,
            [localState.id]: localState.formData
        });
    };

    const postFormData = async () => {
        const errorMessage = await callback(globalFormData);
        setformSubmissionResponse({
            severity: errorMessage != null ? 'error' : 'success',
            message: errorMessage || `Added ${entity} successfully!`
        });
        setIsSubmitting(false);
    };

    const handleNextFn = () => {
        if (getSubmitStatusAtStep(activeStep) === formSubmission.READY) {
            updateGlobalFormData();
            setActiveStep(prevActiveStep => prevActiveStep + 1);
        }
    };

    const handleBackFn = () => {
        setActiveStep(prevActiveStep => prevActiveStep - 1);
    };

    const handleSubmitFn = () => {
        if (getSubmitStatusAtStep(activeStep) === formSubmission.READY) {
            setIsSubmitting(true);
        }
    };

    const getFormMetadataAtStep = stepIdx => localStates[stepIdx];
    const getSubmitStatusAtStep = stepIdx => {
        if (stepIdx < localStates.length) {
            return localStates[stepIdx].submitStatus;
        } else {
            // there is no form data on the last step (confirmation page), so submission status is always READY
            return formSubmission.READY;
        }
    };

    useEffect(() => {
        if (isSubmitting) {
            postFormData();
        }
    }, [isSubmitting]);

    return {
        formData: globalFormData,
        activeStep,
        formSubmissionResponse,
        handleSubmitFn,
        handleNextFn,
        handleBackFn,
        getFormMetadataAtStep,
        getSubmitStatusAtStep
    };
};

export default useFormWithSteps;