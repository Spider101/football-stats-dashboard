import { useCallback } from 'react';
import PropTypes from 'prop-types';

import TextField from '@material-ui/core/TextField';
import InputAdornment from '@material-ui/core/InputAdornment';

import Alert from '../components/Alert';
import DialogForm from '../components/DialogForm';
import PageAction from '../components/PageAction';

import { formSubmission } from '../utils';
import useForm from '../hooks/useForm';
import CustomSlider from '../components/CustomSlider';

export default function AddClub({ addClubAction }) {
    const {
        handleChangeFn,
        handleSubmitFn,
        formData: addNewClubData,
        formValidations: addNewClubValidations,
        submitStatus
    } = useForm(
        {
            name: '',
            managerFunds: '0',
            transferBudget: '0',
            wageBudget: '0',
            income: '0',
            expenditure: '0'
        },
        useCallback(newClubData => addClubAction(newClubData), [])
    );

    const addNewClubDialogForm = (
        <DialogForm dialogTitle='Add New Club' handleSubmit={handleSubmitFn} submitStatus={submitStatus}>
            <div style={{ width: '100%' }}>
                {submitStatus === formSubmission.COMPLETE && (
                    <Alert severity='success' text='New Club Added Successfully!' />
                )}
                {addNewClubValidations.form && <Alert severity='error' text={addNewClubValidations.form} />}
            </div>
            <TextField
                autoFocus
                name='name'
                label='Club Name'
                required
                id='name'
                margin='normal'
                fullWidth
                value={addNewClubData.name}
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={!!addNewClubValidations.name}
                helperText={addNewClubValidations.name}
            />
            <TextField
                name='managerFunds'
                label='Manager Funds'
                required
                id='managerFunds'
                type='number'
                margin='normal'
                fullWidth
                value={addNewClubData.managerFunds}
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={!!addNewClubValidations.managerFunds}
                helperText={addNewClubValidations.managerFunds}
                InputProps={{
                    startAdornment: <InputAdornment position='start'>$</InputAdornment>
                }}
            />
            <CustomSlider
                sliderTitle='Budget Split'
                splitMetadata={{
                    valueToSplit: Number(addNewClubData.managerFunds),
                    entitiesToSplit: [
                        { name: 'transferBudget', handleChange: handleChangeFn },
                        { name: 'wageBudget', handleChange: handleChangeFn },
                    ]
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
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={!!addNewClubValidations.income}
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
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={!!addNewClubValidations.expenditure}
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