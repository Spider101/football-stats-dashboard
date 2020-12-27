import React from 'react';
import PropTypes from 'prop-types';
import ReactApexChart from 'react-apexcharts';

const getOptions = (chartTitle) => ({
    stroke: { width: 2, curve: 'straight' },
    legend: { show: false },
    title: { text: chartTitle, align: 'left', style: { fontFamily: 'Roboto' } },
    xaxis: {
        title: { text: 'Months', style: { fontFamily: 'Roboto' } },
        categories: [1, 2, 3, 4, 5, 6]
    }
});

// TODO: pass in the line chart title
export default function AttributeProgressChart({ attributeData }) {
    const chartTitle = 'Player Attribute Progression over last 6 months';

    return (
        <ReactApexChart
            options={ getOptions(chartTitle) }
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