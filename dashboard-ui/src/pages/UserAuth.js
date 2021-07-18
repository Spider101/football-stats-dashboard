import React from 'react';
import { Route, Switch } from 'react-router-dom';
import PropTypes from 'prop-types';

import SignIn from '../components/SignIn';
import SignUp from '../components/Signup';

import useForm from '../hooks/useForm';
import { useUserAuth } from '../context/authProvider';

export default function UserAuth({ classes }) {
    return (
        <div className={classes.content}>
            <div className={classes.formContainer}>
                <Switch>
                    <Route exact path='/'>
                        <SignInContainer />
                    </Route>
                    <Route path='/signUp'>
                        <SignUpContainer />
                    </Route>
                </Switch>
            </div>
        </div>
    );
}

UserAuth.propTypes = {
    classes: PropTypes.object
};

const SignInContainer = () => {
    const { setAuthToken, login } = useUserAuth();
    const {
        handleChangeFn,
        handleSubmitFn,
        formData: signInFormData,
        formValidations: signInFormValidations,
        submitStatus
    } = useForm(
        {
            email: '',
            password: ''
        },
        React.useCallback(authData => login(authData, setAuthToken), [login, setAuthToken])
    );

    return (
        <SignIn
            values={signInFormData}
            validations={signInFormValidations}
            handleSubmit={handleSubmitFn}
            handleChange={handleChangeFn}
            submitStatus={submitStatus}
        />
    );
};

const SignUpContainer = () => {
    const { setAuthToken, createAccount } = useUserAuth();
    const {
        handleChangeFn,
        handleSubmitFn,
        formData: signUpFormData,
        formValidations: signUpFormValidations,
        submitStatus
    } = useForm(
        {
            firstName: '',
            lastName: '',
            email: '',
            newPassword: '',
            confirmedPassword: ''
        },
        React.useCallback(userCreds => createAccount(userCreds, setAuthToken), [createAccount, setAuthToken])
    );
    return (
        <SignUp
            values={signUpFormData}
            validations={signUpFormValidations}
            handleSubmit={handleSubmitFn}
            handleChange={handleChangeFn}
            submitStatus={submitStatus}
        />
    );
};
