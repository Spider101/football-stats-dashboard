import React from 'react';

import { capitalizeLabel, convertCamelCaseToSnakeCase, formSubmission } from '../utils';

const validateEmail = email => /\S+@\S+\.\S+/.test(email);

const validateFormData = formData => {
    const formValidations = Object.entries(formData).reduce((validations, [key, value]) => {
        if (isNaN(value) && !value.trim()) {
            validations[key] = `${capitalizeLabel(convertCamelCaseToSnakeCase(key))} cannot be empty!`;
        } else if (key === 'email' && !validateEmail(value)) {
            validations[key] = 'Email is in incorrect format!';
        } else if (key === 'newPassword' || key === 'confirmedPassword') {
            if (value.length < 6 || value.length > 12) {
                validations[key] = 'Password must be between 6 and 12 characters!';
            }
        }
        return validations;
    }, {});

    return formData['newPassword'] !== formData['confirmedPassword']
        ? Object.assign({}, formValidations, { confirmedPassword: 'Passwords must match!' })
        : formValidations;
};

const useForm = (defaultFormValues, callback) => {
    const [formData, setFormData] = React.useState(defaultFormValues);
    const [formValidations, setFormValidations] = React.useState({});
    const [submitStatus, setSubmitStatus] = React.useState();

    const handleChangeFn = e => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmitFn = e => {
        e.preventDefault();

        setFormValidations(validateFormData(formData));

        // lock form by disabling input fields and button
        setSubmitStatus(formSubmission.INPROGRESS);
    };

    const postFormData = React.useCallback(
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

    React.useEffect(() => {
        // check if there are any validations set when we are trying to submit the form data
        if (submitStatus === formSubmission.INPROGRESS && Object.values(formValidations).length === 0) {
            postFormData(callback);
        } else if (submitStatus !== formSubmission.COMPLETE) {
            // reset/ unlock form if form submission is in progress and errors are found
            setSubmitStatus(null);
        }
    }, [formValidations, submitStatus, callback]);

    return {
        handleChangeFn,
        handleSubmitFn,
        formData,
        formValidations,
        submitStatus
    };
};

export default useForm;