import React from 'react';

import { capitalizeLabel } from '../utils';
import { useUserAuth } from '../context/authProvider';

const validateEmail = email => /\S+@\S+\.\S+/.test(email);

const validateFormData = formData => {
    let validations = {};

    let newPassword;
    Object.entries(formData).forEach(([key, value]) => {
        let errorMessage = null;

        if (!value.trim()) {
            errorMessage = `${capitalizeLabel(key)} cannot be empty!`;
        } else if (key === 'email' && !validateEmail(value)) {
            errorMessage = 'Email is in incorrect format!';
        } else if (key === 'newPassword') {
            newPassword = value;
            if (value.length < 6 || value.length > 12) {
                errorMessage = 'Password must be between 6 and 12 characters!';
            }
        } else if (key === 'confirmedPassword') {
            if (value.length < 6 || value.length > 12) {
                errorMessage = 'Password must be between 6 and 12 characters!';
            } else if (value !== newPassword) {
                errorMessage = 'Passwords must match!';
            }
        }

        validations = {
            ...validations,
            [key]: errorMessage
        };
    });

    return {
        ...validations,
        form: null
    };
};

const useForm = () => {
    const { setAuthToken, login, createAccount } = useUserAuth();
    const [submitStatus, setSubmitStatus] = React.useState();

    // sign in form data
    const [signInFormData, setSignInFormData] = React.useState({
        email: '',
        password: ''
    });

    const [signInFormValidations, setSignInFormValidations] = React.useState({
        email: null,
        password: null,
        form: null
    });

    // sign up form data
    const [signUpFormData, setSignUpFormData] = React.useState({
        firstName: '',
        lastName: '',
        email: '',
        newPassword: '',
        confirmedPassword: ''
    });

    const [signUpFormValidations, setSignUpFormValidations] = React.useState({
        firstName: null,
        lastName: null,
        email: null,
        newPassword: null,
        confirmedPassword: null,
        form: null
    });

    // common logic
    const handleSubmitFn = (e, formData, setFormValidations) => {
        e.preventDefault();

        setFormValidations(validateFormData(formData));

        // lock form by disabling input fields and button
        setSubmitStatus('SUBMITTING');
    };

    const handleChangeFn = (e, formData, setFormData) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const submitFormLogic = (formValidations, authAction) => {
        if (submitStatus === 'SUBMITTING' && Object.values(formValidations).every(validation => validation == null)) {
            authAction();
            setSubmitStatus('SUBMITTED');
        } else {
            // reset/unlock form
            setSubmitStatus(null);
        }
    };

    React.useEffect(() => {
        submitFormLogic(signInFormValidations, () => login(signInFormData, setAuthToken));
    }, [signInFormValidations]);

    React.useEffect(() => {
        submitFormLogic(signUpFormValidations);
    }, [signUpFormValidations]);

    return {
        submitStatus,
        signInFormData,
        signInFormValidations,
        signInFormChangeHandler: e => handleChangeFn(e, signInFormData, setSignInFormData),
        signInFormSubmitHandler: e => handleSubmitFn(e, signInFormData, setSignInFormValidations),
        signUpFormData,
        signUpFormValidations,
        signUpFormChangeHandler: e => handleChangeFn(e, signUpFormData, setSignUpFormData),
        signUpFormSubmitHandler: e => handleSubmitFn(e, signUpFormData, setSignUpFormValidations)
    };
};

export default useForm;