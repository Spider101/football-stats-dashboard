import React from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';

const useStyles = makeStyles((theme) => ({
    root: {
        '& .MuiTextField-root': {
            margin: theme.spacing(1),
            width: '25ch',
        },
    },
}));

export default function EditPlayerForm({ attributeCategories, handleChangeFn }) {
    const classes = useStyles();

    return (
        <form className={ classes.root }>
            {
                attributeCategories.map((attributeCategory, _categoryIdx) => {
                    return (
                        <div key={ _categoryIdx }>
                            <Typography variant='h5'>
                                { attributeCategory.categoryName }
                            </Typography>
                            {
                                attributeCategory.attributesInCategory.map((attribute, _attributeIdx) => {
                                    return (
                                        <TextField
                                            id="standard-number"
                                            label={ attribute.name }
                                            name={ attribute.name }
                                            type="number"
                                            InputLabelProps={{
                                                shrink: true,
                                            }}
                                            value={ attribute.value }
                                            onChange={ (e) => handleChangeFn(e, _categoryIdx, _attributeIdx) }
                                        />
                                    )
                                })
                            }
                        </div>
                    )
                })
            }
        </form>
    );
}

EditPlayerForm.propTypes = {
    attributeCategories: PropTypes.object,
    handleChangeFn: PropTypes.func
};