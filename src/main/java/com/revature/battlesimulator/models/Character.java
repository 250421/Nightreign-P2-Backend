package com.revature.battlesimulator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long character_id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "origin", nullable = false)
    private String origin;
}
