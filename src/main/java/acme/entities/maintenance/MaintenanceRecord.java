
package acme.entities.maintenance;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidMaintenanceRecord;
import acme.entities.aircraft.Aircraft;
import acme.realms.technician.Technician;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidMaintenanceRecord
@Table(indexes = {
	@Index(columnList = "technician_id"), @Index(columnList = "draftMode")
})

public class MaintenanceRecord extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				maintenanceMoment;

	@Mandatory
	@Valid
	@Automapped
	private MaintenanceStatus	status;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				nextInspectionDue;

	@Mandatory
	@ValidMoney
	@Automapped
	private Money				estimatedCost;

	@Optional
	@ValidString
	@Automapped
	private String				notes;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Relationships -------------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Technician			technician;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft			aircraft;
}
