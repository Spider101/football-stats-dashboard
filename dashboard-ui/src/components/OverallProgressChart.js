import PropTypes from 'prop-types';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { MONTHS } from '../constants';

import { getStrokeColor } from '../utils';

const transformOverallData = overallHistory => overallHistory.map(playerOverall => ({ playerAbility: playerOverall }));

export default function OverallProgressChart({ overallData }) {
    // const chartTitle = 'Player Overall Progression over last 6 months';

    const chartData = transformOverallData(overallData.history);
    return (
        <ResponsiveContainer width='100%' height={500}>
            <BarChart data={chartData} barSize={50}>
                <XAxis axisLine={false} tickLine={false} tickFormatter={number => MONTHS[number]}/>
                <YAxis axisLine={false} tickLine={false} />
                <Tooltip />
                <CartesianGrid vertical={false} opacity={0.5} />
                <Bar dataKey='playerAbility' fill={getStrokeColor(0)}/>
            </BarChart>
        </ResponsiveContainer>
    );
}

OverallProgressChart.propTypes = {
    overallData: PropTypes.shape({
        name: PropTypes.string,
        history: PropTypes.arrayOf(PropTypes.number)
    })
};