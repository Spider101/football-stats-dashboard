import React from "react";
import PropTypes from "prop-types";

import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";

export default function MenuItem({ text, icon, clsName = null, onSelectMenuItem }) {
    return (
        <ListItem button className={ clsName } onClick={() => onSelectMenuItem()}>
            <ListItemIcon>
                { icon }
            </ListItemIcon>
            <ListItemText primary={text} />
        </ListItem>
    );
}

MenuItem.propTypes = {
    text: PropTypes.string,
    icon: PropTypes.elementType,
    onSelectTask: PropTypes.func
};
