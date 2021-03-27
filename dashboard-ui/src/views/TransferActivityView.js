import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';
import SortableTable from '../widgets/SortableTable';
import TableFilterControl from '../components/TableFilterControl';

import { convertCamelCaseToSnakeCase, filterRowsByRole } from '../utils';

const buildRowDataForTransfersTable = transfers =>
    transfers.map(transfer =>
        Object.entries(transfer).map(([key, value]) => {
            let row = { id: convertCamelCaseToSnakeCase(key) };
            switch(key) {
            case 'fee':
                if ((value / 1000000) > 1) {
                    const roundedValue = Math.round((value / 1000000), 1);
                    row = {
                        ...row,
                        type: 'number',
                        data: `$${roundedValue}M`,
                        metadata: { sortValue: value }
                    };
                } else {
                    const roundedValue = Math.round((value / 100000), 1);
                    row = {
                        ...row,
                        type: 'number',
                        data: `$${roundedValue}K`,
                        metadata: { sortValue: value }
                    };
                }
                break;
            case 'date':
                row = {
                    ...row,
                    type: 'string',
                    data: value.slice(0, 10).split`-`.join`/`
                };
                break;
            case 'swapPlayer':
                row = {
                    ...row,
                    type: 'string',
                    data: value === '' ? 'N/A' : value,
                    metadata: { sortValue: value }
                };
                break;
            default:
                row = { ...row, type:  isNaN(value) ? 'string' : 'number', data: value };
                break;
            }
            return row;
        })
    );

const buildHeadersForTransfersTable = headerNames => headerNames.map(headerName => ({
    id: convertCamelCaseToSnakeCase(headerName),
    type: headerName === 'fee' ? 'number' : 'string'
}));

export default function TransferActivityView({ transfers }) {
    const [playerRoles, setPlayerRoles] = React.useState([]);
    const allPlayerRoles = React.useRef([]);
    React.useEffect(() => {
        // get all the distinct player roles in the dataset
        allPlayerRoles.current = [ ...new Set(transfers.map(transfer => transfer.role)) ];

        setPlayerRoles(allPlayerRoles.current);
    }, [transfers]);

    const handleChange = (changeHandler) => (event) => changeHandler(event.target.value);

    const headers = React.useMemo(() => buildHeadersForTransfersTable(Object.keys(transfers[0])), [transfers]);
    const rowData = React.useMemo(() => buildRowDataForTransfersTable(transfers), [transfers]);
    const tableData = {
        headers,
        rows: filterRowsByRole(rowData, playerRoles)
    };

    return (
        <>
            <Grid container spacing={2}>
                <Grid item xs={6}>
                    <TableFilterControl
                        currentValues={ playerRoles }
                        handleChangeFn={ handleChange(setPlayerRoles) }
                        allPossibleValues={ allPlayerRoles.current }
                        allValuesSelectedLabel='All Players'
                        inputLabelText='Filter Players'
                        labelIdFragment='filter-rows'
                    />
                </Grid>
                <Grid item xs={12}>
                    <SortableTable { ...tableData } />
                </Grid>
            </Grid>
        </>
    );
}

TransferActivityView.propTypes = {
    transfers: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        role: PropTypes.string,
        currentAbility: PropTypes.number,
        incomingClub: PropTypes.string,
        outgoingClub: PropTypes.string,
        type: PropTypes.string,
        swapPlayer: PropTypes.string,
        fee: PropTypes.number,
        date: PropTypes.string,
    }))
};