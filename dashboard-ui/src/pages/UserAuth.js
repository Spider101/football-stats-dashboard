import { useCallback } from 'react';
import { Redirect, Route, Switch, useRouteMatch } from 'react-router-dom';
import PropTypes from 'prop-types';

import SignIn from '../components/SignIn';
import SignUp from '../components/Signup';

import useForm from '../hooks/useForm';
import { useUserAuth } from '../context/authProvider';

export default function UserAuth({ classes }) {
    const { path } = useRouteMatch();
    return (
        <div className={classes.content}>
            <div className={classes.formContainer}>
                <Switch>
                    <Route path={`${path}/signIn`}>
                        <SignInContainer />
                    </Route>
                    <Route path={`${path}/signUp`}>
                        <SignUpContainer />
                    </Route>
                    <Redirect to='/' />
                </Switch>
            </div>
        </div>
    );
}

UserAuth.propTypes = {
    classes: PropTypes.object
};

const SignInContainer = () => {
    const { setAuthData, login } = useUserAuth();
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
        useCallback(authData => login(authData, setAuthData), [login, setAuthData])
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
    const { setAuthData, createAccount } = useUserAuth();
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
        useCallback(userCreds => createAccount(userCreds, setAuthData), [createAccount, setAuthData])
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
