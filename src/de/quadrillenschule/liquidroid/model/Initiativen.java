/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author andi
 */
public class Initiativen extends ArrayList<Initiative> {

    public Initiativen findByIssueID(int find) {
        Initiativen retval = new Initiativen();
        for (Initiative i : this) {
            if (i.issue_id == find) {
                retval.add(i);
            }
        }
        return retval;
    }
     public Initiativen findByName(String name) {
        Initiativen retval = new Initiativen();
        for (Initiative i : this) {
            if (i.name == name) {
                retval.add(i);
            }
        }
        return retval;
    }
     public void sortById(){
       Collections.reverse(this);
     }
}
