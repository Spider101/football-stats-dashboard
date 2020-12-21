import React from 'react';
import PropTypes from 'prop-types';

import TableContainer from '@material-ui/core/TableContainer';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';

import { makeStyles } from '@material-ui/core';

import EnhancedTableHeader from '../components/EnhancedTableHeader';
import { capitalizeLabel, getComparator, stableSortList } from '../utils';

const useStyles = makeStyles({
    root: {
        width: '100%'
    },
    table: {
        minWidth: 750
    }
});

const transformHeaderCells = (headerCells) => {
    return headerCells.map(headerCell => ({
        id: headerCell.id,
        label: capitalizeLabel(headerCell.id),
        alignment: headerCell.type === 'number' ? 'right' : 'left'
    }));
};

export default function SquadHubTable({ headers, rows }) {
    const classes = useStyles();

    const [ order, setOrder ] = React.useState('asc');
    const [orderBy, setOrderBy ] = React.useState(headers[0].id);

    const handleRequestSort = (event, property) => {
        // check if selected column is currently in ascending order and if so flip the order
        const isCurrentlyAsc = orderBy === property && order === 'asc';
        setOrder(isCurrentlyAsc ? 'desc' : 'asc');

        // if some other column has been selected, we switch the orderBy to it ('asc' is default order)
        // if not, we just reinforce the orderBy to the currently selected column
        setOrderBy(property);

    };

    return (
        <TableContainer>
            <Table className={classes.table}>
                <EnhancedTableHeader
                    headerCells= { transformHeaderCells(headers) }
                    order={ order }
                    orderBy={ orderBy }
                    onRequestSort={ handleRequestSort }
                />
                <TableBody>
                    {
                        stableSortList(rows, getComparator(order, orderBy))
                            .map((row, _idx) => (
                                <TableRow
                                    key={_idx}
                                >
                                    { row.map((cell, _idx) => (
                                        <TableCell key={ _idx} align={ cell.type === 'number' ? 'right' : 'left' }>
                                            { cell.data }
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                    }
                </TableBody>
            </Table>
        </TableContainer>
    );
}

SquadHubTable.propTypes = {
    headers: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.string,
        type: PropTypes.string
    })),
    rows: PropTypes.arrayOf(
        PropTypes.arrayOf(PropTypes.shape({
            type: PropTypes.string,
            data: PropTypes.any
        }))
    )
};