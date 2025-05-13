package com.revature.battlesimulator.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @Column(name = "roomid", nullable=false, unique=true)
    private String id;

    @Column(name = "roomname", nullable = false, unique = true)
    private String name;

    @Column(name = "matchInProgress", nullable = false)
    private Boolean matchInProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User lobbyCreator;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }
}