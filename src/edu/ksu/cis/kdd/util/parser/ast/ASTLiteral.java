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

public class ASTLiteral extends ASTExpr {
    protected double val;

    public ASTLiteral(Double v) {
        val = v.doubleValue();
    }

    public String toString() {
        return String.valueOf(val);
    }

    public boolean equals(Object o) {
        return (o instanceof ASTLiteral && ((ASTLiteral) o).val == val);
    }

    public Object clone() {
        return new ASTLiteral(new Double(val));
    }

    public Object apply(ExprVisitor visitor, Object input) {
        return visitor.caseASTLiteral(this, input);
    }
}
