import React from "react";
import PropTypes from "prop-types";

import MenuItem from "../components/MenuItem";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Collapse from '@material-ui/core/Collapse';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
    root: {
        width: "100%",
        maxWidth: 360,
        backgroundColor: theme.palette.background.paper
    },
    nested: {
        paddingLeft: theme.spacing(4),
    },
}));

export default function MenuGroup({ menuGroup: { menuItems, groupTitle, isCollapsed }}) {
    const classes = useStyles();
    return (
      <List className={ classes.root } component="nav">
          <ListItem button>
              <ListItemText primary={ groupTitle }/>
              { isCollapsed ? <ExpandLess /> : <ExpandMore />}
          </ListItem>
          <Collapse in={ !isCollapsed } timeout="auto" unmountOnExit>
            <List component="div" disablePadding>
                { menuItems.map(menuItem =>
                    <MenuItem clsName={ classes.nested } text={ menuItem.text } icon={ menuItem.icon }/> ) }
            </List>
          </Collapse>
      </List>
    );
}

MenuGroup.propTypes = {
  menuGroup: PropTypes.shape({
      menuItems: PropTypes.arrayOf(PropTypes.shape(MenuItem.propTypes)),
      groupTitle: PropTypes.string,
      isCollapsed: PropTypes.bool
  })
};
