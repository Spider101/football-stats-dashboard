import React from 'react';
import PropTypes from 'prop-types';

import AttributeComparisonItem from '../components/AttributeComparisonItem';
import TableContainer from '@material-ui/core/TableContainer';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import TableHead from '@material-ui/core/TableHead';
import withStyles from '@material-ui/core/styles/withStyles';

const StyledTableCell = withStyles((theme) => ({
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

export default function AttributeComparisonTable({ headers, rows, children }) {
    return (
        <TableContainer>
            <Table>
                <TableHead >
                    <TableRow>
                        { headers.map((header, idx) => (
                            <StyledTableCell key={ idx }> { header }</StyledTableCell>
                        ))}
                    </TableRow>
                </TableHead>
                <TableBody>
                    { rows.map((row, idx) => (
                        <StyledTableRow key={ idx }>
                            { row.map((cell, idx) => {

                                // inject props from current scope into the child component
                                const childrenWithProps = React.Children.map(children, child => {
                                    if (React.isValidElement(child)) {
                                        return React.cloneElement(child, { ...cell });
                                    }
                                    return child;
                                });

                                return (
                                    <StyledTableCell component='th' scope='row' key={ idx }>
                                       { cell != null ? childrenWithProps : null }
                                    </StyledTableCell>
                                );
                            })}
                        </StyledTableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}

AttributeComparisonTable.propTypes = {
    headers: PropTypes.arrayOf(PropTypes.string),
    rows: PropTypes.arrayOf(PropTypes.arrayOf(AttributeComparisonItem.propTypes)),
    children: PropTypes.node
};
