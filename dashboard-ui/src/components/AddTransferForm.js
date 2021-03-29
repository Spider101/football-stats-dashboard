import React from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';

import { capitalizeLabel, convertCamelCaseToSnakeCase } from '../utils';
import { FormControl, InputAdornment, InputLabel, MenuItem, Select } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
    root: {
        '& .MuiTextField-root': {
            margin: theme.spacing(1),
            width: '25ch',
        },
    },
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120
    }
}));

export default function AddTransferForm({ fields, transfer, handleChangeFn }) {
    const classes = useStyles();

    return (
        <form className={ classes.root }>
            {
                fields.map((field, _fieldIdx) => {
                    const label = capitalizeLabel(convertCamelCaseToSnakeCase(field.name));
                    const inputProps = field.name === 'fee' ? {
                        startAdornment: <InputAdornment position='start'>$</InputAdornment>
                    } : {};
                    let formElement = null;
                    if (field.type === 'select') {
                        formElement = (
                            <FormControl className={ classes.formControl } key={ _fieldIdx }>
                                <InputLabel id="">{ label }</InputLabel>
                                <Select
                                    id=''
                                    name={ field.name }
                                    value={ transfer[field.name ] }
                                    defaultValue={ field.defaultValue }
                                    onChange={ e => handleChangeFn(e) }
                                >
                                    {
                                        field.availableValues.map((validValue, _valueIdx) => (
                                            <MenuItem key={ _valueIdx } value={ validValue }>{ validValue }</MenuItem>
                                        ))
                                    }
                                </Select>
                            </FormControl>
                        );
                    } else {
                        formElement = (
                            <TextField
                                key={ _fieldIdx }
                                label={ label }
                                name={ field.name }
                                type={ field.name === 'id' ? 'hidden' : field.type }
                                InputLabelProps={{
                                    shrink: true
                                }}
                                InputProps={ inputProps }
                                value={ transfer[field.name] }
                                onChange={ e => handleChangeFn(e) }
                                style={ field.name === 'id' ? { display: 'none' } : null }
                            />
                        );
                    }
                    return formElement;
                })
            }
        </form>
    );
}

AddTransferForm.propTypes = {
    fields: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        type: PropTypes.string,
    })),
    handleChangeFn: PropTypes.func
};