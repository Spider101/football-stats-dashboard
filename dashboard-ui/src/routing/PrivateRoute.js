import { Route, Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useUserAuth } from '../context/authProvider';

export default function PrivateRoute({ component: Component, componentProps, ...rest }) {
    // TODO: pass a access flag instead of tight coupling with club page visibility logic
    const { isUserLoggedIn } = useUserAuth();
    return (
        <Route
            {...rest}
            render={props => {
                if (isUserLoggedIn()) {
                    return <Component {...componentProps}/>;
                }

                // eslint-disable-next-line react/prop-types
                return <Redirect to={{ pathname: '/auth/signIn', state: { from: props.location } }}/>;
            }}
        />
    );
}

PrivateRoute.propTypes = {
    component: PropTypes.node,
    componentProps: PropTypes.object
};
