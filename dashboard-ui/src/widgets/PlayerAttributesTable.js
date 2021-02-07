import React from 'react';
import PropTypes from 'prop-types';

import TableContainer from '@material-ui/core/TableContainer';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import TableHead from '@material-ui/core/TableHead';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';

import withStyles from '@material-ui/core/styles/withStyles';
import { makeStyles } from '@material-ui/core/styles';

export const StyledTableCell = withStyles((theme) => ({
    root: {
        borderRight: '4px solid',
        borderRightColor: theme.palette.action.hover,
        '&:last-child': {
            borderRight: 'none'
        }
    },
    head: {
        backgroundColor: theme.palette.primary.dark,
        color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
        padding: 0
    },
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
    root: {
        '&:nth-of-type(odd)': {
            backgroundColor: theme.palette.action.hover,
        },
    },
}))(TableRow);


const useStyles = makeStyles((theme) => ({
    dropdown: {
        display: 'flex',
        flexDirection: 'row-reverse'
    },
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120
    }
}));

export default function PlayerAttributesTable({ roles, headers, rows, children }) {
    const classes = useStyles();
    const [ role, changeRole ] = React.useState('None');

    const handleChange = (evt) => {
        changeRole(evt.target.value);
    };

    return (
        <div>
            <div className={ classes.dropdown }>
                <FormControl className={ classes.formControl }>
                    <InputLabel  id="player-role-input-label">Player Role</InputLabel>
                    <Select id="player-role-select-label"
                        value={ role }
                        onChange={ handleChange }
                    >
                        <MenuItem value={ 'None' }> <em>None</em> </MenuItem>
                        { Object.keys(roles).map((role, _idx) => (
                            <MenuItem key={ _idx } value={ role }> { role }</MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </div>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            { headers.map((header, idx) => (
                                <StyledTableCell key={ idx }> { header }</StyledTableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        { rows.map((row, _idx) => (
                            <StyledTableRow key={ _idx }>
                                { row.map((cell, _idx) => {
                                    // inject props from current scope into the child component
                                    const childrenWithProps = React.Children.map(children, child => {
                                        if (React.isValidElement(child)) {
                                            const highlightedAttributes = roles[role] || [];
                                            return React.cloneElement(child, { ...cell, highlightedAttributes });
                                        }
                                        return child;
                                    });

                                    return (
                                        <StyledTableCell component='th' scope='row' key={ _idx }>
                                            { cell != null ? childrenWithProps : null }
                                        </StyledTableCell>
                                    );
                                })}
                            </StyledTableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
}

PlayerAttributesTable.propTypes = {
    roles: PropTypes.object,
    headers: PropTypes.arrayOf(PropTypes.string),
    rows: PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.object)),
    children: PropTypes.node
};
