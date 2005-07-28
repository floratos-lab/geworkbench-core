/*
 * Created on Oct 26, 2003
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

package edu.ksu.cis.bnj.bbn.converter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * <P>Encapsulate the file input stream. This is so that we can fetch the file
 * name if possible. It is because some converter like Hugin .net requires the
 * graph name to be named as the file name. FileInputStream doesn't facilitate
 * this.
 *
 * @author Roby Joehanes
 */
public class ConverterInputStream extends FileInputStream {

    protected String filename;

    /**
     * @param file
     * @throws java.io.FileNotFoundException
     */
    public ConverterInputStream(File file) throws FileNotFoundException {
        super(file);
        filename = file.getAbsolutePath();
    }

    /**
     * @param fdObj
     */
    public ConverterInputStream(FileDescriptor fdObj) {
        super(fdObj);
    }

    /**
     * @param name
     * @throws java.io.FileNotFoundException
     */
    public ConverterInputStream(String name) throws FileNotFoundException {
        super(name);
        filename = name;
    }

    /**
     * Get the file name
     *
     * @return
     */
    public String getFileName() {
        return filename;
    }
}
