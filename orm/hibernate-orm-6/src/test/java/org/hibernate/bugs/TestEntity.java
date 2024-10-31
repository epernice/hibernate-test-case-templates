package org.hibernate.bugs;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Table(name = "HHH_TEST")
@Entity
public class TestEntity {
    @Id
    private int id;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(updatable = false)
    private String foo;

    public void setId(int id) {
        this.id = id;
    }

    public String getFoo() {
        return foo;
    }
}
