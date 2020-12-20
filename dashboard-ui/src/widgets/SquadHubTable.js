import React from 'react';
import PropTypes from 'prop-types';

import TableContainer from '@material-ui/core/TableContainer';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';

import { makeStyles } from '@material-ui/core';

import EnhancedTableHeader from '../components/EnhancedTableHeader';

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%'
    },
    table: {
        minWidth: 750
    }
}));

// TODO: move this into a common utils file
const capitalizeLabel = (label) => {
    return label.split(' ')
        .map(word => word[0].toUpperCase() + word.slice(1))
        .join(' ')
};

const transformHeaderCells = (headerCells) => {
    return headerCells.map(headerCell => ({
        id: headerCell.id,
        label: capitalizeLabel(headerCell.id),
        alignment: headerCell.type === 'number' ? 'right' : 'left'
    }))
};

const ascComparator = (a, b, propName) => {
    const x = a.find(el => el.id === propName);
    const y = b.find(el => el.id === propName);

    return x.data < y.data ? -1 : x.data > y.data ? 1 : 0;
}

// TODO: move this into a common utils file
const getComparator = (order, orderBy) => {
    return order === 'asc'
        ? (a, b) => ascComparator(a, b, orderBy)
        : (a, b) => -ascComparator(a, b, orderBy)
};

// TODO: move this into a common utils file
const stableSortList = (array, comparator) => {
    const orderedArray = array.map((el, _idx) => [ el, _idx]);

    orderedArray.sort((a, b) => {
        const order = comparator(a[0], b[0]);

        // if the elements are not equal, return if one was bigger than the other
        if (order !== 0) return order;

        // if they are equal, use the position/order to sort the elements
        return a[1] - b[1]
    });

    // return the array with the order information
    return orderedArray.map(([el, _]) => el)
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
                                        <TableCell align={ cell.type === 'number' ? 'right' : 'left' }>
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