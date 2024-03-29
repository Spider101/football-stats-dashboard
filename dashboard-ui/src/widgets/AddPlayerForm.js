import PropTypes from 'prop-types';

import TextField from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Chip from '@material-ui/core/Chip';
import Select from '@material-ui/core/Select';
import Input from '@material-ui/core/Input';
import Checkbox from '@material-ui/core/Checkbox';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import { makeStyles } from '@material-ui/core/styles';

import { capitalizeLabel } from '../utils';
import { caseFormat, PLAYER_ATTRIBUTE_METADATA, PLAYER_NATIONS, PLAYER_ROLE_NAMES } from '../constants';
import { useLookupData } from '../context/LookupDataProvider';

export const getStepper = activeStep => {
    const steps = [
        'Add Player Metadata',
        'Add Player Role',
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
    metadata: { name: '', age: '', country: '' },
    role: { name: '', associatedAttributes: [] },
    technicalAttributes: Object.fromEntries(
        PLAYER_ATTRIBUTE_METADATA
            .filter(attributeMetadata => attributeMetadata.category === 'technical')
            .map(attributeMetadata => [attributeMetadata.name, attributeMetadata.defaultValue])
    ),
    physicalAttributes: Object.fromEntries(
        PLAYER_ATTRIBUTE_METADATA
            .filter(attributeMetadata => attributeMetadata.category === 'physical')
            .map(attributeMetadata => [attributeMetadata.name, attributeMetadata.defaultValue])
    ),
    mentalAttributes: Object.fromEntries(
        PLAYER_ATTRIBUTE_METADATA
            .filter(attributeMetadata => attributeMetadata.category === 'mental')
            .map(attributeMetadata => [attributeMetadata.name, attributeMetadata.defaultValue])
    )
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
    } else if (stepIdx === 1) {
        return (
            <PlayerRoleForm
                newPlayerRoleData={formData}
                newPlayerRoleValidations={formValidations}
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
    const { countryFlagMetadata } = useLookupData();
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
                error={!!newPlayerMetadataValidations.name}
                helperText={newPlayerMetadataValidations.name}
            />
            <TextField
                placeholder='Please enter age between 15 and 50'
                name='age'
                label='Age'
                required
                id='age'
                type='number'
                margin='normal'
                inputProps={{ min: 15, max: 50 }}
                fullWidth
                value={newPlayerMetadata.age}
                onChange={e => handleChangeFn(e)}
                error={!!newPlayerMetadataValidations.age}
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
                error={!!newPlayerMetadataValidations.country}
                helperText={newPlayerMetadataValidations.country}
                select
            >
                {countryFlagMetadata
                    .filter(flagMetadata => PLAYER_NATIONS.includes(flagMetadata.countryName))
                    .map(flagMetadata => (
                        <MenuItem key={flagMetadata.id} value={flagMetadata.countryName}>
                            {/* hide the flag when a country is selected */}
                            {newPlayerMetadata.country !== flagMetadata.countryName && (
                                <ListItemIcon>
                                    <img src={flagMetadata.countryFlagUrl} alt={flagMetadata.countryCode} />
                                </ListItemIcon>
                            )}
                            <ListItemText primary={flagMetadata.countryName} />
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
        country: PropTypes.string
    }),
    handleChangeFn: PropTypes.func
};

const PlayerAttributeForm = ({ newPlayerAttributeData, newPlayerAttributeValidations, handleChangeFn }) => {
    return Object.entries(newPlayerAttributeData).map(([attrDataName, attrDataValue], index) => {
        return (
            <TextField
                autoFocus={index === 0}
                key={attrDataName}
                name={attrDataName}
                label={capitalizeLabel(attrDataName, caseFormat.CAMEL_CASE)}
                required
                id={attrDataName}
                type='number'
                margin='normal'
                fullWidth
                value={attrDataValue}
                onChange={e => handleChangeFn(e)}
                error={!!newPlayerAttributeValidations[attrDataName]}
                helperText={newPlayerAttributeValidations[attrDataName]}
            />
        );
    });
};
PlayerAttributeForm.propTypes = {
    handleChangeFn: PropTypes.func,
    newPlayerAttributeData: PropTypes.object,
    newPlayerAttributeValidations: PropTypes.object
};

const useStyles = makeStyles({
    formControl: {
        width: '100%'
    },
    chips: {
        display: 'flex',
        flexWrap: 'wrap'
    },
    chip: {
        margin: 2
    }
});
const PlayerRoleForm = ({ newPlayerRoleData, newPlayerRoleValidations, handleChangeFn }) => {
    const shouldBeChecked = attributeName => newPlayerRoleData.associatedAttributes.indexOf(attributeName) > -1;

    // TODO: move the multi-select specific code to a component for reuse
    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
    const MenuProps = {
        PaperProps: {
            style: {
                maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
                width: 250
            }
        }
    };
    const classes = useStyles();

    const renderChipsFn = selected => (
        <div className={classes.chips}>
            {selected.map(value => (
                <Chip key={value} label={value} className={classes.chip} />
            ))}
        </div>
    );

    return (
        <>
            <TextField
                name='name'
                label='Select Role'
                required
                id='name'
                margin='normal'
                fullWidth
                value={newPlayerRoleData.name}
                onChange={e => handleChangeFn(e)}
                error={!!newPlayerRoleValidations.name}
                helperText={newPlayerRoleValidations.name}
                select
            >
                {PLAYER_ROLE_NAMES.map(roleName => (
                    <MenuItem key={roleName} value={roleName}>
                        {capitalizeLabel(roleName, caseFormat.CAMEL_CASE)}
                    </MenuItem>
                ))}
            </TextField>
            <FormControl className={classes.formControl}>
                <InputLabel required id='associated-attributes-label'>
                    Associated Attributes
                </InputLabel>
                <Select
                    name='associatedAttributes'
                    label='associated-attributes-label'
                    id='associated-attributes'
                    multiple
                    value={newPlayerRoleData.associatedAttributes}
                    onChange={handleChangeFn}
                    input={<Input />}
                    MenuProps={MenuProps}
                    renderValue={renderChipsFn}
                >
                    {PLAYER_ATTRIBUTE_METADATA
                        .map(attributeMetadata => attributeMetadata.name)
                        .map(attributeName => (
                            <MenuItem key={attributeName} value={attributeName}>
                                <Checkbox checked={shouldBeChecked(attributeName)} />
                                <ListItemText primary={capitalizeLabel(attributeName, caseFormat.CAMEL_CASE)} />
                            </MenuItem>
                        ))}
                </Select>
            </FormControl>
        </>
    );
};
PlayerRoleForm.propTypes = {
    newPlayerRoleData: PropTypes.shape({
        name: PropTypes.string,
        associatedAttributes: PropTypes.arrayOf(PropTypes.string)
    }),
    newPlayerRoleValidations: PropTypes.shape({
        name: PropTypes.string,
        associatedAttributes: PropTypes.string
    }),
    handleChangeFn: PropTypes.func
};
