import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import Checkbox from '@material-ui/core/Checkbox';
import ListItemText from '@material-ui/core/ListItemText';
import Chip from '@material-ui/core/Chip';

import { capitalizeLabel } from '../utils';

export default function TableFilterControl({ currentValues, handleChangeFn, allPossibleValues, allValuesSelectedLabel,
    inputLabelText, labelIdFragment, customStyles, customClasses }) {
    const selectLabelId = `${labelIdFragment}-select-label`;
    const checkboxId = `${labelIdFragment}-checkbox`;

    return (
        <FormControl className={ customClasses.formControl } style={ customStyles }>
            <InputLabel id={ selectLabelId }>{ inputLabelText }</InputLabel>
            <Select
                labelId={ selectLabelId }
                id={ checkboxId }
                multiple
                value={ currentValues }
                onChange={ handleChangeFn }
                input={ <Input /> }
                renderValue={ (selected) => {
                    let renderedValue = null;
                    // check if the selected values are the same as all the possible values (ignoring order)
                    if (_.isEqual(_.sortBy(selected), _.sortBy(allPossibleValues))) {
                        renderedValue = (
                            <Chip key={ allValuesSelectedLabel }
                                label={ allValuesSelectedLabel }
                                className={ customClasses.chip }
                            />
                        );
                    } else {
                        renderedValue = selected.map((value) => (
                            <Chip key={ value } label={ capitalizeLabel(value) } className={ customClasses.chip } />
                        ));
                    }
                    return (
                        <div className={customClasses.chips}>
                            { renderedValue }
                        </div>
                    );
                }}
            >
                {
                    allPossibleValues.map(value => (
                        <MenuItem key={ value } value={value }>
                            <Checkbox
                                checked={ currentValues.indexOf(value) > -1 } />
                            <ListItemText primary={ capitalizeLabel(value) } />
                        </MenuItem>
                    ))
                }
            </Select>

        </FormControl>
    );
}

TableFilterControl.propTypes = {
    currentValues: PropTypes.arrayOf(PropTypes.string),
    handleChangeFn: PropTypes.func,
    allPossibleValues: PropTypes.arrayOf(PropTypes.object),
    allValuesSelectedLabel: PropTypes.string,
    inputLabelText: PropTypes.string,
    labelIdFragment: PropTypes.string,
    customStyles: PropTypes.object,
    customClasses: PropTypes.arrayOf(PropTypes.string)
};