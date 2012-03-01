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

    private ArrayList<Integer> existingIssueIds = new ArrayList<Integer>();

    @Override
    public boolean add(Initiative ini) {
        if (!existingIssueIds.contains((Integer) ini.issue_id)) {
            existingIssueIds.add((Integer) ini.issue_id);
            return super.add(ini);
        }
        return false;
    }

    @Override
    public void clear(){
        existingIssueIds.clear();
        super.clear();

    }

    @Override
    public Initiative remove(int i){
        existingIssueIds.remove((Integer)get(i).issue_id);
        return super.remove(i);
    }



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

    public void sortById() {
        Collections.reverse(this);
    }
}
