package edu.ksu.cis.kdd.util.parser.ast;

/*
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * @author Roby Joehanes
 */

public class ASTDivExpr extends ASTBinopExpr implements Cloneable {
    public ASTDivExpr(ASTExpr o1, ASTExpr o2) {
        super(o1, o2);
    }

    public Object apply(ExprVisitor visitor, Object input) {
        return visitor.caseASTDivExpr(this, input);
    }

    public Object clone() {
        ASTExpr o1 = (ASTExpr) op1.clone();
        ASTExpr o2 = (ASTExpr) op2.clone();
        return new ASTMulExpr(o1, o2);
    }

    public String toString() {
        return "(" + op1 + "/" + op2 + ")";
    } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
}
