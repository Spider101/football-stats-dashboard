import { useState, useCallback, useEffect } from 'react';

import { capitalizeLabel, convertCamelCaseToSnakeCase, formSubmission } from '../utils';

const getEmptyFieldValidation = fieldName =>
    `${capitalizeLabel(convertCamelCaseToSnakeCase(fieldName))} cannot be empty!`;
const validateEmail = email => /\S+@\S+\.\S+/.test(email);

const validateInput = (name, value, formData) => {
    if ((isNaN(value) || isNaN(parseInt(value))) && !value.trim()) return getEmptyFieldValidation(name);
    if (name === 'email' && !validateEmail(value)) return 'Email format is incorrect!';
    if (name === 'newPassword' || name === 'confirmedPassword') {
        if (value.length < 6 || value.length > 12) return 'Password must be between 6 and 12 characters';
    }
    if (name === 'confirmedPassword' && value !== formData['newPassword']) return 'Passwords must match!';
};

const useForm = (defaultFormValues, callback) => {
    const [formData, setFormData] = useState(defaultFormValues);
    const [formValidations, setFormValidations] = useState({});
    const [submitStatus, setSubmitStatus] = useState();

    const handleChangeFn = e => {
        const { name, value } = e.target;

        // validate the input
        const validation = validateInput(name, value, formData);
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

        // validate all fields once more
        const validations = {};
        Object.entries(formData).forEach(([key, value]) => {
            const validation = validateInput(key, value, formData);
            validations[key] = validation || null;
        });
        setFormValidations({ ...validations });

        // lock form by disabling input fields and button
        setSubmitStatus(formSubmission.INPROGRESS);
    };

    const postFormData = useCallback(
        async authAction => {
            const formErrorMessage = await authAction(formData);
            if (formErrorMessage != null) {
                setFormValidations(formValidations => ({
                    ...formValidations,
                    form: formErrorMessage
                }));
            } else {
                setSubmitStatus(formSubmission.COMPLETE);
            }
        },
        [formData]
    );

    useEffect(() => {
        // check if there are any validations set when we are trying to submit the form data
        if (submitStatus === formSubmission.INPROGRESS &&
            Object.values(formValidations).every(validation => validation === null)) {
            postFormData(callback);
        } else if (submitStatus !== formSubmission.COMPLETE) {
            // reset/ unlock form if form submission is in progress and errors are found
            setSubmitStatus(null);
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