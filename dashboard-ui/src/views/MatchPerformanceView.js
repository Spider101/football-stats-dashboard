import { useState, useMemo } from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import SortableTable from '../widgets/SortableTable';
import TableFilterControl from '../components/TableFilterControl';

import { buildChartPalette, convertCamelCaseToSnakeCase } from '../utils';
import { Bar, BarChart, ResponsiveContainer, Tooltip, Legend, YAxis, XAxis, CartesianGrid } from 'recharts';
import CustomToolTip from '../components/CustomToolTip';

const matchPerformanceTableHeaderDisplayTypeMap = {
    competition: 'string',
    appearances: 'number',
    goals: 'number',
    penalties: 'number',
    assists: 'number',
    player_of_the_match: 'number',
    yellow_cards: 'number',
    red_cards: 'number',
    tackles: 'number',
    pass_completion_rate: 'string',
    dribbles: 'number',
    fouls: 'number',
    average_rating: 'number'
};

const buildHeaderDataForMatchPerformanceTable = headerNames =>
    headerNames.map(name => ({
        id: name === 'id' ? 'competition' : convertCamelCaseToSnakeCase(name) + (name.includes('Rate') ? ' (%)' : ''),
        type: matchPerformanceTableHeaderDisplayTypeMap[name]
    }));

const buildMatchPerformanceData = competitionData => {
    return competitionData.map(competitionPerformance => {
        return Object.entries(competitionPerformance).map(([key, value]) => {
            const label = key === 'id' ? 'competition' : key === 'matchRatingHistory' ? 'averageRating' : key;

            const data = key === 'matchRatingHistory' ? value.reduce((a, b) => a + b, 0) / value.length : value;

            return {
                id: convertCamelCaseToSnakeCase(label),
                type: key === 'id' ? 'string' : 'number',
                data
            };
        });
    });
};

const filterMatchRatingsByCompetitions = (competitionData, competitionsList) =>
    competitionData
        .filter(competition => competitionsList.includes(competition.id))
        .map(competition =>
            competition.matchRatingHistory.map((matchRating, idx) => ({
                matchNumber: idx,
                [competition.id]: matchRating
            }))
        )
        .flat();

const filterMatchPerformancesByCompetitions = (matchPerformances, competitionNames) =>
    matchPerformances.filter(matchPerformance => {
        const competitionData = matchPerformance.find(entity => entity.id === 'competition');
        return competitionNames.includes(competitionData.data);
    });

export default function MatchPerformanceView({ playerPerformance: { competitions } }) {
    // const chartTitle = 'Player Performance over last 10 matches';

    const allCompetitionNames = competitions.map(competition => competition.id).flat();

    const [competitionNames, setCompetitionNames] = useState(allCompetitionNames);

    const handleChange = event => setCompetitionNames(event.target.value);

    const rowData = useMemo(() => buildMatchPerformanceData(competitions), [competitions]);

    const matchPerformanceTableHeaders = buildHeaderDataForMatchPerformanceTable(
        Object.keys(competitions.length === 0 ? matchPerformanceTableHeaderDisplayTypeMap : competitions[0])
    );
    const matchPerformanceData = {
        headers: matchPerformanceTableHeaders,
        rows: filterMatchPerformancesByCompetitions(rowData, competitionNames)
    };

    const chartData = filterMatchRatingsByCompetitions(competitions, competitionNames);
    const { getPaletteColor } = buildChartPalette();
    return (
        <Grid container spacing={2}>
            <Grid item xs={6}>
                <TableFilterControl
                    currentValues={competitionNames}
                    handleChangeFn={handleChange}
                    allPossibleValues={allCompetitionNames}
                    allValuesSelectedLabel='All Competitions'
                    inputLabelText='Filter Competitions'
                    labelIdFragment='filter-competitions'
                />
            </Grid>
            <Grid item xs={12}>
                <ResponsiveContainer height={500} width='100%'>
                    <BarChart data={chartData} barGap={0} barCategoryGap={0}>
                        <Tooltip content={<CustomToolTip />}/>
                        {competitionNames.map((competitionName, idx) => (
                            <Bar key={competitionName} dataKey={competitionName} fill={getPaletteColor(idx)} />
                        ))}
                        <XAxis hide={true} dataKey='matchNumber' />
                        <YAxis
                            axisLine={false}
                            tickLine={false}
                            domain={[0, 11]}
                            label={{ value: 'Match Rating', angle: -90 }}
                        />
                        <CartesianGrid vertical={false} opacity={0.5} />
                        <Legend verticalAlign='top' />
                    </BarChart>
                </ResponsiveContainer>
            </Grid>
            <Grid item xs={12}>
                <SortableTable {...matchPerformanceData} />
            </Grid>
        </Grid>
    );
}

MatchPerformanceView.propTypes = {
    playerPerformance: PropTypes.shape({
        competitions: PropTypes.arrayOf(
            PropTypes.shape({
                id: PropTypes.string,
                appearances: PropTypes.number,
                goals: PropTypes.number,
                penalties: PropTypes.number,
                assists: PropTypes.number,
                playerOfTheMatch: PropTypes.number,
                yellowCards: PropTypes.number,
                redCards: PropTypes.number,
                tackles: PropTypes.number,
                passCompletionRate: PropTypes.number,
                dribbles: PropTypes.number,
                fouls: PropTypes.number,
                matchRatingHistory: PropTypes.arrayOf(PropTypes.number)
            })
        )
    })
};
