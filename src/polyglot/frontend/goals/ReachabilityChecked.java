/*
 * ReachabilityChecked.java
 * 
 * Author: nystrom
 * Creation date: Oct 11, 2005
 */
package polyglot.frontend.goals;

import java.util.*;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.TypeSystem;
import polyglot.visit.ReachChecker;

public class ReachabilityChecked extends VisitorGoal {
    public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
        return scheduler.internGoal(new ReachabilityChecked(job, ts, nf));
    }

    protected ReachabilityChecked(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, new ReachChecker(job, ts, nf));
    }

    public Collection prerequisiteGoals(Scheduler scheduler) {
        List l = new ArrayList();
        l.addAll(super.prerequisiteGoals(scheduler));
        l.add(scheduler.TypeChecked(job));
        l.add(scheduler.ConstantsChecked(job));
        return l;
    }
}
