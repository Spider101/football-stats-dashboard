import PropTypes from 'prop-types';
import { CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { CHART_ANIMATION_THRESHOLD, months } from '../constants';

import { getStrokeColor } from '../utils';

const transformAttributeData = attributeData => {
    // assuming each attribute has the name number of historical data points (months)
    const numMonths = attributeData[0].history.length;
    return [...Array(numMonths)].map((_, idx) =>
        Object.fromEntries(
            attributeData.map(attribute => [attribute.name, attribute.history[idx]])
        )
    );
};

export default function AttributeProgressChart({ attributeData }) {
    // const chartTitle = 'Player Attribute Progression over last 6 months';

    const attributeNames = attributeData.map(attribute => attribute.name);
    const chartData = transformAttributeData(attributeData);
    return (
        <ResponsiveContainer height={500} width='100%'>
            <LineChart data={chartData}>
                {attributeNames.map((attributeName, idx) => (
                    <Line
                        type='monotone'
                        key={attributeName}
                        dataKey={attributeName}
                        stroke={getStrokeColor(idx)}
                        isAnimationActive={attributeNames.length <= CHART_ANIMATION_THRESHOLD}
                    />
                ))}
                {/* figure out how to layer this on top of the chart */}
                {/* <text x={500 / 2} y={20} fill="black" textAnchor="middle" dominantBaseline="central">
                    <tspan fontSize="14">{chartTitle}</tspan>
                </text> */}
                <XAxis axisLine={false} tickFormatter={number => months[number]} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <Tooltip />
                <Legend />
                <CartesianGrid vertical={false} opacity={0.5} />
            </LineChart>
        </ResponsiveContainer>
    );
}

AttributeProgressChart.propTypes = {
    attributeData: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        history: PropTypes.arrayOf(PropTypes.number)
    }))
};