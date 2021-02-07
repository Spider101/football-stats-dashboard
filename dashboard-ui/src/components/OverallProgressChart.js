import React from 'react';
import PropTypes from 'prop-types';
import ReactApexChart from 'react-apexcharts';

import { useGlobalChartOptions } from '../context/chartOptionsProvider';

export default function OverallProgressChart({ overallData }) {
    const chartTitle = 'Player Overall Progression over last 6 months';
    const globalChartOptions = useGlobalChartOptions();

    return (
        <ReactApexChart 
            options={{
                ...globalChartOptions,
                stroke: {
                    ...globalChartOptions.stroke,
                    curve: 'straight'
                },
                dataLabels: { enabled: false },
                plotOptions: { bar: { columnWidth: '15%' } },
                title: {
                    ...globalChartOptions.title,
                    text: chartTitle
                },
                xaxis: {
                    title: { text: 'Months', style: { fontFamily: 'Roboto' } },
                    categories: [1, 2, 3, 4, 5, 6]
                }
            }}
            series={ overallData }
            type='bar'
            height={ 500 }
        />
    );
}

OverallProgressChart.propTypes = {
    overallData: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        data: PropTypes.arrayOf(PropTypes.number)
    }))
};