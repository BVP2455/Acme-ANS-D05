
package acme.features.customer.booking;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status) {
			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int bookingId = super.getRequest().getData("id", int.class);
			Booking booking = this.repository.findBookingById(bookingId);
			status = status && booking.getCustomer().getId() == customerId;
			status = status && booking.getDraftMode();
		}

		if (status && super.getRequest().getMethod().equals("POST")) {

			Integer flightId = super.getRequest().getData("flight", Integer.class);
			Flight flight = this.repository.findFlightById(flightId);

			Collection<Flight> flights = this.repository.findAllFlights().stream().filter(f -> f.getNumberLegs() != 0).collect(Collectors.toList());
			Collection<Flight> flightsAvaiables = flights.stream().filter(f -> f.getScheduledDeparture().after(MomentHelper.getCurrentMoment()) && !f.getDraftMode()).collect(Collectors.toList());

			if (flightId != 0 && !flightsAvaiables.contains(flight))
				status = false;

			if (flight != null && flight.getDraftMode())
				status = false;
		}
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {

		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flight", "locatorCode", "travelClass", "lastCardNibble");
	}

	@Override
	public void validate(final Booking booking) {

		if (booking.getFlight() != null) {
			boolean laterFlight = MomentHelper.isAfter(booking.getFlight().getScheduledDeparture(), MomentHelper.getCurrentMoment());
			super.state(laterFlight, "flight", "customer.booking.form.error.flightBeforeBooking");

		}
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices travelClasses;

		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Collection<Flight> flights = this.repository.findAllFlights();
		SelectChoices flightChoices = SelectChoices.from(flights, "label", booking.getFlight());

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "price", "lastCardNibble", "id");
		dataset.put("travelClasses", travelClasses);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
