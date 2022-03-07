import PropTypes from 'prop-types';

import Box from '@material-ui/core/Box';
import LinearProgress from '@material-ui/core/LinearProgress';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { useTheme, withStyles } from '@material-ui/core/styles';

export default function FileUpload({ TextFieldProps, progress, errorMessage, fileKey, handleChangeFn }) {
    const theme = useTheme();
    return (
        <>
            <TextField
                {...TextFieldProps}
                required
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
    TextFieldProps: PropTypes.shape({
        name: PropTypes.string,
        id: PropTypes.string,
        label: PropTypes.string,
    }),
    progress: PropTypes.number,
    fileKey: PropTypes.string,
    errorMessage: PropTypes.string,
    handleChangeFn: PropTypes.func
};