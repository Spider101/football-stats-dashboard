import PropTypes from 'prop-types';

import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TableSortLabel from '@material-ui/core/TableSortLabel';

import { makeStyles, withStyles } from '@material-ui/core/styles';

import { StyledTableCell } from '../components/PlayerAttributesTable';

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
    },
});

const StyledTableSortLabel = withStyles((theme) => ({
    root: {
        color: theme.palette.common.white,
        '&$active': {
            color: theme.palette.common.white
        }
    },
    active: {},
    icon: {
        color: 'inherit !important'
    }
}))(TableSortLabel);

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
                        <StyledTableCell
                            key={ headerCell.id }
                            align={ headerCell.alignment }
                            padding='default'
                            sortDirection={ orderBy === headerCell.id ? order : false }
                        >
                            <StyledTableSortLabel
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
                            </StyledTableSortLabel>
                        </StyledTableCell>

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