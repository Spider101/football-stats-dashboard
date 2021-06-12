import React from 'react';
import PropTypes from 'prop-types';

import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

import Alert from './Alert';

const useStyles = makeStyles((theme) => ({
    paper: {
        display: 'flex',
        marginTop: theme.spacing(8),
        flexDirection: 'column',
        alignItems: 'center'
    },
    avatar: {
        margin: theme.spacing(1),
        backgroundColor: theme.palette.secondary.main
    },
    form: {
        width: '100%',
        marginTop: theme.spacing(1)
    },
    submit: {
        margin: theme.spacing(3, 0, 2)
    },
    validations: {
        width: '100%'
    }
}));

export default function SignIn({ values, handleChange, handleSubmit, validations, submitStatus }) {
    const classes = useStyles();
    return (
        <div className={ classes.paper }>
            <Avatar  className={ classes.avatar }>
                <LockOutlinedIcon />
            </Avatar>
            <Typography component='h1' variant='h5'>
                Sign In
            </Typography>
            <div className={ classes.validations }>
                { submitStatus === 'SUBMITTED' && <Alert severity='success' text='Signed In Successfully!' /> }
                { validations.form != null && <Alert severity='error' text={validations.form} /> }
            </div>
            <form className={ classes.form } onSubmit={ handleSubmit } noValidate>
                <TextField
                    name='email'
                    required
                    id='email'
                    label='Email Address'
                    variant='outlined'
                    margin='normal'
                    type='email'
                    fullWidth
                    autoFocus
                    value={ values.email }
                    disabled={ submitStatus === 'SUBMITTING' }
                    onChange={ e => handleChange(e) }
                    error={ validations.email != null }
                    helperText={ validations.email }
                />
                <TextField
                    name='password'
                    required
                    id='password'
                    label='Password'
                    variant='outlined'
                    margin='normal'
                    type='password'
                    fullWidth
                    autoComplete='current-password'
                    value={ values.password }
                    disabled={ submitStatus === 'SUBMITTING' }
                    onChange={ e => handleChange(e) }
                    error={ validations.password != null }
                    helperText={ validations.password }
                />
                <FormControlLabel
                    control={<Checkbox value="remember" color="primary" disabled={ submitStatus === 'SUBMITTING' }/>}
                    label="Remember me"
                />
                <Button
                    className={ classes.submit }
                    type='submit'
                    variant='contained'
                    color='primary'
                    fullWidth
                    disabled={ submitStatus === 'SUBMITTING' }
                >
                    { submitStatus === 'SUBMITTING' ? 'Signing In ...' : 'Sign In' }
                </Button>
                <Grid container>
                    <Grid item xs>
                        <Link href='/forgotPassword' variant='body2'>
                            Forgot password?
                        </Link>
                    </Grid>
                    <Grid item>
                        <Link to='/signUp' variant="body2">
                            Don&apos;t have an account? Sign Up
                        </Link>
                    </Grid>
                </Grid>
            </form>
        </div>
    );
}

SignIn.propTypes = {
    values: PropTypes.shape({
        email: PropTypes.string,
        password: PropTypes.string
    }),
    handleChange: PropTypes.func,
    handleSubmit: PropTypes.func,
    validations: PropTypes.shape({
        email: PropTypes.string,
        password: PropTypes.string,
        form: PropTypes.string
    }),
    submitStatus: PropTypes.string
};
