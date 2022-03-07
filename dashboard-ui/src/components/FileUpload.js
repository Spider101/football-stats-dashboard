import PropTypes from 'prop-types';

import { Box, LinearProgress, TextField, Typography, useTheme, withStyles } from '@material-ui/core';
import { capitalizeLabel } from '../utils';
import { caseFormat } from '../constants';

export default function FileUpload({ name, id, progress, errorMessage, fileKey, handleChangeFn }) {
    const theme = useTheme();
    return (
        <>
            <TextField
                name={name}
                label={capitalizeLabel(id, caseFormat.CAMEL_CASE)}
                required
                id={id}
                type='file'
                margin='normal'
                fullWidth
                InputLabelProps={{ shrink: true }}
                onChange={e => handleChangeFn(e)}
                error={!!errorMessage}
                helperText={errorMessage}
            />
            {progress > 0 && <BorderLinearProgress value={progress} variant='determinate' />}
            {fileKey && (
                <Box color={ theme.palette.type === 'light' ? 'success.dark' : 'success.light' }>
                    <Typography variant='caption'>{fileKey} uploaded successfully</Typography>
                </Box>
            )}
        </>
    );
}

const BorderLinearProgress = withStyles(theme => ({
    root: {
        borderRadius: 5
    },
    colorPrimary: {
        backgroundColor: theme.palette.grey[theme.palette.type === 'light' ? 200 : 700]
    },
    bar: {
        borderRadius: 5,
        backgroundColor: '#1a90ff'
    }
}))(LinearProgress);

FileUpload.propTypes = {
    name: PropTypes.string,
    id: PropTypes.string,
    progress: PropTypes.number,
    fileKey: PropTypes.string,
    errorMessage: PropTypes.string,
    handleChangeFn: PropTypes.func
};