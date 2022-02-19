import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import TableContainer from '@material-ui/core/TableContainer';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';

import { makeStyles } from '@material-ui/core';

import EnhancedTableHeader from '../components/EnhancedTableHeader';
import { capitalizeLabel, stableSortList } from '../utils';
import { Link } from 'react-router-dom';
import { Bar, BarChart } from 'recharts';
import { useTheme } from '@material-ui/core/styles';

const useStyles = makeStyles({
    table: {
        minWidth: 750
    },
    tableCell: {
        maxHeight: 40
    },
    flag: {
        width: 40,
        height: 'auto',
        display: 'table-cell'
    }
});

const getStartingColumnName = headers => headers[0] !== undefined ? headers[0].id : '';

const transformHeaderCells = headerCells => {
    return headerCells.map(headerCell => ({
        id: headerCell.id,
        label: capitalizeLabel(headerCell.id),
        alignment: headerCell.type === 'number' ? 'right' : 'left'
    }));
};

const getCellChart = (chartData, dataKey, fillColor) => (
    <BarChart data={chartData} width={80} height={40}>
        <Bar dataKey={dataKey} fill={fillColor}/>
    </BarChart>
);

export default function SortableTable({ headers, rows }) {
    const classes = useStyles();
    const theme = useTheme();

    const [ order, setOrder ] = useState('asc');
    const [orderBy, setOrderBy ] = useState(getStartingColumnName(headers));
    const [ sortedRows, setSortedRows ] = useState([]);

    useEffect(() => {
        const newStartingColumnName = getStartingColumnName(headers);
        const initialOrder = 'asc';

        setSortedRows(stableSortList(rows, initialOrder, newStartingColumnName));
        setOrder(initialOrder);
        setOrderBy(newStartingColumnName);
    }, [headers, rows]);

    const handleRequestSort = (event, property) => {
        // check if selected column is currently in ascending order and if so flip the order
        const isCurrentlyAsc = orderBy === property && order === 'asc';
        const newOrder = isCurrentlyAsc ? 'desc' : 'asc';
        setOrder(newOrder);

        // if some other column has been selected, we switch the orderBy to it ('asc' is default order)
        // if not, we just reinforce the orderBy to the currently selected column
        setOrderBy(property);

        setSortedRows(stableSortList(rows, newOrder, property));
    };

    return (
        headers.length === 0 && sortedRows.length === 0
            ? <Container maxWidth='lg'>
                <Typography component='div'  variant='h5' style={{ textAlign: 'center', padding: '5%' }}>
                    No Data is available for display. Please select one or more columns!
                </Typography>
            </Container>
            : <TableContainer>
                <Table className={classes.table}>
                    <EnhancedTableHeader
                        headerCells= { transformHeaderCells(headers) }
                        order={ order }
                        orderBy={ orderBy }
                        onRequestSort={ handleRequestSort }
                    />
                    <TableBody>
                        {
                            sortedRows.map((row, _idx) => (
                                <TableRow key={_idx}>
                                    {
                                        row.map((cell, _idx) => {
                                            let tableCell;

                                            if (cell.type === 'image') {
                                                tableCell = (
                                                    <img src={ cell.data }
                                                        className={ classes.flag }
                                                        alt={ cell.metadata.sortValue }
                                                    />
                                                );
                                            } else if (cell.type === 'chart') {
                                                tableCell = getCellChart(
                                                    cell.data,
                                                    cell.id,
                                                    theme.palette.primary.main
                                                );
                                            } else {
                                                tableCell = cell.data;
                                            }

                                            const isWithLink = 'metadata' in cell && 'playerId' in cell.metadata;

                                            return (
                                                <TableCell
                                                    component={ isWithLink ? Link : 'td' }
                                                    to={ isWithLink ? `/player/${cell.metadata.playerId}` : null }
                                                    className={ classes.tableCell }
                                                    key={ _idx}
                                                    align={ cell.type === 'number' ? 'right' : 'left' }
                                                >
                                                    { tableCell }
                                                </TableCell>
                                            );
                                        })
                                    }
                                </TableRow>
                            ))
                        }
                    </TableBody>
                </Table>
                {
                    headers.length !== 0 && sortedRows.length === 0 &&
                    (
                        <Container maxWidth='lg'>
                            <Typography component='div' variant='h5' style={{ textAlign: 'center', padding: '5%' }}>
                                No Data is available for display. Please select one or more rows!
                            </Typography>
                        </Container>
                    )
                }
            </TableContainer>
    );
}

SortableTable.propTypes = {
    headers: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.string,
        type: PropTypes.string
    })),
    rows: PropTypes.arrayOf(
        PropTypes.arrayOf(PropTypes.shape({
            type: PropTypes.string,
            data: PropTypes.any,
            metadata: PropTypes.shape({
                sortValue: PropTypes.any
            })
        }))
    )
};