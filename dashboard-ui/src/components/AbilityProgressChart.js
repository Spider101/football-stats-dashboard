import PropTypes from 'prop-types';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { useTheme } from '@material-ui/core/styles';

import { MONTHS } from '../constants';
import CustomToolTip from './CustomToolTip';

const transformAbilityData = abilityHistory => abilityHistory.map(ability => ({ playerAbility: ability }));

export default function AbilityProgressChart({ abilityData }) {
    // const chartTitle = 'Player ability Progression over last 6 months';
    const theme = useTheme();

    const chartData = transformAbilityData(abilityData.history);

    return (
        <ResponsiveContainer width='100%' height={500}>
            <BarChart data={chartData} barSize={50}>
                <XAxis
                    axisLine={false}
                    tickLine={false}
                    tickFormatter={number => MONTHS[number]}
                    domain={['0', '11']}
                />
                <YAxis axisLine={false} tickLine={false} />
                <Tooltip content={<CustomToolTip />} cursor={false}/>
                <CartesianGrid vertical={false} opacity={0.5} />
                <Bar dataKey='playerAbility' fill={theme.palette.primary.main}/>
            </BarChart>
        </ResponsiveContainer>
    );
}

AbilityProgressChart.propTypes = {
    abilityData: PropTypes.shape({
        name: PropTypes.string,
        history: PropTypes.arrayOf(PropTypes.number)
    })
};