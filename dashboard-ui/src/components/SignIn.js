import PropTypes from 'prop-types';
import { Link as RouterLink } from 'react-router-dom';

import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
// import FormControlLabel from '@material-ui/core/FormControlLabel';
// import Checkbox from '@material-ui/core/Checkbox';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

import Alert from './Alert';
import { formSubmission } from '../utils';

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

export default function SignIn({ values, handleChange, handleSubmit, validations, submitStatus }) {
    const classes = useStyles();
    return (
        <div className={classes.paper}>
            <Avatar className={classes.avatar}>
                <LockOutlinedIcon />
            </Avatar>
            <Typography component='h1' variant='h5'>
                Sign In
            </Typography>
            <div className={classes.validations}>
                {submitStatus === formSubmission.COMPLETE && (
                    <Alert severity='success' text='Signed In Successfully!' />
                )}
                {validations.form && <Alert severity='error' text={validations.form} />}
            </div>
            <form className={classes.form} onSubmit={handleSubmit} noValidate>
                <TextField
                    name='email'
                    required
                    id='email'
                    label='Email Address'
                    variant='outlined'
                    margin='normal'
                    type='email'
                    autoComplete='email'
                    fullWidth
                    autoFocus
                    value={values.email}
                    disabled={submitStatus === formSubmission.INPROGRESS}
                    onChange={e => handleChange(e)}
                    error={validations.email}
                    helperText={validations.email}
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
                    value={values.password}
                    disabled={submitStatus === formSubmission.INPROGRESS}
                    onChange={e => handleChange(e)}
                    error={validations.password}
                    helperText={validations.password}
                />
                {/* // TODO: uncomment when Remember Me functionality is ready */}
                {/* <FormControlLabel
                    control={
                        <Checkbox
                            value='remember'
                            color='primary'
                            disabled={submitStatus === formSubmission.INPROGRESS}
                        />
                    }
                    label='Remember me'
                /> */}
                <Button
                    className={classes.submit}
                    type='submit'
                    variant='contained'
                    color='primary'
                    fullWidth
                    disabled={submitStatus !== formSubmission.READY}
                >
                    {submitStatus === formSubmission.INPROGRESS ? 'Signing In ...' : 'Sign In'}
                </Button>
                <Grid container>
                    {/* // TODO: uncomment when forgot password functionality is ready */}
                    {/* <Grid item xs>
                        <Link href='/forgotPassword' variant='body2'>
                            Forgot password?
                        </Link>
                    </Grid> */}
                    <Grid item>
                        <Link to='/signUp' component={RouterLink} variant='body2'>
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
