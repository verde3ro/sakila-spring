package mx.edu.upq.model;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The primary key class for the user_role database table.
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRolePK implements Serializable {

	@Serial
	private static final long serialVersionUID = -3547842379354309437L;

	@Column(name = "user_id", insertable = false, updatable = false, unique = true, nullable = false)
	private short userId;

	@Column(name = "role_id", insertable = false, updatable = false, unique = true, nullable = false)
	private short roleId;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof UserRolePK castOther)) {
			return false;
		}
		return (this.userId == castOther.userId) && (this.roleId == castOther.roleId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId;
		hash = hash * prime + this.roleId;

		return hash;
	}

}
