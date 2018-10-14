package kz.scope.hiremeserver.model.audit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy

import javax.persistence.MappedSuperclass

/**
 * Created by scope team on 19/08/17.
 */

@MappedSuperclass
@JsonIgnoreProperties(value = ["createdBy", "updatedBy"], allowGetters = true)
abstract class UserDateAudit : DateAudit() {

    @CreatedBy
    var createdBy: Long = 0

    @LastModifiedBy
    var updatedBy: Long = 0
}
