/*******************************************************************************
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2012 Polyglot project group, Cornell University
 * Copyright (c) 2006-2012 IBM Corporation
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
 * monitored by USAF Rome Laboratory, ONR Grants N00014-01-1-0968 and
 * N00014-09-1-0652, NSF Grants CNS-0208642, CNS-0430161, CCF-0133302,
 * and CCF-1054172, AFRL Contract FA8650-10-C-7022, an Alfred P. Sloan 
 * Research Fellowship, and an Intel Research Ph.D. Fellowship.
 *
 * See README for contributors.
 ******************************************************************************/
package polyglot.ext.jl5.ast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorCall.Kind;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.NodeFactory_c;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * NodeFactory for jl5 extension.
 */
public class JL5NodeFactory_c extends NodeFactory_c implements JL5NodeFactory {
    public JL5NodeFactory_c() {
        super(new JL5ExtFactory_c(), JL5Del.instance);
    }

    public JL5NodeFactory_c(JL5ExtFactory extFactory) {
        super(extFactory, JL5Del.instance);
    }

    public JL5NodeFactory_c(JL5ExtFactory extFactory, JL5Del del) {
        super(extFactory, del);
    }

    @Override
    public JL5ExtFactory extFactory() {
        return (JL5ExtFactory) super.extFactory();
    }

    @Override
    public JL5Del lang() {
        return (JL5Del) super.lang();
    }

    @Override
    public CanonicalTypeNode CanonicalTypeNode(Position pos, Type type) {
        if (!type.isCanonical()) {
            throw new InternalCompilerError("Cannot construct a canonical "
                    + "type node for a non-canonical type.");
        }

        return super.CanonicalTypeNode(pos,
                                       JL5CanonicalTypeNodeExt.makeRawIfNeeded(type,
                                                                               pos));
    }

    @Override
    public ClassDecl EnumDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, Id name, TypeNode superType,
            List<TypeNode> interfaces, ClassBody body) {
        ClassDecl n =
                new ClassDecl_c(pos, flags, name, superType, interfaces, body);
        n = (ClassDecl) n.ext(extFactory().extEnumDecl());
        JL5EnumDeclExt ext = (JL5EnumDeclExt) JL5Ext.ext(n);
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return n;

    }

    @Override
    public AnnotationElemDecl AnnotationElemDecl(Position pos, Flags flags,
            TypeNode type, Id name, Term defaultValue) {
        AnnotationElemDecl n =
                new AnnotationElemDecl_c(pos, flags, type, name, defaultValue);
        n = (AnnotationElemDecl) n.ext(extFactory().extAnnotationElemDecl());
        return n;
    }

    @Override
    public AnnotationElem NormalAnnotationElem(Position pos, TypeNode name,
            List<ElementValuePair> elements) {
        AnnotationElem n = new AnnotationElem_c(pos, name, elements);
        n = (AnnotationElem) n.ext(extFactory().extNormalAnnotationElem());
        return n;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AnnotationElem MarkerAnnotationElem(Position pos, TypeNode name) {
        return NormalAnnotationElem(pos, name, Collections.EMPTY_LIST);
    }

    @Override
    public AnnotationElem SingleElementAnnotationElem(Position pos,
            TypeNode name, Term value) {
        List<ElementValuePair> l = new LinkedList<ElementValuePair>();
        l.add(ElementValuePair(pos, this.Id(pos, "value"), value));
        return NormalAnnotationElem(pos, name, l);
    }

    @Override
    public ElementValuePair ElementValuePair(Position pos, Id name, Term value) {
        ElementValuePair n = new ElementValuePair_c(pos, name, value);
        n = (ElementValuePair) n.ext(extFactory().extElementValuePair());
        return n;
    }

    @Override
    public ClassDecl ClassDecl(Position pos, Flags flags, Id name,
            TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
        return ClassDecl(pos,
                         flags,
                         Collections.<AnnotationElem> emptyList(),
                         name,
                         superClass,
                         interfaces,
                         body,
                         Collections.<ParamTypeNode> emptyList());
    }

    @Override
    public ClassDecl ClassDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, Id name, TypeNode superType,
            List<TypeNode> interfaces, ClassBody body,
            List<ParamTypeNode> paramTypes) {
        ClassDecl n =
                super.ClassDecl(pos, flags, name, superType, interfaces, body);
        JL5ClassDeclExt ext = (JL5ClassDeclExt) JL5Ext.ext(n);
        ext.paramTypes =
                (paramTypes == null ? Collections.<ParamTypeNode> emptyList()
                        : paramTypes);
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return n;
    }

    @Override
    public ExtendedFor ExtendedFor(Position pos, LocalDecl decl, Expr expr,
            Stmt stmt) {
        ExtendedFor n = new ExtendedFor_c(pos, decl, expr, stmt);
        n = (ExtendedFor) n.ext(extFactory().extExtendedFor());
        return n;
    }

    @Override
    public EnumConstantDecl EnumConstantDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, Id name, List<Expr> args,
            ClassBody body) {
        EnumConstantDecl n =
                new EnumConstantDecl_c(pos, flags, name, args, body);
        n = (EnumConstantDecl) n.ext(extFactory().extEnumConstantDecl());
        EnumConstantDeclExt ext = (EnumConstantDeclExt) JL5Ext.ext(n);
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return n;
    }

    @Override
    public EnumConstantDecl EnumConstantDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, Id name, List<Expr> args) {
        return EnumConstantDecl(pos, flags, annotations, name, args, null);
    }

    @Override
    public EnumConstant EnumConstant(Position pos, Receiver target, Id name) {
        EnumConstant n = new EnumConstant_c(pos, target, name);
        n = (EnumConstant) n.ext(extFactory().extEnumConstant());
        return n;
    }

    @Override
    public ConstructorCall ConstructorCall(Position pos, Kind kind, Expr outer,
            List<Expr> args) {
        return ConstructorCall(pos, kind, outer, args, false);
    }

    @Override
    public ConstructorCall ConstructorCall(Position pos, Kind kind, Expr outer,
            List<Expr> args, boolean isEnumConstructorCall) {
        return ConstructorCall(pos,
                               kind,
                               null,
                               outer,
                               args,
                               isEnumConstructorCall);
    }

    @Override
    public ConstructorCall ConstructorCall(Position pos, Kind kind,
            List<TypeNode> typeArgs, Expr outer, List<Expr> args,
            boolean isEnumConstructorCall) {

        ConstructorCall n = super.ConstructorCall(pos, kind, outer, args);
        JL5ConstructorCallExt ext = (JL5ConstructorCallExt) JL5Ext.ext(n);
        ext.typeArgs = typeArgs;
        ext.isEnumConstructorCall = isEnumConstructorCall;
        return n;
    }

    @Override
    public ConstructorCall ConstructorCall(Position pos, Kind kind,
            List<TypeNode> typeArgs, Expr outer, List<Expr> args) {
        return ConstructorCall(pos, kind, typeArgs, outer, args, false);
    }

    @Override
    public ConstructorDecl ConstructorDecl(Position pos, Flags flags, Id name,
            List<Formal> formals, List<TypeNode> throwTypes, Block body) {
        ConstructorDecl n =
                ConstructorDecl(pos,
                                flags,
                                null,
                                name,
                                formals,
                                throwTypes,
                                body,
                                null);
        return n;
    }

    @Override
    public ConstructorDecl ConstructorDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, Id name, List<Formal> formals,
            List<TypeNode> throwTypes, Block body,
            List<ParamTypeNode> typeParams) {
        ConstructorDecl n =
                super.ConstructorDecl(pos,
                                      flags,
                                      name,
                                      formals,
                                      throwTypes,
                                      body);
        JL5ConstructorDeclExt ext = (JL5ConstructorDeclExt) JL5Ext.ext(n);
        ext.typeParams =
                (typeParams == null ? Collections.<ParamTypeNode> emptyList()
                        : typeParams);
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return n;
    }

    @Override
    public Disamb disamb() {
        Disamb n = new JL5Disamb_c();
        return n;
    }

    @Override
    public MethodDecl MethodDecl(Position pos, Flags flags,
            TypeNode returnType, Id name, List<Formal> formals,
            List<TypeNode> throwTypes, Block body) {
        MethodDecl n =
                MethodDecl(pos,
                           flags,
                           null,
                           returnType,
                           name,
                           formals,
                           throwTypes,
                           body,
                           null);
        return n;
    }

    @Override
    public MethodDecl MethodDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, TypeNode returnType, Id name,
            List<Formal> formals, List<TypeNode> throwTypes, Block body,
            List<ParamTypeNode> typeParams) {
        MethodDecl n =
                super.MethodDecl(pos,
                                 flags,
                                 returnType,
                                 name,
                                 formals,
                                 throwTypes,
                                 body);
        JL5MethodDeclExt ext = (JL5MethodDeclExt) JL5Ext.ext(n);
        ext.typeParams =
                (typeParams == null ? Collections.<ParamTypeNode> emptyList()
                        : typeParams);
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return n;
    }

    @Override
    public ParamTypeNode ParamTypeNode(Position pos, List<TypeNode> bounds,
            Id id) {
        ParamTypeNode n = new ParamTypeNode_c(pos, bounds, id);
        n = (ParamTypeNode) n.ext(extFactory().extParamTypeNode());
        return n;
    }

    @Override
    public AmbTypeInstantiation AmbTypeInstantiation(Position pos,
            TypeNode base, List<TypeNode> typeArguments) {
        AmbTypeInstantiation n =
                new AmbTypeInstantiation(pos, base, typeArguments);
        n =
                (AmbTypeInstantiation) n.ext(extFactory().extAmbTypeInstantiation());
        return n;
    }

    @Override
    public Formal Formal(Position pos, Flags flags, TypeNode type, Id name) {
        return Formal(pos, flags, null, type, name);
    }

    @Override
    public Formal Formal(Position pos, Flags flags,
            List<AnnotationElem> annotations, TypeNode type, Id name) {
        return Formal(pos, flags, annotations, type, name, false);
    }

    @Override
    public polyglot.ast.Formal Formal(Position pos, Flags flags,
            List<AnnotationElem> annotations, TypeNode type, Id name,
            boolean var_args) {
        Formal f = super.Formal(pos, flags, type, name);
        JL5FormalExt ext = (JL5FormalExt) JL5Ext.ext(f);
        ext.isVarArg = var_args;
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return f;
    }

    @Override
    public AmbWildCard AmbWildCard(Position pos) {
        AmbWildCard n = new AmbWildCard(pos);
        n = (AmbWildCard) n.ext(extFactory().extAmbWildCard());
        return n;
    }

    @Override
    public AmbWildCard AmbWildCardExtends(Position pos, TypeNode extendsNode) {
        AmbWildCard n = new AmbWildCard(pos, extendsNode, true);
        n = (AmbWildCard) n.ext(extFactory().extAmbWildCard());
        return n;
    }

    @Override
    public AmbWildCard AmbWildCardSuper(Position pos, TypeNode superNode) {
        AmbWildCard n = new AmbWildCard(pos, superNode, false);
        n = (AmbWildCard) n.ext(extFactory().extAmbWildCard());
        return n;
    }

    @Override
    public Call Call(Position pos, Receiver target, Id name, List<Expr> args) {
        return Call(pos, target, null, name, args);
    }

    @Override
    public Call Call(Position pos, Receiver target, List<TypeNode> typeArgs,
            Id name, List<Expr> args) {
        Call n =
                super.Call(pos, target, name, CollectionUtil.nonNullList(args));
        JL5CallExt ext = (JL5CallExt) JL5Ext.ext(n);
        ext.typeArgs = CollectionUtil.nonNullList(typeArgs);
        return n;
    }

    @Override
    public New New(Position pos, List<TypeNode> typeArgs, TypeNode type,
            List<Expr> args, ClassBody body) {
        return this.New(pos, null, typeArgs, type, args, body);
    }

    @Override
    public New New(Position pos, Expr outer, List<TypeNode> typeArgs,
            TypeNode objectType, List<Expr> args, ClassBody body) {
        New n =
                super.New(pos,
                          outer,
                          objectType,
                          CollectionUtil.nonNullList(args),
                          body);
        JL5NewExt ext = (JL5NewExt) JL5Ext.ext(n);
        ext.typeArgs = CollectionUtil.nonNullList(typeArgs);
        return n;
    }

    @Override
    public New New(Position pos, Expr outer, TypeNode objectType,
            List<Expr> args, ClassBody body) {
        return New(pos, outer, null, objectType, args, body);
    }

    @Override
    public ConstructorCall ThisCall(Position pos, List<TypeNode> typeArgs,
            List<Expr> args) {
        return ConstructorCall(pos, ConstructorCall.THIS, typeArgs, null, args);
    }

    @Override
    public ConstructorCall ThisCall(Position pos, List<TypeNode> typeArgs,
            Expr outer, List<Expr> args) {
        return ConstructorCall(pos, ConstructorCall.THIS, typeArgs, outer, args);
    }

    @Override
    public ConstructorCall SuperCall(Position pos, List<TypeNode> typeArgs,
            List<Expr> args) {
        return ConstructorCall(pos, ConstructorCall.SUPER, typeArgs, null, args);
    }

    @Override
    public ConstructorCall SuperCall(Position pos, List<TypeNode> typeArgs,
            Expr outer, List<Expr> args) {
        return ConstructorCall(pos,
                               ConstructorCall.SUPER,
                               typeArgs,
                               outer,
                               args);
    }

    @Override
    public ConstructorCall ConstructorCall(Position pos, Kind kind,
            List<TypeNode> typeArgs, List<Expr> args) {
        return ConstructorCall(pos, kind, typeArgs, null, args);
    }

    @Override
    public LocalDecl LocalDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, TypeNode type, Id name) {
        return LocalDecl(pos, flags, annotations, type, name, null);
    }

    @Override
    public LocalDecl LocalDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, TypeNode type, Id name, Expr init) {
        LocalDecl n = super.LocalDecl(pos, flags, type, name, init);
        JL5LocalDeclExt ext = (JL5LocalDeclExt) JL5Ext.ext(n);
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return n;
    }

    @Override
    public LocalDecl LocalDecl(Position pos, Flags flags, TypeNode type,
            Id name, Expr init) {
        return LocalDecl(pos, flags, null, type, name, init);
    }

    @Override
    public FieldDecl FieldDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, TypeNode type, Id name) {
        return FieldDecl(pos, flags, annotations, type, name, null);
    }

    @Override
    public FieldDecl FieldDecl(Position pos, Flags flags, TypeNode type,
            Id name, Expr init) {
        return FieldDecl(pos, flags, null, type, name, init);
    }

    @Override
    public FieldDecl FieldDecl(Position pos, Flags flags,
            List<AnnotationElem> annotations, TypeNode type, Id name, Expr init) {
        FieldDecl n = super.FieldDecl(pos, flags, type, name, init);
        JL5FieldDeclExt ext = (JL5FieldDeclExt) JL5Ext.ext(n);
        ext.annotations = CollectionUtil.nonNullList(annotations);
        return n;
    }

    @Override
    public TypeNode TypeNodeFromQualifiedName(Position pos,
            String qualifiedName, List<TypeNode> typeArguments) {
        TypeNode base = super.TypeNodeFromQualifiedName(pos, qualifiedName);
        if (typeArguments.isEmpty())
            return base;
        else return AmbTypeInstantiation(pos, base, typeArguments);
    }

    @Override
    public ElementValueArrayInit ElementValueArrayInit(Position pos,
            List<Term> elements) {
        ElementValueArrayInit n = new ElementValueArrayInit_c(pos, elements);
        n =
                (ElementValueArrayInit) n.ext(extFactory().extElementValueArrayInit());
        return n;
    }

    @Override
    public ElementValueArrayInit ElementValueArrayInit(Position pos) {
        return ElementValueArrayInit(pos, Collections.<Term> emptyList());

    }
}
