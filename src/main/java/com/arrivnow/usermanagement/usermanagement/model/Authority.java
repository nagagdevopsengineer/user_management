package com.arrivnow.usermanagement.usermanagement.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * An authority (a security role) used by Spring Security.
 */

@Entity
@Table(name = "authority")
@Data
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    
    @NotNull
    @Size(max = 50)
    @Id
    private String name;

   

   
}
