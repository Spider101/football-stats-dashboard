import React from 'react';
import PropTypes from 'prop-types';
import ReactApexChart from 'react-apexcharts';

const options = {
    chart: { type: 'radar', toolbar: { show: false } },
    stroke: { width: 2 },
    fill: { opacity: 0.2 },
    legend: { show: false },
    xaxis: {
        labels: { style: { fontSize: '14px' } },
        categories: [ 'Defense', 'Mental', 'Physical', 'Attack', 'Technical' ]
    },
    plotOptions: {
        radar: {
            polygons: {
                strokeColors: '#e9e9e9',
                fill: { colors: ['#f8f8f8', '#fff'] }
            }
        }
    },
};

export default function AttributeComparisonPolarPlot({ playerAttributes }) {
    return (
      <ReactApexChart
          options={ options }
          series={ playerAttributes }
          type='radar'
      />
    );
};

AttributeComparisonPolarPlot.propTypes = {
    playerAttributes: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        data: PropTypes.array
    }))
};
