import { useState, useRef, useEffect, useMemo } from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';
import { makeStyles } from '@material-ui/core/styles';

import { MORALE_ICON_MAPPING } from '../constants';
import SortableTable from '../widgets/SortableTable';
import TableFilterControl from '../components/TableFilterControl';

const useStyles = makeStyles(theme => ({
    fab: {
        position: 'fixed',
        bottom: theme.spacing(4),
        right: theme.spacing(4)
    }
}));

const squadTableHeaderDisplayTypeMap = {
    name: 'string',
    nationality: 'image',
    role: 'string',
    wages: 'string',
    form: 'chart',
    morale: 'icon',
    current_ability: 'number'
};

// TODO: define a more appropriate method for this
const getSortValueForForm = matchRatingsList => matchRatingsList.length > 0 && matchRatingsList[0];
const transformFormData = formData => formData.map(form => ({ form }));

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
                // TODO: maybe avoid using M and K formatting here.
                row = { id: key, type: 'string', data: '$' + value + 'K' };
                break;
            case 'nationality':
                row = {
                    id: key,
                    type: 'image',
                    data: value['flagURL'],
                    metadata: { sortValue: value['countryName'] }
                };
                break;
            case 'morale':
                row = {
                    id: key,
                    type: 'icon',
                    data: MORALE_ICON_MAPPING.find(entity => entity.morale === value).icon,
                    metadata: { sortValue: value }
                };
                break;
            case 'form':
                row = {
                    id: key,
                    type: 'chart',
                    data: transformFormData(value),
                    metadata: { sortValue: getSortValueForForm(value) || 0 }
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

export default function SquadHubView({ players, addPlayerWidget }) {
    const classes = useStyles();
    const [playerRoles, setPlayerRoles] = useState([]);

    const initialSquadHubTableHeaders = buildHeaderDataForSquadTable(
        Object.keys(players.length === 0 ? squadTableHeaderDisplayTypeMap : players[0])
    );
    const allSquadHubTableHeaderNames = initialSquadHubTableHeaders.map(header => header.id);
    const [columnNames, setColumnNames] = useState(allSquadHubTableHeaderNames);

    const allPlayerRoles = useRef([]);
    useEffect(() => {
        // get all the distinct player roles in the dataset
        allPlayerRoles.current = [...new Set(players.map(player => player.role))];

        setPlayerRoles(allPlayerRoles.current);
    }, [players]);

    const handleChange = changeHandler => event => changeHandler(event.target.value);

    // we just need to calculate this once when the component is mounted
    const rowData = useMemo(() => buildRowDataForSquadTable(players), [players]);

    const filteredRowData = filterRowsByRole(rowData, playerRoles);

    return (
        <>
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
                        allValuesSelectedLabel='All Player Roles'
                        inputLabelText='Filter Players By Role'
                        labelIdFragment='filter-rows'
                        customStyles={{ float: 'right' }}
                    />
                </Grid>
                <Grid item xs={12}>
                    <SortableTable {...filterColumns(initialSquadHubTableHeaders, filteredRowData, columnNames)} />
                </Grid>
            </Grid>
            <div className={classes.fab}>
                {addPlayerWidget}
            </div>
        </>
    );
}

SquadHubView.propTypes = {
    players: PropTypes.arrayOf(
        PropTypes.shape({
            playerId: PropTypes.string,
            name: PropTypes.string,
            nationality: PropTypes.shape({
                countryName: PropTypes.string,
                flagURL: PropTypes.string
            }),
            role: PropTypes.string,
            wages: PropTypes.number,
            form: PropTypes.arrayOf(PropTypes.number),
            morale: PropTypes.string,
            currentAbility: PropTypes.number
        })
    ),
    addPlayerWidget: PropTypes.element
};