import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import { makeStyles } from '@material-ui/core/styles';

import { allSquadHubTableHeaders, moraleIconsMap, nationalityFlagMap } from '../utils';
import SortableTable from '../widgets/SortableTable';
import TableFilterControl from '../components/TableFilterControl';

// TODO: define a more appropriate method for this
const getSortValueForForm = (matchRatingsList) => matchRatingsList[0];

const buildRowDataForSquadTable = (players) => {
    return players.map(player => {
        return Object.entries(player).map(([key, value]) => {
            let row = null;
            switch(key) {
            case 'wages':
                row = { id: key, type: 'string', data: '$' + value + 'K'};
                break;
            case 'nationality':
                row = {
                    id: key,
                    type: 'image',
                    data: nationalityFlagMap.find(entity => entity.nationality === value).flag,
                    metadata: { sortValue: value }
                };
                break;
            case 'morale':
                row = {
                    id: key,
                    type: 'icon',
                    data: moraleIconsMap.find(entity => entity.morale === value).icon,
                    metadata: { sortValue: value }
                };
                break;
            case 'form':
                row = {
                    id: key,
                    type: 'chart',
                    data: {
                        type: 'bar',
                        series: [{
                            name: 'Match Rating',
                            data: value
                        }]
                    },
                    metadata: { sortValue: getSortValueForForm(value) }
                };
                break;
            default:
                row = { id: key, type: isNaN(value) ? 'string' : 'number', data: value };
                break;
            }
            return row;
        });
    });
};

const useStyles = makeStyles((theme) => ({
    formControl: {
        margin: theme.spacing(1),
        minWidth: 150,
        maxWidth: 300
    },
    chips: {
        display: 'flex',
        flexWrap: 'wrap'
    },
    chip: {
        margin: 2
    }
}));

const filterColumns = (originalHeaders, filteredRowData, selectedColumns) => {
    const headers = originalHeaders.filter(header => selectedColumns.includes(header.id));
    const updatedRows = filteredRowData.map(rowData =>
        rowData.filter(cellData => selectedColumns.includes(cellData.id)));

    return {
        headers,
        // return empty list if all rows are just empty lists themselves
        rows: updatedRows.every(row => row.length === 0) ? [] : updatedRows
    };
};

const filterRowsByRole = (originalRowData, roles) => originalRowData.filter(rowData => {
    const roleData = rowData.find(cell => cell.id === 'role');
    return roles.includes(roleData.data);
});

export default function SquadHubView({ players }) {
    const classes = useStyles();
    const allSquadHubTableHeaderNames = allSquadHubTableHeaders.map(header => header.id);

    const [columnNames, setColumnNames] = React.useState(allSquadHubTableHeaderNames);

    // get all the distinct player roles in the dataset
    const allPlayerRoles = [ ...new Set(players.map(player => player.role)) ];
    const [playerRoles, setPlayerRoles] = React.useState(allPlayerRoles);

    const handleChange = (changeHandler) => (event) => changeHandler(event.target.value);

    // we just need to calculate this once when the component is mounted
    const rowData = React.useMemo(() => buildRowDataForSquadTable(players), [players]);

    const filteredRowData = filterRowsByRole(rowData, playerRoles);
    
    return (
        <Grid container spacing={2}>
            <Grid item xs={6}>
                <TableFilterControl
                    currentValues={ columnNames }
                    handleChangeFn={ handleChange(setColumnNames) }
                    allPossibleValues={ allSquadHubTableHeaderNames }
                    allValuesSelectedLabel='All Columns'
                    inputLabelText='Configure Columns'
                    labelIdFragment='configure-columns'
                    customClasses={ classes }
                />
            </Grid>
            <Grid item xs={6}>
                <TableFilterControl
                    currentValues={ playerRoles }
                    handleChangeFn={ handleChange(setPlayerRoles) }
                    allPossibleValues={ allPlayerRoles }
                    allValuesSelectedLabel='All Players'
                    inputLabelText='Filter Players'
                    labelIdFragment='filter-rows'
                    customClasses={ classes }
                    customStyles={{ float: 'right' }}
                />
            </Grid>
            <Grid item xs={12}>
                <SortableTable { ...filterColumns(allSquadHubTableHeaders, filteredRowData, columnNames) } />
            </Grid>
        </Grid>
    );
}

SquadHubView.propTypes = {
    players: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        nationality: PropTypes.string,
        role: PropTypes.string,
        wages: PropTypes.number,
        form: PropTypes.arrayOf(PropTypes.number),
        morale: PropTypes.string,
        currentAbility: PropTypes.number
    }))
};