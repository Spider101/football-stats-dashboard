import PropTypes from 'prop-types';
import { Link as RouterLink } from 'react-router-dom';

import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

import Alert from './Alert';
import { formSubmission } from '../constants';

const useStyles = makeStyles(theme => ({
    paper: {
        display: 'flex',
        marginTop: theme.spacing(8),
        flexDirection: 'column',
        alignItems: 'center',
        width: '50%',
        borderColor: theme.palette.divider,
        borderStyle: 'solid',
        borderWidth: '1px',
        borderRadius: '5px',
        padding: theme.spacing(3, 2)
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

export default function Signup({ values, handleChange, handleSubmit, validations, submitStatus }) {
    const classes = useStyles();
    return (
        <div className={classes.paper}>
            <Avatar className={classes.avatar}>
                <LockOutlinedIcon />
            </Avatar>
            <Typography component='h1' variant='h5'>
                Sign Up
            </Typography>
            <div className={classes.validations}>
                {submitStatus === formSubmission.COMPLETE && (
                    <Alert severity='success' text='Account created successfully!' />
                )}
                {validations.form != null && <Alert severity='error' text={validations.form} />}
            </div>
            <form className={classes.form} onSubmit={handleSubmit} noValidate>
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                        <TextField
                            name='firstName'
                            required
                            id='firstName'
                            label='First Name'
                            variant='outlined'
                            style={{ marginTop: '8px' }}
                            autoFocus
                            fullWidth
                            value={values.firstName}
                            disabled={submitStatus === formSubmission.INPROGRESS}
                            onChange={e => handleChange(e)}
                            error={!!validations.firstName}
                            helperText={validations.firstName}
                        />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <TextField
                            name='lastName'
                            required
                            id='lastName'
                            label='Last Name'
                            variant='outlined'
                            style={{ marginTop: '8px' }}
                            fullWidth
                            value={values.lastName}
                            disabled={submitStatus === formSubmission.INPROGRESS}
                            onChange={e => handleChange(e)}
                            error={!!validations.lastName}
                            helperText={validations.lastName}
                        />
                    </Grid>
                    <Grid item xs={12} sm={12}>
                        <TextField
                            name='email'
                            required
                            id='email'
                            label='Email Address'
                            variant='outlined'
                            type='email'
                            fullWidth
                            value={values.email}
                            autoComplete='email'
                            disabled={submitStatus === formSubmission.INPROGRESS}
                            onChange={e => handleChange(e)}
                            error={!!validations.email}
                            helperText={validations.email}
                        />
                    </Grid>
                    <Grid item xs={12} sm={12}>
                        <TextField
                            name='newPassword'
                            required
                            id='newPassword'
                            label='New Password'
                            variant='outlined'
                            type='password'
                            fullWidth
                            value={values.newPassword}
                            autoComplete='new-password'
                            disabled={submitStatus === formSubmission.INPROGRESS}
                            onChange={e => handleChange(e)}
                            error={!!validations.newPassword}
                            helperText={validations.newPassword}
                        />
                    </Grid>
                    <Grid item xs={12} sm={12}>
                        <TextField
                            name='confirmedPassword'
                            required
                            id='confirmedPassword'
                            label='Confirm Password'
                            variant='outlined'
                            type='password'
                            fullWidth
                            value={values.confirmedPassword}
                            autoComplete='new-password'
                            disabled={submitStatus === formSubmission.INPROGRESS}
                            onChange={e => handleChange(e)}
                            error={!!validations.confirmedPassword}
                            helperText={validations.confirmedPassword}
                        />
                    </Grid>
                </Grid>
                <Button
                    className={classes.submit}
                    type='submit'
                    variant='contained'
                    color='primary'
                    fullWidth
                    disabled={submitStatus !== formSubmission.READY}
                >
                    {submitStatus === formSubmission.INPROGRESS ? 'Signing Up ...' : 'Sign Up'}
                </Button>
                <Grid container justifyContent='flex-end'>
                    <Grid item>
                        <Link to='/auth/signIn' component={RouterLink} variant='body2'>
                            Already have an account? Sign In
                        </Link>
                    </Grid>
                </Grid>
            </form>
        </div>
    );
}

Signup.propTypes = {
    values: PropTypes.shape({
        email: PropTypes.string,
        firstName: PropTypes.string,
        lastName: PropTypes.string,
        newPassword: PropTypes.string,
        confirmedPassword: PropTypes.string
    }),
    handleChange: PropTypes.func,
    handleSubmit: PropTypes.func,
    validations: PropTypes.shape({
        email: PropTypes.string,
        firstName: PropTypes.string,
        lastName: PropTypes.string,
        newPassword: PropTypes.string,
        confirmedPassword: PropTypes.string,
        form: PropTypes.string
    }),
    submitStatus: PropTypes.string
};
