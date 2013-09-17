/*
  The Broad Institute
  SOFTWARE COPYRIGHT NOTICE AGREEMENT
  This software and its documentation are copyright (2003-2007) by the
  Broad Institute/Massachusetts Institute of Technology. All rights are
  reserved.

  This software is supplied without any warranty or guaranteed support
  whatsoever. Neither the Broad Institute nor MIT can be responsible for its
  use, misuse, or functionality.
*/
package org.geworkbench.bison.datastructure.biocollections;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.geworkbench.util.FilePathnameUtils;

/**
 * @author  Marc-Danie Nazaire
 */
public class PredictionModel implements Serializable
{
	private static final long serialVersionUID = 739774613405629498L;
    private byte[] model;
    private File modelFile;

    public PredictionModel(File file)
    {
        modelFile = file;
        model = null;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(modelFile);
            FileChannel fc = fis.getChannel();
            model = new byte[(int) (fc.size())];
            ByteBuffer bb = ByteBuffer.wrap(model);
            fc.read(bb);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public File getPredModelFile()
    {
        if(modelFile!= null && modelFile.exists())
            return modelFile;

        try
        {
            modelFile = File.createTempFile("predModel", ".odf", new File(FilePathnameUtils.getTemporaryFilesDirectoryPath()));
            modelFile.deleteOnExit();

            FileOutputStream out = new FileOutputStream(modelFile);

            out.write(model);

            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return modelFile;
    }

    public byte[] getModelFileContent()
    {   
        return model;
    }
}
