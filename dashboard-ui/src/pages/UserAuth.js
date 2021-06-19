import React from 'react';
import { Route, Switch } from 'react-router-dom';
import PropTypes from 'prop-types';

import SignIn from '../components/SignIn';
import SignUp from '../components/Signup';

import useForm from '../hooks/useForm';

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
    const {
        signInFormData,
        submitStatus,
        signInFormValidations,
        signInFormChangeHandler: handleChangeFn,
        signInFormSubmitHandler: handleSubmitFn
    } = useForm();

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
    const {
        submitStatus,
        signUpFormData,
        signUpFormValidations,
        signUpFormChangeHandler: handleChangeFn,
        signUpFormSubmitHandler: handleSubmitFn
    } = useForm();
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
