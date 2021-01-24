import React from 'react';
import PropTypes from 'prop-types';
import ReactApexChart from 'react-apexcharts';

import { useGlobalChartOptions } from '../context/chartOptionsProvider';

// TODO: pass in the line chart title
export default function AttributeProgressChart({ attributeData }) {
    const chartTitle = 'Player Attribute Progression over last 6 months';
    const globalChartOptions = useGlobalChartOptions();

    const chartOptions = {
        ...globalChartOptions,
        // TODO: switch to a more well-defined limit for skipping data animations through load-testing
        chart: { animations: { enabled: attributeData.length <= 10 } },
        stroke: {
            ...globalChartOptions.stroke,
            curve: 'straight'
        },
        title: {
            ...globalChartOptions.title,
            text: chartTitle
        },
        xaxis: {
            title: { text: 'Months', style: { fontFamily: 'Roboto' } },
            categories: [1, 2, 3, 4, 5, 6]
        }
    };

    return (
        <ReactApexChart
            options={ chartOptions }
            series={ attributeData }
            type='line'
            height={ 500 }
        />
    );
}

AttributeProgressChart.propTypes = {
    attributeData: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        data: PropTypes.arrayOf(PropTypes.number)
    }))
};