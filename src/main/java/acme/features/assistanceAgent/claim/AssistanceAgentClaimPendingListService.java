
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLogStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPendingListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private ClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findClaimsByAssistanceAgent(assistanceAgentId).stream().filter(x -> x.getStatus() == TrackingLogStatus.PENDING || x.getStatus() == null).toList();

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		TrackingLogStatus status;

		status = claim.getStatus();
		dataset = super.unbindObject(claim, "passengerEmail", "type");
		dataset.put("status", status);
		super.addPayload(dataset, claim, "registrationMoment", "description", "leg.flightNumber");

		super.getResponse().addData(dataset);
	}
}
