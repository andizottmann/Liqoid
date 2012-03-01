/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.quadrillenschule.liquidroid.model;

import java.util.ArrayList;

/**
 *
 * @author andi
 */
 public class Areas extends ArrayList<Area> {

        public Area getByName(String searchname) {
            for (Area area : this) {
                if (area.getName().equals(searchname)) {
                    return area;
                }
            }
            return null;
        }

          public Area getById(int search) {
            for (Area area : this) {
                if (area.getId()==search) {
                    return area;
                }
            }
            return null;
        }
     
    }
