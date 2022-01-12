import { useEffect, useRef, useState } from 'react';
import PropTypes from 'prop-types';

import Slider from '@material-ui/core/Slider';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import { capitalizeLabel, formatNumberWithCommas } from '../utils';
import { makeStyles } from '@material-ui/core/styles';

const getSplitComponents = (total, percentage) => (
    [
        Math.trunc(total * percentage / 100),
        Math.trunc(total * (1 - percentage / 100))
    ]
);

const useStyles = makeStyles(theme => ({
    container: {
        padding: theme.spacing(1),
        borderStyle: 'dashed',
        borderWidth: '1px',
        borderRadius: '5px',
        borderColor: theme.palette.divider,
        backgroundColor: theme.palette.background.paper
    }
}));
const INITIAL_PERCENTAGE = 100;
export default function CustomSlider({ sliderTitle, splitMetadata: { valueToSplit, entitiesToSplit } }) {
    const classes = useStyles();
    const isFirstUpdate = useRef(true);
    const [sliderData, setSliderData] = useState({
        currentPercentage: INITIAL_PERCENTAGE,
        components: getSplitComponents(valueToSplit, INITIAL_PERCENTAGE)
    });

    const handleChangeFn = (_, newValue) => {
        const updatedComponents = getSplitComponents(valueToSplit, newValue);
        setSliderData({
            currentPercentage: newValue,
            components: updatedComponents
        });
        updatedComponents.forEach((component, idx) => {
            entitiesToSplit[idx].handleChange({ target: { name: entitiesToSplit[idx].name, value: component }});
        });
    };

    // update the slider data when the valueToSplit prop changes
    useEffect(() => {
        // skip updating state and invoking change handlers if this is being called on mount, i.e. not on user input
        if (isFirstUpdate.current) {
            isFirstUpdate.current = false;
            return;
        }
        const splitComponents = getSplitComponents(valueToSplit, sliderData.currentPercentage);
        setSliderData({
            ...sliderData,
            components: splitComponents
        });
        splitComponents.forEach((component, idx) => {
            entitiesToSplit[idx].handleChange({ target: { name: entitiesToSplit[idx].name, value: component }});
        });
    }, [valueToSplit]);

    return (
        <div className={classes.container}>
            <Typography variant='h6' align='center'>{sliderTitle}</Typography>
            <Grid container spacing={2}>
                <Grid item>
                    <Grid container item direction='column' alignItems='flex-end' spacing={1}>
                        <Grid item>
                            <Typography>${formatNumberWithCommas(sliderData.components[0])}</Typography>
                        </Grid>
                        <Grid item>
                            <Typography>
                                {capitalizeLabel(entitiesToSplit[0].name, 'camelcase')}
                            </Typography>
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item xs>
                    <Slider value={sliderData.currentPercentage} onChange={handleChangeFn} valueLabelDisplay='on' />
                </Grid>
                <Grid item>
                    <Grid container item direction='column' spacing={1}>
                        <Grid item>
                            <Typography>
                                ${formatNumberWithCommas(sliderData.components[1])}
                            </Typography>
                        </Grid>
                        <Grid item>
                            <Typography>
                                {capitalizeLabel(entitiesToSplit[1].name, 'camelcase')}
                            </Typography>
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
        </div>
    );
}

CustomSlider.propTypes = {
    sliderTitle: PropTypes.string,
    splitMetadata: PropTypes.shape({
        valueToSplit: PropTypes.number,
        entitiesToSplit: PropTypes.arrayOf(PropTypes.shape({
            name: PropTypes.string,
            handleChange: PropTypes.func
        }))
    })
};