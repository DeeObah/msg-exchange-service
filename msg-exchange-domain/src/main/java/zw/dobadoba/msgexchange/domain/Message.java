package zw.dobadoba.msgexchange.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import zw.dobadoba.msgexchange.domain.utils.DomainKeyGenerator;
import zw.dobadoba.msgexchange.domain.utils.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by dobadoba on 7/8/17.
 */
@Entity
@Table(name="messages")
@Data
@NoArgsConstructor
public class Message implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Id
    private Long id;
    @Version
    private long version;
    @Column(nullable = false,unique = true)
    private String ref;
    @Column(nullable = false)
    private String source;
    @Column(nullable = false, name = "message",columnDefinition="LONGTEXT")
    private String payload;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false)
    private LocalDateTime dateCreated;
    private LocalDateTime dateLastUpdated;

    @PrePersist
    protected void init() {
        dateCreated = LocalDateTime.now();
        dateLastUpdated = LocalDateTime.now();
        if(id == null || id == 0l) {
            id = DomainKeyGenerator.getKey();
        }
    }
}
