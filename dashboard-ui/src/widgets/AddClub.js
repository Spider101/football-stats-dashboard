import React from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import InputAdornment from '@material-ui/core/InputAdornment';

import Alert from '../components/Alert';
import DialogForm from '../components/DialogForm';
import PageAction from '../components/PageAction';

import { formSubmission } from '../utils';
import useForm from '../hooks/useForm';

const useStyles = makeStyles(theme => ({
    submit: {
        margin: theme.spacing(3, 0, 2)
    },
    validations: {
        width: '100%'
    }
}));

export default function AddClub({ addClubAction }) {
    const classes = useStyles();
    const {
        handleChangeFn,
        handleSubmitFn,
        formData: addNewClubData,
        formValidations: addNewClubValidations,
        submitStatus
    } = useForm(
        {
            name: '',
            transferBudget: 0,
            wageBudget: 0,
            income: 0,
            expenditure: 0
        },
        addClubAction
    );

    const addNewClubDialogForm = (
        <DialogForm dialogTitle='Add New Club' handleSubmit={handleSubmitFn}>
            <div className={classes.validations}>
                {submitStatus === formSubmission.COMPLETE && (
                    <Alert severity='success' text='New Club Added Successfully!' />
                )}
                {addNewClubValidations.form && <Alert severity='error' text={addNewClubValidations.form} />}
            </div>
            <TextField
                name='name'
                label='Club Name'
                required
                id='name'
                margin='normal'
                fullWidth
                value={addNewClubData.name}
                disabled={submitStatus === formSubmission.COMPLETE}
                onChange={e => handleChangeFn(e)}
                error={addNewClubValidations.name}
                helperText={addNewClubValidations.name}
            />
            <TextField
                name='transferBudget'
                label='Transfer Budget'
                required
                id='transferBudget'
                type='number'
                margin='normal'
                fullWidth
                value={addNewClubData.transferBudget}
                disabled={submitStatus === formSubmission.COMPLETE}
                onChange={e => handleChangeFn(e)}
                error={addNewClubValidations.transferBudget}
                helperText={addNewClubValidations.transferBudget}
                InputProps={{
                    startAdornment: <InputAdornment position='start'>$</InputAdornment>
                }}
            />
            <TextField
                name='wageBudget'
                label='Wage Budget'
                required
                id='wageBudget'
                type='number'
                margin='normal'
                fullWidth
                value={addNewClubData.wageBudget}
                disabled={submitStatus === formSubmission.COMPLETE}
                onChange={e => handleChangeFn(e)}
                error={addNewClubValidations.wageBudget}
                helperText={addNewClubValidations.wageBudget}
                InputProps={{
                    startAdornment: <InputAdornment position='start'>$</InputAdornment>
                }}
            />
            <TextField
                name='income'
                label='Income'
                required
                id='income'
                type='number'
                margin='normal'
                fullWidth
                value={addNewClubData.income}
                disabled={submitStatus === formSubmission.COMPLETE}
                onChange={e => handleChangeFn(e)}
                error={addNewClubValidations.income}
                helperText={addNewClubValidations.income}
                InputProps={{
                    startAdornment: <InputAdornment position='start'>$</InputAdornment>
                }}
            />
            <TextField
                name='expenditure'
                label='Expenditure'
                required
                id='expenditure'
                type='number'
                margin='normal'
                fullWidth
                value={addNewClubData.expenditure}
                disabled={submitStatus === formSubmission.COMPLETE}
                onChange={e => handleChangeFn(e)}
                error={addNewClubValidations.expenditure}
                helperText={addNewClubValidations.expenditure}
                InputProps={{
                    startAdornment: <InputAdornment position='start'>$</InputAdornment>
                }}
            />
        </DialogForm>
    );

    return <PageAction actionType='add' dialog={addNewClubDialogForm} />;
}

AddClub.propTypes = {
    addClubAction: PropTypes.func
};