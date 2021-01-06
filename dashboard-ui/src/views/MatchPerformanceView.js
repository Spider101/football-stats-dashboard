import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import SortableTable from '../widgets/SortableTable';
import ReactApexChart from 'react-apexcharts';
import { allMatchPerformanceTableHeaders } from '../utils';

const convertCamelCaseToSnakeCase = (camelCaseString) =>
    camelCaseString.replace(/([a-z])([A-Z])/g, '$1_$2')
        .toLowerCase();

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

const getOptions = chartTitle => ({
    dataLabels: { enabled: false },
    title: { text: chartTitle, align: 'left', style: { fontFamily: 'Roboto' } },
    xaxis: {
        legend: { show: false },
        title: { text: 'Matches', style: { fontFamily: 'Roboto' } },
        categories: [ ...Array(10) ].map((_, _idx) => _idx + 1)
    }
});

export default function MatchPerformanceView({ playerPerformance: { competitions } }) {
    const chartTitle = 'Player Performance over last 10 matches';

    const matchPerformanceData = {
        headers: allMatchPerformanceTableHeaders,
        rows: playerPerformance.competitions
            .map(competition => buildMatchPerformanceData(competition))
    };

    const chartData = [{
        name: 'Match Rating',
        data: playerPerformance.competitions.map(competition => competition.matchRatingHistory).flat()
    }];

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <ReactApexChart
                    options={ getOptions(chartTitle) }
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