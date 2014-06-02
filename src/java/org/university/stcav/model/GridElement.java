/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.university.stcav.model;

/**
 *
 * @author Administrador
 */
public class GridElement {
private String ubication;
private String name;

    public GridElement() {
    }

    public GridElement(String ubication, String name) {
        this.ubication = ubication;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

}
