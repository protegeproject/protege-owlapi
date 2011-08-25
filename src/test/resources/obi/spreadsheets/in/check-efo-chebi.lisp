(defun chebis-without-inchis ()
  (with-open-file (in "obi:spreadsheets;efo-chebi.txt")
    (loop for line = (read-line in nil :eof)
       until (eq line :eof)
       for (term accession annotation-type xref) = (split-at-char line #\tab)
       for has-inchi
       = 
       (and xref
	    (not (char= (char term 0) #\#))
	    (#"matches" xref "(?i)^chebi:.*")
	    (sparql
	     `(:select (?name) () 
		       (:graph !<http://purl.org/science/graph/obo/CHEBI>
			       (,(read-from-string (format nil "!~a" xref)) !oboinowl:hasRelatedSynonym ?syn)
			       (?syn !rdfs:label ?name)
			       (:filter (regex (str ?name) "(?i)^inchi=.*"))))
	     :use-reasoner !<http://sparql.obo.neurocommons.org/sparql> ))
       unless has-inchi do (write-line line)
       )))
	 

