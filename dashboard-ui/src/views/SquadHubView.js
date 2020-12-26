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

const filterColumns = (originalHeaders, originalRowData, selectedColumns) => {
    const headers = originalHeaders.filter(header => selectedColumns.includes(header.id));
    const rows = originalRowData.map(rowData =>
        rowData.filter(cellData => selectedColumns.includes(cellData.id)));

    return {
        headers,
        rows
    };
};

export default function SquadHubView({ players }) {
    const classes = useStyles();
    const allSquadHubTableHeaderNames = allSquadHubTableHeaders.map(header => header.id);

    const [columnNames, setColumnNames] = React.useState(allSquadHubTableHeaderNames);

    const handleChange = (event) => {
        setColumnNames(event.target.value);
    };

    // TODO: memoize or put it in a onLoad type hook so it is computed only once per load
    const rowData = buildRowDataForSquadTable(players);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FormControl className={ classes.formControl }>
                    {/* TODO: change the id labels */}
                    <InputLabel id="demo-mutiple-checkbox-label">Configure Columns</InputLabel>
                    <Select
                        labelId="demo-mutiple-checkbox-label"
                        id="demo-mutiple-checkbox"
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
                                        // disabled={ columnNames.length === 1 && columnNames.includes(header.id) }
                                        checked={ columnNames.indexOf(header.id) > -1 } />
                                    <ListItemText primary={ header.id } />
                                </MenuItem>
                            ))
                        }
                    </Select>

                </FormControl>
            </Grid>
            <Grid item xs={12}>
                <SquadHubTable { ...filterColumns(allSquadHubTableHeaders, rowData, columnNames) } />
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