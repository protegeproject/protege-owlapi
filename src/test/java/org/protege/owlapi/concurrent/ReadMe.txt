Revision 23775 has a OWLApiThreadSafetyIssue class that illustrates 
the difference between concurrency in this library and concurrency
in the core OWL api implementation.  The class has been removed because
the OWL api does not include the implconcurrent library by default.  (So Hudson
has been failing.)