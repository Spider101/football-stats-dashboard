import React from 'react';
import PropTypes from 'prop-types';

import ReactApexChart from 'react-apexcharts';
import Grid from '@material-ui/core/Grid';

import SortableTable from '../widgets/SortableTable';
import TableFilterControl from '../components/TableFilterControl';

import { allMatchPerformanceTableHeaders, convertCamelCaseToSnakeCase } from '../utils';
import { useGlobalChartOptions } from '../context/chartOptionsProvider';

const buildMatchPerformanceData = competitionData => {
    return competitionData.map(competitionPerformance => {
        return Object.entries(competitionPerformance).map(([key, value]) => {
            const label = key === 'id' ? 'competition' : key === 'matchRatingHistory' ? 'averageRating' : key;

            const data = key === 'matchRatingHistory'
                ? value.reduce((a, b) => a + b, 0) / value.length
                : key.includes('percentage') ? (value/100).toFixed(2) : value;

            return {
                id: convertCamelCaseToSnakeCase(label),
                type: 'number',
                data
            };
        });
    });
};

const filterMatchRatingsByCompetitions = (competitionData, competitionsList) =>
    competitionData
        .filter(competition => competitionsList.includes(competition.id))
        .map(competition => competition.matchRatingHistory)
        .flat();

const filterMatchPerformancesByCompetitions = (matchPerformances, competitionNames) =>
    matchPerformances.filter(matchPerformance => {
        const competitionData = matchPerformance.find(entity => entity.id === 'competition');
        return competitionNames.includes(competitionData.data);
    });

const getOptions = (globalChartOptions, chartTitle) => ({
    ...globalChartOptions,
    dataLabels: { enabled: false },
    title: {
        ...globalChartOptions.title,
        text: chartTitle
    },
    xaxis: {
        title: { text: 'Matches', style: { fontFamily: 'Roboto' } },
        categories: [ ...Array(10) ].map((_, _idx) => _idx + 1)
    }
});

export default function MatchPerformanceView({ playerPerformance: { competitions } }) {
    const chartTitle = 'Player Performance over last 10 matches';

    const allCompetitionNames = competitions.map(competition => competition.id).flat();

    const [competitionNames, setCompetitionNames] = React.useState(allCompetitionNames);

    const handleChange = (event) => setCompetitionNames(event.target.value);

    const rowData = React.useMemo(() => buildMatchPerformanceData(competitions), [competitions]);

    const matchPerformanceData = {
        headers: allMatchPerformanceTableHeaders,
        rows: filterMatchPerformancesByCompetitions(rowData, competitionNames)
    };

    const chartData = [{
        name: 'Match Rating',
        data: filterMatchRatingsByCompetitions(competitions, competitionNames)
    }];

    return (
        <Grid container spacing={2}>
            <Grid item xs={6}>
                <TableFilterControl
                    currentValues={ competitionNames }
                    handleChangeFn={ handleChange }
                    allPossibleValues={ allCompetitionNames }
                    allValuesSelectedLabel='All Competitions'
                    inputLabelText='Filter Competitions'
                    labelIdFragment='filter-competitions'
                />
            </Grid>
            <Grid item xs={12}>
                <ReactApexChart
                    options={ getOptions(useGlobalChartOptions(), chartTitle) }
                    series={ chartData }
                    type='bar'
                    height={500}
                />
            </Grid>
            <Grid item xs={12}>
                <SortableTable { ...matchPerformanceData } />
            </Grid>
        </Grid>
    );
}

MatchPerformanceView.propTypes = {
    playerPerformance: PropTypes.shape({
        competitions: PropTypes.arrayOf(PropTypes.shape({
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
            matchRatingHistory: PropTypes.arrayOf(PropTypes.number),
        }))
    })
};