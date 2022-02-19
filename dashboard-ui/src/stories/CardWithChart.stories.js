import { faker } from '@faker-js/faker';

import CardWithChart from '../widgets/CardWithChart';
import { getCardWithChartData, MAX_ATTR_VALUE, MAX_OVERALL_VALUE } from './utils/storyDataGenerators';

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

const Template = args => <CardWithChart {...args} />;

const lineChartDataKeys = [...Array(10)].map(() => faker.hacker.noun());
export const LineChart = Template.bind({});
LineChart.args = {
    cardTitle: 'Demo Line Chart in Card',
    chartData: getCardWithChartData(lineChartDataKeys, 6, MAX_ATTR_VALUE),
    chartOptions: { dataKeys: lineChartDataKeys, height: 500 },
    chartType: 'line'
};

const barChartDataKeys = ['playerAbility'];
export const BarChart = Template.bind({});
BarChart.args = {
    cardTitle: 'Demo Bar Chart in Card',
    chartData: getCardWithChartData(barChartDataKeys, 6, MAX_OVERALL_VALUE),
    chartOptions: { dataKeys: barChartDataKeys, height: 500, barSize: 50 },
    chartType: 'bar'
};

const areaChartDataKeys = ['clubProfit'];
export const AreaChart = Template.bind({});
AreaChart.args = {
    cardTitle: 'Demo Area Chart in Card',
    chartData: getCardWithChartData(areaChartDataKeys, 6, MAX_OVERALL_VALUE),
    chartOptions: { dataKeys: areaChartDataKeys, height: 500, barSize: 50 },
    chartType: 'area'
};