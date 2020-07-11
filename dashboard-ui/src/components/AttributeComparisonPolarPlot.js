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
    const series = playerAttributes.map(player => ({
        name: player.name,
        data: player.attributes.map(attrGroup =>
            Math.round(attrGroup.groupAttrValues.reduce((a, b) => a + b, 0) / attrGroup.groupAttrValues.length))
    }));

    return (
        <ReactApexChart
            options={ options }
            series={ series }
            type='radar'
        />
    );
}

AttributeComparisonPolarPlot.propTypes = {
    playerAttributes: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        attributes: PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.shape({
            groupName: PropTypes.string,
            groupAttrValues: PropTypes.array
        })))
    }))
};
