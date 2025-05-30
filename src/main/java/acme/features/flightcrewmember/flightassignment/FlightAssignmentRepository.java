
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.flightassignment.FlightCrewDuty;
import acme.entities.leg.Leg;
import acme.realms.flightcrewmember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa WHERE fa.leg.scheduledArrival < CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findCompletedFlightAssignments();

	@Query("select fa from FlightAssignment fa WHERE fa.leg.scheduledDeparture > CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findPlannedFlightAssignments();

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.leg.scheduledArrival < CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findCompletedFlightAssignmentsByMemberId(final int flightCrewMemberId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.leg.scheduledDeparture > CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findPlannedFlightAssignmentsByMemberId(final int flightCrewMemberId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false")
	Collection<Leg> findAllLegs();

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.scheduledDeparture > :date AND l.scheduledArrival > :date")
	Collection<Leg> findAllFutureLegs(Date date);

	@Query("SELECT fcm FROM FlightCrewMember fcm")
	Collection<FlightCrewMember> findAllFlightCrewMembers();

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegById(int legId);

	@Query("SELECT fcm FROM FlightCrewMember fcm WHERE fcm.id = :flightCrewMemberId")
	FlightCrewMember findFlightCrewMemberById(int flightCrewMemberId);

	@Query("SELECT fa.leg FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId ORDER BY fa.leg.scheduledDeparture ASC")
	List<Leg> findLegsByMemberId(int flightCrewMemberId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.leg.scheduledDeparture < :scheduledArrival AND fa.leg.scheduledArrival > :scheduledDeparture AND fa.flightCrewMember.id = :flightCrewMemberId AND fa.draftMode = false")
	List<FlightAssignment> findflightAssignmentsWithOverlappedLegsByMemberId(Date scheduledDeparture, Date scheduledArrival, int flightCrewMemberId);

	@Query("SELECT count(fa) from FlightAssignment fa WHERE fa.leg.id = :legId and fa.duty = :duty and fa.id != :id and fa.draftMode = false")
	int hasDutyAssigned(int legId, FlightCrewDuty duty, int id);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.leg.id = :id")
	Collection<FlightAssignment> findFlightAssignmentByLegId(int id);

	@Query("SELECT al FROM ActivityLog al WHERE al.activityLogAssignment.id = :activityLogAssignmentId")
	Collection<ActivityLog> findAllLogsByAssignmentId(int activityLogAssignmentId);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.id = :legId")
	Leg findPublishedLegById(int legId);

}
