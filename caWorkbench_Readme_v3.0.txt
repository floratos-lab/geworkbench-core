                           Readme.txt
    
                          caWorkbench
                          Version 3.0
                         November 10, 2005
    
================================================================
                            Contents
================================================================
    
    1.0 Introduction and Installation
    2.0 Samples
    3.0 Security Note
    4.0 License
    
================================================================
                        1.0 Introduction and Installation
================================================================
    
    This distribution consists of source codes and external libraries for 
	caWorkbench version 3.0.
	
	JDK5.0 or later is needed to run caWorkbench. 
	 
	 
		 
	To compile the source code, Ant is required. 
		use "ant compile" or "ant" to compile source codes.
		use "ant run" to run the application.
		 
		
		
    For more information, please visit our website:
     
	http://amdec-bioinfo.cu-genome.org/html/caWorkBench3.htm

    
================================================================
                       2.0 Samples
================================================================

    Gene Expression Data:     
	Affy_sample.txt:  Microarray data from a single Affy chip.
	Webmatrix.exp:  Microarray data from multiple Affy chips.
	Genepix_sample.gpr: Microarray data from Genepix chip.
	 	


    
================================================================
                   3.0 Security Note
================================================================
       A java security policy is required to make calls against the
    caBIO server because the caBIO.jar uses Java RMI (Remote
    Method Invocation) calls to connect with the caBIO server.
    The policies defined in a java.policy file which can be found in 
	same folder as this README file. It is for the protection of 
	your system--not the caBIO server. Therefore,  you are 
	free (and encouraged) to edit the java.policy file
    as appropriate for your system. For more information, see:
    
        - http://java.sun.com/security/
    	
    

================================================================
                   4.0 License
================================================================
    
    Please see the file caWorkbenchLicense.txt for all external libraries
    lincense.

 	
    
    
//end
