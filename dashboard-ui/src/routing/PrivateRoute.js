import { Route, Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';

import { useCurrentClub } from '../context/clubProvider';

export default function PrivateRoute({ component: Component, ...rest }) {
    // TODO: pass a access flag instead of tight coupling with club page visibility logic
    const { currentClubId } = useCurrentClub();
    return (
        <Route
            {...rest}
            render={() => {
                if (currentClubId) {
                    return <Component />;
                }

                return <Redirect to='/' />;
            }}
        />
    );
}

PrivateRoute.propTypes = {
    component: PropTypes.node
};
