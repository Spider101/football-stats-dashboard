import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import Grid from '@material-ui/core/Grid';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import Checkbox from '@material-ui/core/Checkbox';
import ListItemText from '@material-ui/core/ListItemText';
import Chip from '@material-ui/core/Chip';

import { makeStyles } from '@material-ui/core/styles';

import { capitalizeLabel, allSquadHubTableHeaders, moraleIconsMap, nationalityFlagMap } from '../utils';
import SquadHubTable from '../widgets/SquadHubTable';

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

    const handleChange = (event) => {
        const selectedValues = event.target.value;
        if (selectedValues.every(selectedValue => allPlayerRoles.includes(selectedValue))){
            setPlayerRoles(event.target.value);
        } else {
            setColumnNames(event.target.value);
        }
    };

    // we just need to calculate this once when the component is mounted
    const rowData = React.useMemo(() => buildRowDataForSquadTable(players), [players]);

    const filteredRowData = filterRowsByRole(rowData, playerRoles);

    return (
        <Grid container spacing={2}>
            <Grid item xs={6}>
                <FormControl className={ classes.formControl }>
                    <InputLabel id="configure-columns-input-label">Configure Columns</InputLabel>
                    <Select
                        labelId="configure-columns-select-label"
                        id="configure-columns-checkbox"
                        multiple
                        value={columnNames}
                        onChange={ handleChange }
                        input={ <Input /> }
                        renderValue={ (selected) => {
                            let renderedValue = null;
                            if (_.isEqual(selected, allSquadHubTableHeaderNames)) {
                                renderedValue =
                                    <Chip key='All Columns' label='All Columns' className={ classes.chip } />;
                            } else {
                                renderedValue = selected.map((value) => (
                                    <Chip key={ value } label={ capitalizeLabel(value) } className={ classes.chip } />
                                ));
                            }
                            return (
                                <div className={classes.chips}>
                                    { renderedValue }
                                </div>
                            );
                        }}
                    >
                        {
                            allSquadHubTableHeaders.map(header => (
                                <MenuItem key={ header.id } value={ header.id }>
                                    <Checkbox
                                        checked={ columnNames.indexOf(header.id) > -1 } />
                                    <ListItemText primary={ capitalizeLabel(header.id) } />
                                </MenuItem>
                            ))
                        }
                    </Select>

                </FormControl>
            </Grid>
            <Grid item xs={6}>
                <FormControl className={ classes.formControl }>
                    <InputLabel id="filter-rows-input-label">Filter Players</InputLabel>
                    <Select
                        labelId="filter-rows-select-label"
                        id="filter-rows-checkbox"
                        multiple
                        value={playerRoles}
                        onChange={ handleChange }
                        input={ <Input /> }
                        renderValue={ (selected) => {
                            let renderedValue = null;
                            if (_.isEqual(selected, allPlayerRoles)) {
                                renderedValue =
                                    <Chip key='All Players' label='All Players' className={ classes.chip } />;
                            } else {
                                renderedValue = selected.map((value) => (
                                    <Chip key={ value } label={ capitalizeLabel(value) } className={ classes.chip } />
                                ));
                            }
                            return (
                                <div className={classes.chips}>
                                    { renderedValue }
                                </div>
                            );
                        }}
                    >
                        {
                            allPlayerRoles.map(role => (
                                <MenuItem key={ role } value={ role }>
                                    <Checkbox
                                        checked={ playerRoles.indexOf(role) > -1 } />
                                    <ListItemText primary={ capitalizeLabel(role) } />
                                </MenuItem>
                            ))
                        }
                    </Select>

                </FormControl>
            </Grid>
            <Grid item xs={12}>
                <SquadHubTable { ...filterColumns(allSquadHubTableHeaders, filteredRowData, columnNames) } />
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