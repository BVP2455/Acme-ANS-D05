
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.flightassignment.FlightCrewDuty;
import acme.entities.leg.Leg;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		FlightAssignment flightAssignment;
		boolean status;
		int flightAssignmentId;
		int flightCrewMemberId;

		flightAssignmentId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		status = flightAssignment != null && flightAssignment.getFlightCrewMember().getId() == flightCrewMemberId && flightAssignment.isDraftMode();

		if (status) {
			String method;

			method = super.getRequest().getMethod();

			if (method.equals("GET"))
				status = true;
			else {

				Integer legId = super.getRequest().getData("leg", Integer.class);
				Leg leg = this.repository.findPublishedLegById(legId);

				Collection<Leg> legsAvaiables = this.repository.findAllLegs().stream().toList();

				if (legId != 0 && !legsAvaiables.contains(leg))
					status = false;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {

		flightAssignment.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;

		SelectChoices legChoice;
		Collection<Leg> legs;

		SelectChoices flightCrewMemberChoice;
		Collection<FlightCrewMember> flightCrewMembers;

		dutyChoice = SelectChoices.from(FlightCrewDuty.class, flightAssignment.getDuty());
		currentStatusChoice = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());

		flightCrewMembers = this.repository.findAllFlightCrewMembers();
		flightCrewMemberChoice = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdateMoment", "currentStatus", "remarks", "leg", "draftMode");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightCrewMemberChoice);

		super.getResponse().addData(dataset);
	}

}
