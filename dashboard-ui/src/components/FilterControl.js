import React from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/core/styles';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import FormHelperText from '@material-ui/core/FormHelperText';
import MenuItem from '@material-ui/core/MenuItem';

import { capitalizeLabel } from '../utils';

const useStyles = makeStyles((theme) => ({
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120
    }
}));

export default function FilterControl({ currentValue, allPossibleValues, handleChangeFn, inputLabelText,
    labelIdFragment, helperText }) {
    const classes = useStyles();
    const selectLabelId = `${labelIdFragment}-select-label`;

    return (
        <FormControl className={ classes.formControl }>
            <InputLabel id={ selectLabelId }>{ capitalizeLabel(inputLabelText) }</InputLabel>
            <Select
                labelId={ selectLabelId }
                value={ currentValue }
                onChange={ handleChangeFn }
            >
                <MenuItem aria-label="None" value={ -1 }><em>None</em></MenuItem>
                {
                    allPossibleValues.map(value => (
                        <MenuItem key={ value.id } value={ value.id }>{ capitalizeLabel(value.text) }</MenuItem>
                    ))
                }
            </Select>
            <FormHelperText>{ helperText } </FormHelperText>
        </FormControl>
    );
}

FilterControl.propTypes = {
    handleChangeFn: PropTypes.func,
    currentValue: PropTypes.number,
    allPossibleValues: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number,
        name: PropTypes.string,
    })),
    inputLabelText: PropTypes.string,
    labelIdFragment: PropTypes.string,
    helperText: PropTypes.string
};