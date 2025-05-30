
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentListCompletedService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	//Internal state ---------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	//AbstractGuiService interface -------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		Collection<FlightAssignment> flightAssignments;

		int flightCrewMemberId;

		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		flightAssignments = this.repository.findCompletedFlightAssignmentsByMemberId(flightCrewMemberId);

		super.getBuffer().addData(flightAssignments);

	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		//unbindObject crea el dataset
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdateMoment", "currentStatus", "draftMode");

		super.addPayload(dataset, flightAssignment, "draftMode");

		super.getResponse().addData(dataset);
	}

}
