import React from 'react';
import ReactApexChart from 'react-apexcharts';
import CardWithChart from '../widgets/CardWithChart';

import { getPlayerProgressionData, MAX_ATTR_VALUE, MAX_OVERALL_VALUE } from './utils/storyDataGenerators';

export default {
    component: CardWithChart,
    title: 'Widgets/HomePageView',
    excludeStories: /.*Data$/,
};

const lineChartData = {
    cardTitle: 'Demo Line Chart in Card',
    chartData: getPlayerProgressionData(10, null, MAX_ATTR_VALUE),
    dataTransformer: x => x,
    chartOptions: {
        stroke: { width: 2, curve: 'straight' },
        legend: { show: false },
        xaxis: {
            title: { text: 'Months', style: { fontFamily: 'Roboto' } },
            categories: [1, 2, 3, 4, 5, 6]
        }
    },
    chartType: 'line'
};

const barChartData = {
    cardTitle: 'Demo Bar Chart in Card',
    chartData: getPlayerProgressionData(1, 'Player Ability', MAX_OVERALL_VALUE),
    dataTransformer: x => x,
    chartOptions: {
        stroke: { width: 2, curve: 'straight' },
        plotOptions: { bar: { columnWidth: '15%' } },
        dataLabels: { enabled: false },
        legend: { show: false },
        xaxis: {
            title: { text: 'Months', style: { fontFamily: 'Roboto' } },
            categories: [1, 2, 3, 4, 5, 6]
        }
    },
    chartType: 'bar'
};

export const LineChart = () => (
    <CardWithChart { ...lineChartData }>
        <ReactApexChart />
    </CardWithChart>
);

export const BarChart = () => (
    <CardWithChart { ...barChartData }>
        <ReactApexChart />
    </CardWithChart>
)