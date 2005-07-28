/*
 * Created on 9 Jul 2003
 *
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
package edu.ksu.cis.kdd.data.converter.excel;

import java.util.StringTokenizer;

/**
 * @author Roby Joehanes
 */
public class ExcelSheetRange {
    public int x1, y1, x2, y2;

    public ExcelSheetRange() {
    }

    public ExcelSheetRange(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Takes the Excel-style range string. Example:
     * A1:G12
     *
     * @param args
     */
    public ExcelSheetRange(String args) {
        assert (args != null);
        StringTokenizer tok = new StringTokenizer(args, ":"); // $NON-NLS-1$
        String token1 = tok.nextToken().trim();
        String token2 = tok.nextToken().trim();
        int[] xy = parse(token1);
        x1 = xy[0];
        y1 = xy[1];
        xy = parse(token2);
        x2 = xy[0];
        y2 = xy[1];
    }

    protected int[] parse(String str) {
        int[] xy = new int[2];
        String alpha = "", digit;
        int len = str.length();
        int pos = 0;
        for (; pos < len; pos++) {
            char c = str.charAt(pos);
            if (Character.isLetter(c)) {
                alpha += c;
            } else if (Character.isDigit(c)) {
                break;
            } else {
                throw new RuntimeException("Unknown identifier " + c);
            }
        }
        alpha = alpha.toUpperCase();
        digit = str.substring(pos);
        xy[1] = Integer.parseInt(digit);

        len = alpha.length();
        xy[0] = 0;
        for (pos = 0; pos < len; pos++) {
            int c = alpha.charAt(pos) - 'A';
            xy[0] += (int) Math.round(c * Math.pow(26, len - pos - 1));
        }
        return xy;
    }
}
