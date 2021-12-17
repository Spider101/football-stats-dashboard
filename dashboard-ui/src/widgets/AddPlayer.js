import PropTypes from 'prop-types';
import { useCallback } from 'react';

import TextField from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';

import Alert from '../components/Alert';
import DialogForm from '../components/DialogForm';
import PageAction from '../components/PageAction';

import useForm from '../hooks/useForm';
import { formSubmission, nationalityFlagMap } from '../utils';

// TODO: build this from the server lookup for roles and countries instead of hard-coding it here
const roles = [
    { value: 'defensiveCentralMidfielder', label: 'Defensive Central Midfielder' },
    { value: 'falseNine', label: 'False Nine' },
    { value: 'sweeperKeeper', label: 'Sweeper Keeper' },
    { value: 'regista', label: 'Regista' },
    { value: 'insideForward', label: 'Inside Forward' }
];
const countries = nationalityFlagMap.map(entity => ({ value: entity.nationality, label: entity.nationality }));

export default function AddPlayer({ addPlayerAction }) {
    const {
        handleChangeFn,
        handleSubmitFn,
        formData: addNewPlayerData,
        formValidations: addNewPlayerValidations,
        submitStatus
    } = useForm(
        {
            name: '',
            currentAbility: 0,
            country: '',
            role: ''
        },
        useCallback(newPlayerData => addPlayerAction(newPlayerData), [])
    );
    const addNewPlayerDialogForm = (
        <DialogForm dialogTitle='Add New Player' handleSubmit={handleSubmitFn}>
            <div style={{ width: '100%' }}>
                {submitStatus === formSubmission.COMPLETE && (
                    <Alert severity='success' text='New Player Added Successfully!' />
                )}
                {addNewPlayerValidations.form && <Alert severity='error' text={addNewPlayerValidations.form} />}
            </div>
            <TextField
                autoFocus
                name='name'
                label='Player Name'
                required
                id='name'
                margin='normal'
                fullWidth
                value={addNewPlayerData.name}
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={addNewPlayerValidations.name}
                helperText={addNewPlayerValidations.name}
            />
            <TextField
                name='currentAbility'
                label='Ability'
                required
                id='currentAbility'
                type='number'
                margin='normal'
                fullWidth
                value={addNewPlayerData.currentAbility}
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={addNewPlayerValidations.currentAbility}
                helperText={addNewPlayerValidations.currentAbility}
            />
            <TextField
                name='role'
                label='Select Role'
                required
                id='role'
                margin='normal'
                fullWidth
                value={addNewPlayerData.role}
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={addNewPlayerValidations.role}
                helperText={addNewPlayerValidations.role}
                select
            >
                {roles.map(role => (
                    <MenuItem key={role.value} value={role.value}>
                        {role.label}
                    </MenuItem>
                ))}
            </TextField>
            <TextField
                name='country'
                label='Select Nationality'
                required
                id='country'
                margin='normal'
                fullWidth
                value={addNewPlayerData.country}
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                error={addNewPlayerValidations.country}
                helperText={addNewPlayerValidations.country}
                select
            >
                {countries.map(country => (
                    <MenuItem key={country.value} value={country.value}>
                        {country.label}
                    </MenuItem>
                ))}
            </TextField>
        </DialogForm>
    );

    return <PageAction actionType='add' dialog={addNewPlayerDialogForm} />;
}

AddPlayer.propTypes = {
    addPlayerAction: PropTypes.func
};