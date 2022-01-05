import { useState, useCallback, useEffect } from 'react';

import { capitalizeLabel, convertCamelCaseToSnakeCase, formSubmission } from '../utils';

const getEmptyFieldValidation = fieldName =>
    `${capitalizeLabel(convertCamelCaseToSnakeCase(fieldName))} cannot be empty!`;
const validateEmail = email => /\S+@\S+\.\S+/.test(email);
const validatePlayerAge = playerAge => parseInt(playerAge) >= 15 && parseInt(playerAge) <= 50;

const validateInput = (name, value, formData) => {
    if (typeof value === 'string' && !value.trim()) return getEmptyFieldValidation(name);
    if (name === 'age' && !validatePlayerAge(value)) return 'Player age must be between 15 and 50 years!';
    if (name === 'email' && !validateEmail(value)) return 'Email format is incorrect!';
    if (name === 'newPassword' || name === 'confirmedPassword') {
        if (value.length < 6 || value.length > 12) return 'Password must be between 6 and 12 characters';
    }
    if (name === 'confirmedPassword' && value !== formData['newPassword']) return 'Passwords must match!';
};

const useForm = (defaultFormValues, callback) => {
    const [formData, setFormData] = useState(defaultFormValues);
    const [formValidations, setFormValidations] = useState({});
    const [submitStatus, setSubmitStatus] = useState(formSubmission.NOT_READY);
    const numFields = Object.keys(defaultFormValues).length;

    const handleChangeFn = e => {
        const { name, value } = e.target;

        // validate the input
        const validation = validateInput(name, value, formData);
        if (!validation) {
            // the current validation returned as falsy, so set submit status to READY if the remaining fields do not
            // have any validations either
            if (Object.values(formValidations).filter(validation => validation === null).length === (numFields - 1)) {
                setSubmitStatus(formSubmission.READY);
            }
        } else {
            setSubmitStatus(formSubmission.NOT_READY);
        }
        setFormValidations({
            ...formValidations,
            [name]: validation || null
        });

        // update form field data
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmitFn = e => {
        e.preventDefault();

        // do not update form submission state to IN PROGRESS if it is not in READY state
        if (submitStatus !== formSubmission.READY) return;

        // lock form by disabling input fields and button
        setSubmitStatus(formSubmission.INPROGRESS);
    };

    const postFormData = useCallback(
        // TODO: rename this from authaction to something more meaningful
        async authAction => {
            const formErrorMessage = await authAction(formData);
            if (formErrorMessage != null) {
                setSubmitStatus(formSubmission.NOT_READY);
                setFormValidations(formValidations => ({
                    ...formValidations,
                    form: formErrorMessage
                }));
            } else {
                // TODO: implement and invoke a function to reset the states here
                setSubmitStatus(formSubmission.COMPLETE);
            }
        },
        [formData]
    );

    useEffect(() => {
        // only invoke the callback if the form submission status is in INPROGRESS state
        if (submitStatus === formSubmission.INPROGRESS) {
            postFormData(callback);
        }
    }, [formValidations, submitStatus, callback, postFormData]);

    return {
        handleChangeFn,
        handleSubmitFn,
        formData,
        formValidations,
        submitStatus
    };
};

export default useForm;