import PropTypes from 'prop-types';

import SortableTable from './SortableTable';
import { convertCamelCaseToSnakeCase } from '../utils';
import { Card, CardHeader, CardContent } from '@material-ui/core';

const buildLeagueTableData = teamsInTable =>
    teamsInTable.map(teamMetadata =>
        Object.entries(teamMetadata).map(([key, value]) => ({
            id: convertCamelCaseToSnakeCase(key),
            type: key === 'team' ? 'string' : 'number',
            data: value
        }))
    );

const buildLeagueTableHeaders = tableHeaderLabels =>
    tableHeaderLabels.map(label => ({
        id: convertCamelCaseToSnakeCase(label),
        type: label === 'team' ? 'string' : 'number'
    }));

export default function LeagueTable({ metadata }) {
    const leagueTableData = {
        headers: buildLeagueTableHeaders(Object.keys(metadata[0])),
        rows: buildLeagueTableData(metadata)
    };

    return (
        <>
            <Card>
                <CardHeader title='League Table' align='center'/>
                <CardContent>
                    <SortableTable {...leagueTableData} />
                </CardContent>
            </Card>
        </>
    );
}

LeagueTable.propTypes = {
    metadata: PropTypes.array
};
