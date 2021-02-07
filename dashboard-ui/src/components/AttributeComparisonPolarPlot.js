import React from 'react';
import PropTypes from 'prop-types';
import ReactApexChart from 'react-apexcharts';
import { useGlobalChartOptions } from '../context/chartOptionsProvider';

export default function AttributeComparisonPolarPlot({ playerAttributes }) {
    const globalChartOptions = useGlobalChartOptions();

    const options = {
        ...globalChartOptions,
        chart: { type: 'radar', toolbar: { show: false } },
        fill: { opacity: 0.2 },
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
        }
    };

    const series = playerAttributes.map(player => ({
        name: player.name,
        data: player.attributes.map(attrGroup =>
            Math.round(attrGroup.attributesInGroup.reduce((a, b) => a + b, 0) / attrGroup.attributesInGroup.length))
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
        attributes: PropTypes.arrayOf(PropTypes.shape({
            groupName: PropTypes.string,
            attributesInGroup: PropTypes.array
        }))
    }))
};
