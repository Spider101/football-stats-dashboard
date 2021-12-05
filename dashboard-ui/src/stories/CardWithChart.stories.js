import ReactApexChart from 'react-apexcharts';
import CardWithChart from '../widgets/CardWithChart';

import { getPlayerProgressionData, MAX_ATTR_VALUE, MAX_OVERALL_VALUE } from './utils/storyDataGenerators';

export default {
    component: CardWithChart,
    title: 'Widgets/ClubPageView/CardWithChart',
    argTypes: {
        children: { table: { disable: true } }
    },
    parameters: {
        docs: {
            description: {
                component: 'Widget for housing a chart element. The chart element and associated options are'
                + ' dynamically passed into the widget.'
            }
        }
    }
};

const baseCardWithChartArgs = {
    dataTransformer: x => x,
    chartOptions: {
        stroke: { width: 2, curve: 'straight' },
        legend: { show: false },
        xaxis: {
            title: { text: 'Months', style: { fontFamily: 'Roboto' } },
            categories: [1, 2, 3, 4, 5, 6]
        }
    }
};

const Template = args => (
    <CardWithChart { ...args }>
        <ReactApexChart />
    </CardWithChart>
);
export const LineChart = Template.bind({});
LineChart.args = {
    ...baseCardWithChartArgs,
    cardTitle: 'Demo Line Chart in Card',
    chartData: getPlayerProgressionData(10, null, MAX_ATTR_VALUE),
    chartType: 'line'
};

export const BarChart = Template.bind({});
BarChart.args = {
    ...baseCardWithChartArgs,
    cardTitle: 'Demo Bar Chart in Card',
    chartData: getPlayerProgressionData(1, 'Player Ability', MAX_OVERALL_VALUE),
    chartOptions: {
        ...baseCardWithChartArgs.chartOptions,
        plotOptions: { bar: { columnWidth: '15%' } },
        dataLabels: { enabled: false },
    },
    chartType: 'bar'
};