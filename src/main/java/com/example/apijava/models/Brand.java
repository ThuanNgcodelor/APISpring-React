package com.example.apijava.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean active;

    public Brand(Long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }
    public Brand() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
