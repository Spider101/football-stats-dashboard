import PropTypes from 'prop-types';
import { Bar, BarChart, Tooltip, XAxis, YAxis } from 'recharts';
import clsx from 'clsx';

import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import { alpha, makeStyles, useTheme } from '@material-ui/core/styles';

import { MAX_ATTR_VALUE } from '../stories/utils/storyDataGenerators';
import { getFillColorFromTheme } from '../utils';

const useStyles = makeStyles((theme) => ({
    attr: {
        height: '60px'
    },
    highlighted: {
        borderLeft: '2px solid',
        borderLeftColor: theme.palette.primary.main,
        backgroundColor: alpha(theme.palette.primary.main, 0.15)
    },
}));

const transformComparisonItemData = attributeItem => ([{
    name: attributeItem.label,
    ...Object.fromEntries(attributeItem.attrValues.map(attr => [attr.name, ...attr.data]))
}]);


export default function AttributeComparisonItem({ attrComparisonItem: { attrValues, label }, highlightedAttributes }) {
    const classes = useStyles();
    const isHighlighted = highlightedAttributes.includes(label);
    const chartData = transformComparisonItemData({ label, attrValues });
    const theme = useTheme();
    const chartRange = [-MAX_ATTR_VALUE, MAX_ATTR_VALUE];
    return (
        <ListItem
            className={clsx(classes.attr, {
                [classes.highlighted]: isHighlighted
            })}
        >
            <ListItemText primary={label} />
            <BarChart layout='vertical' data={chartData} stackOffset='sign' width={300} height={50}>
                <Tooltip />
                {attrValues.map((attr, idx) => (
                    <Bar
                        isAnimationActive={false}
                        key={attr.name}
                        dataKey={attr.name}
                        stackId='stack'
                        fill={getFillColorFromTheme(theme, idx)}
                    />
                ))}
                <XAxis hide={true} type='number' domain={chartRange}/>
                <YAxis hide={true} dataKey='name' type='category'/>
            </BarChart>
        </ListItem>
    );
}

AttributeComparisonItem.propTypes = {
    attrComparisonItem: PropTypes.shape({
        attrValues: PropTypes.arrayOf(PropTypes.shape({
            name: PropTypes.string,
            data: PropTypes.array
        })),
        label: PropTypes.string
    }),
    highlightedAttributes: PropTypes.arrayOf(PropTypes.string)
};

