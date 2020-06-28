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
    listGroupRoot: {
        width: "100%",
        backgroundColor: theme.palette.background.paper
    },
    nestedListItems: {
        paddingLeft: theme.spacing(4),
    },
}));

export default function MenuItemGroup({ menuGroup: { id, menuItems, groupTitle, isCollapsed },
                                          onCollapseMenuItemGroup}) {
    const classes = useStyles();

    return (
      <List key={ id } className={ classes.listGroupRoot } component="nav">
          <ListItem button onClick={ () => onCollapseMenuItemGroup(id) }>
              <ListItemText primary={ groupTitle }/>
              { isCollapsed ? <ExpandLess /> : <ExpandMore />}
          </ListItem>
          <Collapse in={ !isCollapsed } timeout="auto" unmountOnExit>
            <List component="div" disablePadding>
                { menuItems.map(menuItem => <MenuItem { ...menuItem }  clsName={ classes.nestedListItems } /> ) }
            </List>
          </Collapse>
      </List>
    );
}

MenuItemGroup.propTypes = {
  menuGroup: PropTypes.shape({
      id: PropTypes.string,
      menuItems: PropTypes.arrayOf(PropTypes.shape(MenuItem.propTypes)),
      groupTitle: PropTypes.string,
      isCollapsed: PropTypes.bool,
  }),
  onSelectTask: PropTypes.func
};
