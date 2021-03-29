import React from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';

import { capitalizeLabel, convertCamelCaseToSnakeCase } from '../utils';
import { InputAdornment } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
    root: {
        '& .MuiTextField-root': {
            margin: theme.spacing(1),
            width: '25ch',
        },
    },
}));

export default function AddTransferForm({ fields, transfer, handleChangeFn }) {
    const classes = useStyles();

    return (
        <form className={ classes.root }>
            {
                fields.map((field, _idx) => {
                    const label = capitalizeLabel(convertCamelCaseToSnakeCase(field.name));
                    const inputProps = field.name === 'fee' ? {
                        startAdornment: <InputAdornment position='start'>$</InputAdornment>
                    } : {};
                    return (
                        <TextField
                            key={ _idx }
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
                    )
                })
            }
        </form>
    )
}

AddTransferForm.propTypes = {
    fields: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        type: PropTypes.string,
    })),
    handleChangeFn: PropTypes.func
};