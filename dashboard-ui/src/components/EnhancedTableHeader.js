import React from 'react';
import PropTypes from 'prop-types';

import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';
import TableSortLabel from '@material-ui/core/TableSortLabel';

import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles({
    visuallyHidden: {
        border: 0,
        clip: 'rect(0 0 0 0)',
        height: 1,
        margin: -1,
        overflow: 'hidden',
        padding: 0,
        position: 'absolute',
        top: 20,
        width: 1
    }
});

export default function EnhancedTableHeader({ headerCells, order, orderBy, onRequestSort }) {
    const classes = useStyles();

    const createSortHandler = (property) => (event) => {
        onRequestSort(event, property);
    };

    return (
        <TableHead>
            <TableRow>
                {
                    headerCells.map((headerCell) => (
                        <TableCell
                            key={ headerCell.id }
                            align={ headerCell.alignment }
                            padding='default'
                            sortDirection={ orderBy === headerCell.id ? order : false }
                        >
                            <TableSortLabel
                                active={ orderBy === headerCell.id }
                                direction={ orderBy === headerCell.id ? order : 'asc' }
                                onClick={ createSortHandler(headerCell.id) }
                            >
                                { headerCell.label }
                                { orderBy === headerCell.id ? (
                                    <span className={ classes.visuallyHidden }>
                                        { order === 'desc' ? 'sorted descending' : 'sorted ascending' }
                                    </span>
                                ) : null }
                            </TableSortLabel>
                        </TableCell>

                    ))
                }
            </TableRow>
        </TableHead>
    );
}

EnhancedTableHeader.propTypes = {
    order: PropTypes.oneOf(['asc', 'desc']),
    orderBy: PropTypes.string,
    onRequestSort: PropTypes.func,
    headerCells: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.string,
        alignment: PropTypes.string,
        label: PropTypes.string
    }))
};