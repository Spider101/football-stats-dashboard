import PropTypes from 'prop-types';
import {
    Area,
    AreaChart,
    Bar,
    BarChart,
    CartesianGrid,
    Cell,
    Line,
    LineChart,
    Pie,
    PieChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from 'recharts';

import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';

import { getStrokeColor } from '../utils';

const getChartFromType = (type, chartData, options = {}) => {
    const { dataKeys, barSize, fillOpacity } = options;
    console.log(chartData);

    switch (type) {
    case 'bar':
        return (
            <BarChart data={chartData} barSize={barSize}>
                <Tooltip />
                <XAxis axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <CartesianGrid vertical={false} opacity={0.5} />
                {dataKeys.map((dataKey, idx) => (
                    <Bar key={dataKey} dataKey={dataKey} fill={getStrokeColor(idx)}/>
                ))}
            </BarChart>
        );
    case 'line':
        return (
            <LineChart data={chartData}>
                <Tooltip />
                <XAxis axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <CartesianGrid vertical={false} opacity={0.5} />
                {dataKeys.map((dataKey, idx) => (
                    <Line key={dataKey} dataKey={dataKey} type='monotone' stroke={getStrokeColor(idx)}/>
                ))}
            </LineChart>
        );
    case 'area':
        return (
            <AreaChart data={chartData}>
                <Tooltip />
                <XAxis axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <CartesianGrid vertical={false} opacity={0.5} />
                {dataKeys.map((dataKey, idx) => (
                    <Area
                        key={dataKey}
                        dataKey={dataKey}
                        type='monotone'
                        stroke={getStrokeColor(idx)}
                        fill={getStrokeColor(idx)}
                        fillOpacity={fillOpacity}
                    />
                ))}
            </AreaChart>
        );
    case 'donut':
        return (
            <PieChart>
                <Tooltip />
                <Pie data={chartData} dataKey={dataKeys[0]} innerRadius={100} outerRadius={200}>
                    {chartData.map((entry, idx) => (
                        <Cell key={entry.name} fill={getStrokeColor(idx)} />
                    ))}
                </Pie>
            </PieChart>
        );
    default:
        throw new Error('No Chart supported for type: ' + type);
    }
};

export default function CardWithChart({ cardTitle, chartData, chartOptions, chartType }) {
    return (
        <Card>
            <CardHeader title={ cardTitle }/>
            <CardContent style={{ paddingTop: 0, paddingBottom: 0}}>
                <ResponsiveContainer width='100%' height={chartOptions.height}>
                    {getChartFromType(chartType, chartData, chartOptions)}
                </ResponsiveContainer>
            </CardContent>
        </Card>
    );
}

CardWithChart.propTypes = {
    cardTitle: PropTypes.string,
    chartData: PropTypes.array,
    chartOptions: PropTypes.object,
    chartType: PropTypes.string,
};