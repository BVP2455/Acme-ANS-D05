
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.leg.Leg;
import acme.entities.trackingLog.TrackingLog;

@Repository
public interface ClaimRepository extends AbstractRepository {

	@Query("Select c from Claim c where c.id=:claimId")
	Claim findClaimById(int claimId);

	@Query("Select c from Claim c where c.assistanceAgent.id=:assistanceAgentId")
	List<Claim> findClaimsByAssistanceAgent(int assistanceAgentId);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegByLegId(int legId);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false")
	Collection<Leg> findAllLegPublish();

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLeg();

	@Query("select tl FROM TrackingLog tl where tl.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("SELECT c from Claim c where c.leg.id = :id")
	Collection<Claim> findClaimsByLegId(int id);

}
