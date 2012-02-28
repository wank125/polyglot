/*******************************************************************************
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2008 Polyglot project group, Cornell University
 * Copyright (c) 2006-2008 IBM Corporation
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This program and the accompanying materials are made available under
 * the terms of the Lesser GNU Public License v2.0 which accompanies this
 * distribution.
 * 
 * The development of the Polyglot project has been supported by a
 * number of funding sources, including DARPA Contract F30602-99-1-0533,
 * monitored by USAF Rome Laboratory, ONR Grant N00014-01-1-0968, NSF
 * Grants CNS-0208642, CNS-0430161, and CCF-0133302, an Alfred P. Sloan
 * Research Fellowship, and an Intel Research Ph.D. Fellowship.
 *
 * See README for contributors.
 ******************************************************************************/

package polyglot.ext.param.types;

import polyglot.types.TypeSystem_c;
import polyglot.types.*;
import polyglot.util.*;

import java.util.*;

/**
 * Implementation of type system for parameterized types.
 */
public abstract class ParamTypeSystem_c extends TypeSystem_c
    implements ParamTypeSystem
{
    /**
     * Create a new mutable PClass.
     *
     * @param pos The position of the PClass.
     */
    public MuPClass mutablePClass(Position pos) {
        return new MuPClass_c(this, pos);
    }

    /**
     * Instantiate a parametric type on a list of actual parameters.
     *
     * @param pos The position of the instantiated type
     * @param base The parameterized type
     * @param actuals The list of actuals
     *
     * @throws SemanticException when the actuals do not agree with the formals
     */
    public ClassType instantiate(Position pos, PClass base, List actuals) 
        throws SemanticException
    {
        checkInstantiation(pos, base, actuals);
        return uncheckedInstantiate(pos, base, actuals);
    }

    /**
     * Check that an instantiation of a parametric type on a list of actual
     * parameters is legal.
     *
     * @param pos The position of the instantiated type
     * @param base The parameterized type
     * @param actuals The list of actuals
     *
     * @throws SemanticException when the actuals do not agree with the formals
     */
    protected void checkInstantiation(Position pos, PClass base,
        List actuals) throws SemanticException
    {
        if (base.formals().size() != actuals.size()) {
            throw new SemanticException("Wrong number of actual parameters " +
                                        "for instantiation of \"" +
                                        base.clazz() + "\".", pos);
        }
    }

    /**
     * Instantiate a parametric type on a list of actual parameters, but
     * do not check that the instantiation is legal.
     *
     * @param pos The position of the instantiated type
     * @param base The parameterized type
     * @param actuals The list of actuals
     */
    protected ClassType uncheckedInstantiate(Position pos, PClass base,
        List actuals)
    {
        Map substMap = new HashMap();
        Iterator i = base.formals().iterator();
        Iterator j = actuals.iterator();

        while (i.hasNext() && j.hasNext()) {
            Object formal = i.next();
            Object actual = j.next();
            substMap.put(formal, actual);
        }

        if (i.hasNext() || j.hasNext()) {
            throw new InternalCompilerError("Wrong number of actual " +
                "parameters for instantiation " + "of \"" + base + "\".", pos);
        }

        Type inst = subst(base.clazz(), substMap, new HashMap());
        if (!inst.isClass()) {
            throw new InternalCompilerError("Instantiating a PClass "
                + "produced something other than a ClassType.", pos);
        }
        
        return inst.toClass();
    }

    
    /**
     * Apply a parameter substitution to a type.
     *
     * @param t The type on which we perform substitutions.
     * @param substMap Map from formal parameters to actuals; the formals are
     * not necessarily formals of <code>t</code>.
     */
    public Type subst(Type t, Map substMap) {
        return subst(t, substMap, new HashMap());
    }

    /**
     * Apply a parameter substitution to a type.
     *
     * @param t The type on which we perform substitutions.
     * @param substMap Map from formal parameters to actuals; the formals are
     * not necessarily formals of <code>t</code>.
     * @param cache Cache of substitutions performed, implemented as a map from
     * type to substituted type.  This is passed in to ensure pointers to
     * outer classes are substituted correctly.
     */
    public Type subst(Type t, Map substMap, Map cache) {
        return subst(substMap, cache).substType(t);
    }

    /**
     * Create a substitutor.
     *
     * @param substMap Map from formal parameters to actuals; the formals are
     * not necessarily formals of <code>t</code>.
     * @param cache Cache of substitutions performed, implemented as a map from
     * type to substituted type.  This is passed in to ensure pointers to
     * outer classes are substituted correctly.
     */
    public Subst subst(Map substMap, Map cache) {
        return new Subst_c(this, substMap, cache);
    }
}