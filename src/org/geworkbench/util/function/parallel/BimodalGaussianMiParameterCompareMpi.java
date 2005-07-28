package org.geworkbench.util.function.parallel;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.geworkbench.util.function.tp.BimodalGaussianMIParameterCompare;

public class BimodalGaussianMiParameterCompareMpi {

    public BimodalGaussianMiParameterCompareMpi() {
    }

    static public void main(String[] args) throws MPIException {
        new BimodalGaussianMiParameterCompareMpi().compareMis(args);
    }

    public void compareMis(String[] args) throws MPIException {

        MPI.Init(args);

        int my_rank; // Rank of process
        int source; // Rank of sender
        int dest; // Rank of receiver
        int tag = 50; // Tag for messages
        int myrank = MPI.COMM_WORLD.Rank();
        int p = MPI.COMM_WORLD.Size();

        double[] sigmas = {};


        if (myrank == 0) {

            //tell each slave to run a simulation
            for (int procCtr = 1; procCtr < p; procCtr++) {
                if (procCtr <= sigmas.length) {
                    MPI.COMM_WORLD.Send(sigmas, 0, 0, MPI.INT, procCtr, procCtr);
                }
            }
        } else { //Slave nodes to run a simulation
            System.out.println("Node " + myrank + " running simulation");
            double[] mySigmas = new double[20];

            Status status = MPI.COMM_WORLD.Recv(mySigmas, 0, 0, MPI.DOUBLE, 0, MPI.ANY_TAG);
            int sigmaIndex = status.tag;
            double sigma = mySigmas[sigmaIndex];
            BimodalGaussianMIParameterCompare comp = new BimodalGaussianMIParameterCompare();
            comp.computeMisForSigma(sigma);
        }

        MPI.Finalize();
    }

}
