import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Default, NoObjectives, FullObjectives } from '../stories/BoardObjectives.stories';

it('renders successfully', () => {
    render(<Default {...Default.args} />);
    const numObjectives = Default.args.objectives.length;
    const objectiveItemHeadings = screen.getAllByRole('heading');
    expect(objectiveItemHeadings.length).toEqual(numObjectives);

    // using the checkbox and its checked/unchecked status as a proxy for the objective's complete/incomplete state
    const incompleteObjectives = screen.getAllByRole('checkbox', { checked: false });
    expect(incompleteObjectives.length).toEqual(numObjectives);
});

it('should render Save Changes button as disabled when nothing has changed', () => {
    render(<Default {...Default.args} />);
    const saveChangesButton = screen.getByRole('button', { name: 'Save Changes'});
    expect(saveChangesButton).toBeDisabled();
});

it('should render Add Objective button as disabled when objectives limit has been reached', () => {
    render(<FullObjectives {...FullObjectives.args} />);
    const addObjectiveButton = screen.getByRole('button', { name: 'Add Objective'});
    expect(addObjectiveButton).toBeDisabled();
});

it('should render text when no objectives have been added', () => {
    render(<NoObjectives {...NoObjectives.args} />);
    expect(screen.getByText('No Board Objectives have been added yet!')).toBeInTheDocument();
});

it('should mark objective as completed and enable Save Changes button when objective is clicked on', () => {
    render(<Default {...Default.args} />);
    const { title: objectiveTitle } = Default.args.objectives[0];
    const objectiveItemHeading = screen.getByRole('heading', { name: objectiveTitle });

    userEvent.click(objectiveItemHeading);

    // verify that the heading has a strike-through styling and the checkbox is checked
    expect(screen.getByRole('checkbox', { checked: true })).toBeInTheDocument();
    expect(objectiveItemHeading).toHaveStyle({ 'text-decoration': 'line-through' });

    // since there is a change in the state of the objectives, the Save Changes button is expected to not be `disabled`
    const saveChangesButton = screen.getByRole('button', { name: 'Save Changes' });
    expect(saveChangesButton).not.toBeDisabled();
});