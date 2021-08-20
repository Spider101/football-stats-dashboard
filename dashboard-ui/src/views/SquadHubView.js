import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';

import { squadTableHeaderDisplayTypeMap, moraleIconsMap, nationalityFlagMap } from '../utils';
import SortableTable from '../widgets/SortableTable';
import TableFilterControl from '../components/TableFilterControl';

// TODO: define a more appropriate method for this
const getSortValueForForm = matchRatingsList => matchRatingsList[0];

const buildHeaderDataForSquadTable = headerNames =>
    headerNames
        .filter(name => name !== 'playerId')
        .map(name => ({
            id: name,
            type: squadTableHeaderDisplayTypeMap[name]
        }));

const buildRowDataForSquadTable = players => {
    return players.map(player => {
        const keys = Object.keys(player);
        const playerId = player.playerId;
        const keysToFocusOn = keys.filter(key => key !== 'playerId');
        return Object.entries(_.pick(player, keysToFocusOn)).map(([key, value]) => {
            let row = null;
            switch (key) {
                case 'wages':
                    row = { id: key, type: 'string', data: '$' + value + 'K' };
                    break;
                case 'nationality':
                    row = {
                        id: key,
                        type: 'image',
                        data: nationalityFlagMap.find(entity => entity.nationality === value)?.flag || '',
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
                            series: [
                                {
                                    name: 'Match Rating',
                                    data: value
                                }
                            ]
                        },
                        metadata: { sortValue: getSortValueForForm(value) }
                    };
                    break;
                case 'name':
                    row = {
                        id: key,
                        type: 'link',
                        data: value,
                        metadata: { playerId }
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

const filterColumns = (originalHeaders, filteredRowData, selectedColumns) => {
    const headers = originalHeaders.filter(header => selectedColumns.includes(header.id));
    const updatedRows = filteredRowData.map(rowData =>
        rowData.filter(cellData => selectedColumns.includes(cellData.id))
    );

    return {
        headers,
        // return empty list if all rows are just empty lists themselves
        rows: updatedRows.every(row => row.length === 0) ? [] : updatedRows
    };
};

const filterRowsByRole = (originalRowData, roles) =>
    originalRowData.filter(rowData => {
        const roleData = rowData.find(cell => cell.id === 'role');
        return roles.includes(roleData.data);
    });

export default function SquadHubView({ players }) {
    const [playerRoles, setPlayerRoles] = React.useState([]);

    const initialSquadHubTableHeaders = buildHeaderDataForSquadTable(
        Object.keys(players.length === 0 ? squadTableHeaderDisplayTypeMap : players[0])
    );
    const allSquadHubTableHeaderNames = initialSquadHubTableHeaders.map(header => header.id);
    const [columnNames, setColumnNames] = React.useState(allSquadHubTableHeaderNames);

    const allPlayerRoles = React.useRef([]);
    React.useEffect(() => {
        // get all the distinct player roles in the dataset
        allPlayerRoles.current = [...new Set(players.map(player => player.role))];

        setPlayerRoles(allPlayerRoles.current);
    }, [players]);

    const handleChange = changeHandler => event => changeHandler(event.target.value);

    // we just need to calculate this once when the component is mounted
    const rowData = React.useMemo(() => buildRowDataForSquadTable(players), [players]);

    const filteredRowData = filterRowsByRole(rowData, playerRoles);

    return (
        <Grid container spacing={2}>
            <Grid item xs={6}>
                <TableFilterControl
                    currentValues={columnNames}
                    handleChangeFn={handleChange(setColumnNames)}
                    allPossibleValues={allSquadHubTableHeaderNames}
                    allValuesSelectedLabel='All Columns'
                    inputLabelText='Configure Columns'
                    labelIdFragment='configure-columns'
                />
            </Grid>
            <Grid item xs={6}>
                <TableFilterControl
                    currentValues={playerRoles}
                    handleChangeFn={handleChange(setPlayerRoles)}
                    allPossibleValues={allPlayerRoles.current}
                    allValuesSelectedLabel='All Players'
                    inputLabelText='Filter Players'
                    labelIdFragment='filter-rows'
                    customStyles={{ float: 'right' }}
                />
            </Grid>
            <Grid item xs={12}>
                <SortableTable {...filterColumns(initialSquadHubTableHeaders, filteredRowData, columnNames)} />
            </Grid>
        </Grid>
    );
}

SquadHubView.propTypes = {
    players: PropTypes.arrayOf(
        PropTypes.shape({
            playerId: PropTypes.number,
            name: PropTypes.string,
            nationality: PropTypes.string,
            role: PropTypes.string,
            wages: PropTypes.number,
            form: PropTypes.arrayOf(PropTypes.number),
            morale: PropTypes.string,
            currentAbility: PropTypes.number
        })
    )
};