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

package polyglot.util;

/** Exception thrown when the compiler is confused. */
public class InternalCompilerError extends RuntimeException
{
    protected Position pos;

    public InternalCompilerError(String msg) {
        this(msg, (Position)null);
    }

    public InternalCompilerError(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public InternalCompilerError(String msg, Throwable cause) {
        this(msg, null, cause);
    }

    public InternalCompilerError(Position position, String msg) {
	this(msg, position); 
    }

    public InternalCompilerError(String msg, Position position) {
        super(msg); 
        pos = position;
    }
    public InternalCompilerError(String msg, Position position, Throwable cause) {
        super(msg, cause); 
        pos = position;
    }

    public void setPosition(Position pos) {
	this.pos = pos;
    }

    public Position position() {
	return pos;
    }

    public String message() {
	return super.getMessage();
    }

    public String getMessage() {
	return pos == null ? message() : pos + ": " + message();
    }
}