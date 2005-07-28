package org.geworkbench.util.function.parallel;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.geworkbench.util.function.tp.BimodalGaussianMICompare;

public class BimodalGaussianMiCompareMpi {

    public BimodalGaussianMiCompareMpi() {
    }

    static public void main(String[] args) throws MPIException {
        new BimodalGaussianMiCompareMpi().compareMis(args);
    }

    public void compareMis(String[] args) throws MPIException {

        MPI.Init(args);

        int my_rank; // Rank of process
        int source; // Rank of sender
        int dest; // Rank of receiver
        int tag = 50; // Tag for messages
        int myrank = MPI.COMM_WORLD.Rank();
        int p = MPI.COMM_WORLD.Size();

        final int RUN_SIMULATION = 1;
        final int SIMULATION_COMPLETE = 2;
        final int END_OF_SIMULATIONS = 3;

        int[] startIndices = new int[5];
        for (int i = 0; i < startIndices.length; i++) {
            startIndices[i] = 10 * i;
        }

        if (myrank == 0) {
            int[] fakeArr = new int[0];
            //tell each slave to run a simulation
            for (int procCtr = 1; procCtr < p; procCtr++) {
                if (procCtr <= startIndices.length) {
                    MPI.COMM_WORLD.Send(fakeArr, 0, 0, MPI.INT, procCtr, startIndices[procCtr]);
                }
            }
        } else { //Slave nodes to run a simulation
            System.out.println("Node " + myrank + " running simulation");
            int[] fakeArr = new int[0];

            Status status = MPI.COMM_WORLD.Recv(fakeArr, 0, 0, MPI.INT, 0, MPI.ANY_TAG);
            int startIndex = status.tag;
            BimodalGaussianMICompare comp = new BimodalGaussianMICompare();
            comp.compareAllMIs(startIndex);
        }

        MPI.Finalize();
    }

}
