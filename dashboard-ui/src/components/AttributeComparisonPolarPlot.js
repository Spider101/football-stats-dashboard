import PropTypes from 'prop-types';
import ReactApexChart from 'react-apexcharts';

import { useGlobalChartOptions } from '../context/chartOptionsProvider';
import { playerAttributes } from '../utils';

export default function AttributeComparisonPolarPlot({ playersWithAttributes }) {
    const globalChartOptions = useGlobalChartOptions();

    const options = {
        ...globalChartOptions,
        chart: { type: 'radar', toolbar: { show: false } },
        fill: { opacity: 0.2 },
        xaxis: {
            labels: { style: { fontSize: '14px' } },
            categories: playerAttributes.GROUPS
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

    const series = playersWithAttributes.map(player => ({
        name: player.name,
        data: playerAttributes.GROUPS.map(groupName => {
            const attributeGroup = player.attributes.find(attribute => attribute.groupName === groupName);
            const attributesInGroup = attributeGroup?.attributesInGroup || [];
            const attributesGroupTotal = attributesInGroup.reduce((a, b) => a + b, 0);
            return attributesGroupTotal === 0
                ? attributesGroupTotal
                : Math.round(attributesGroupTotal / attributesInGroup.length);
        })
    }));

    return <ReactApexChart options={options} series={series} type='radar' />;
}

AttributeComparisonPolarPlot.propTypes = {
    playersWithAttributes: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string,
            attributes: PropTypes.arrayOf(
                PropTypes.shape({
                    groupName: PropTypes.string,
                    attributesInGroup: PropTypes.array
                })
            )
        })
    )
};
