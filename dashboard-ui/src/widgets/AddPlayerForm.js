import PropTypes from 'prop-types';

import TextField from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import { capitalizeLabel, nationalityFlagMap } from '../utils';

export const getStepper = activeStep => {
    const steps = [
        'Add Player Metadata',
        'Add Technical Attributes',
        'Add Physical Attributes',
        'Add Mental Attributes'
    ];
    const stepper = (
        <Stepper activeStep={activeStep} alternativeLabel>
            {steps.map(label => (
                <Step key={label}>
                    <StepLabel>{label}</StepLabel>
                </Step>
            ))}
        </Stepper>
    );
    return { stepper, numSteps: steps.length };
};

export const getAddPlayerFormSchema = () => ({
    metadata: { name: '', age: 0, country: '' },
    technicalAttributes: {
        freekickAccuracy: 0,
        penalties: 0,
        headingAccuracy: 0,
        crossing: 0,
        shortPassing: 0,
        longPassing: 0,
        longShots: 0,
        finishing: 0,
        volleys: 0,
        ballControl: 0,
        standingTackle: 0,
        slidingTackle: 0,
        dribbling: 0,
        curve: 0
    },
    physicalAttributes: {
        stamina: 0,
        jumping: 0,
        strength: 0,
        sprintSpeed: 0,
        acceleration: 0,
        agility: 0,
        balance: 0
    },
    mentalAttributes: {
        aggression: 0,
        vision: 0,
        composure: 0,
        defensiveAwareness: 0,
        attackingPosition: 0
    }
});

export default function AddPlayerForm({ getFormMetadataAtStep, stepIdx }) {
    const { formData, formValidations, handleChangeFn } = getFormMetadataAtStep(stepIdx);
    if (stepIdx === 0) {
        return (
            <PlayerMetadataForm
                newPlayerMetadata={formData}
                newPlayerMetadataValidations={formValidations}
                handleChangeFn={handleChangeFn}
            />
        );
    }

    return (
        <PlayerAttributeForm
            newPlayerAttributeData={formData}
            newPlayerAttributeValidations={formValidations}
            handleChangeFn={handleChangeFn}
        />
    );
}
AddPlayerForm.propTypes = {
    getFormMetadataAtStep: PropTypes.func,
    stepIdx: PropTypes.number
};

const PlayerMetadataForm = ({ newPlayerMetadata, newPlayerMetadataValidations, handleChangeFn }) => {
    const countries = nationalityFlagMap.map(entity => ({ value: entity.nationality, label: entity.nationality }));
    return (
        <>
            <TextField
                autoFocus
                name='name'
                label='Player Name'
                required
                id='name'
                margin='normal'
                fullWidth
                value={newPlayerMetadata.name}
                onChange={e => handleChangeFn(e)}
                error={newPlayerMetadataValidations.name}
                helperText={newPlayerMetadataValidations.name}
            />
            <TextField
                name='age'
                label='Age'
                required
                id='age'
                type='number'
                margin='normal'
                fullWidth
                value={newPlayerMetadata.age}
                onChange={e => handleChangeFn(e)}
                error={newPlayerMetadataValidations.age}
                helperText={newPlayerMetadataValidations.age}
            />
            <TextField
                name='country'
                label='Select Nationality'
                required
                id='country'
                margin='normal'
                fullWidth
                value={newPlayerMetadata.country}
                onChange={e => handleChangeFn(e)}
                error={newPlayerMetadataValidations.country}
                helperText={newPlayerMetadataValidations.country}
                select
            >
                {countries.map(country => (
                    <MenuItem key={country.value} value={country.value}>
                        {country.label}
                    </MenuItem>
                ))}
            </TextField>
        </>
    );
};
PlayerMetadataForm.propTypes = {
    newPlayerMetadata: PropTypes.shape({
        name: PropTypes.string,
        age: PropTypes.string,
        country: PropTypes.string
    }),
    newPlayerMetadataValidations: PropTypes.shape({
        name: PropTypes.string,
        age: PropTypes.string,
        country: PropTypes.string,
    }),
    handleChangeFn: PropTypes.func
};

const PlayerAttributeForm = ({ newPlayerAttributeData, newPlayerAttributeValidations, handleChangeFn }) => {
    return Object.entries(newPlayerAttributeData).map(([attrDataName, attrDataValue], index)  => {
        return <TextField
            autoFocus={index === 0}
            key={attrDataName}
            name={attrDataName}
            label={capitalizeLabel(attrDataName)}
            required
            id={attrDataName}
            type='number'
            margin='normal'
            fullWidth
            value={attrDataValue}
            onChange={e => handleChangeFn(e)}
            error={newPlayerAttributeValidations[attrDataName]}
            helperText={newPlayerAttributeValidations[attrDataName]}
        />;
    });
};
PlayerAttributeForm.propTypes = {
    handleChangeFn: PropTypes.func,
    newPlayerAttributeData: PropTypes.object,
    newPlayerAttributeValidations: PropTypes.object
};