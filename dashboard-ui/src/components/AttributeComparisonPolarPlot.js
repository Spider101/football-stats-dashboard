import PropTypes from 'prop-types';
import {
    Legend,
    PolarAngleAxis,
    PolarGrid,
    PolarRadiusAxis,
    Radar,
    RadarChart,
    ResponsiveContainer,
    Tooltip
} from 'recharts';
import { useTheme } from '@material-ui/core/styles';

import { playerAttributes } from '../constants';
import { MAX_ATTR_VALUE } from '../stories/utils/storyDataGenerators';
import { buildChartPalette } from '../utils';
import CustomToolTip from './CustomToolTip';

const transformGroupedAttributeData = playersWithGroupedAttributeData =>
    playerAttributes.GROUPS.map(groupName => {
        const attributeGroupData = Object.fromEntries(
            playersWithGroupedAttributeData.map(playerData => {
                const attributeGroup = playerData.attributes.find(attribute => attribute.groupName === groupName);
                const attributesInGroup = attributeGroup?.attributesInGroup || [];
                const attributeTotalByGroup = attributesInGroup.reduce((a, b) => a + b, 0);
                const meanAttributeByGroup =
                    attributeTotalByGroup === 0
                        ? attributeTotalByGroup
                        : Math.round(attributeTotalByGroup / attributesInGroup.length);
                return [playerData.name, meanAttributeByGroup];
            })
        );
        return {
            groupName,
            ...attributeGroupData
        };
    });

export default function AttributeComparisonPolarPlot({ playersWithAttributes }) {
    const theme = useTheme();
    const numGroups = playerAttributes.GROUPS.length;
    const polarAxisAngle = 90 - (360/numGroups);

    const chartData = transformGroupedAttributeData(playersWithAttributes);
    const { getPaletteColor } = buildChartPalette(theme);
    return (
        <ResponsiveContainer width='100%' height={500}>
            <RadarChart data={chartData} outerRadius='100%' cy='60%'>
                <PolarGrid />
                <PolarRadiusAxis angle={polarAxisAngle} domain={[0, MAX_ATTR_VALUE]} />
                <PolarAngleAxis dataKey='groupName' />
                {playersWithAttributes.map((playerData, idx) => (
                    <Radar
                        key={playerData.name}
                        dataKey={playerData.name}
                        name={playerData.name}
                        stroke={getPaletteColor(idx)}
                        fill={getPaletteColor(idx)}
                        fillOpacity={0.6}
                    />
                ))}
                <Legend wrapperStyle={{bottom: -50}}/>
                <Tooltip content={<CustomToolTip />}/>
            </RadarChart>
        </ResponsiveContainer>
    );
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