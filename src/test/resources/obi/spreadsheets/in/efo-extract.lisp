# The list is attached.  The latest EFO is available from
# www.ebi.ac.uk/efo/efo.owl What we need is the values of
# definition_citation annotation properties for each of the EFO classes
# in the list (so we can determine what we need to mireot in OBI).
# Appreciate your script wizardry.

(defun extract-efo-citations ()
  (let ((kb (load-kb-jena  "obi:spreadsheets;efo-2009-07-30.owl"))
	(id2cite (make-hash-table :test 'equalp)))
    (loop for (uri citation) in 
	 (sparql '(:select (?uri ?citation) () (?uri !<http://www.ebi.ac.uk/efo/definition_citation> ?citation))
		 :use-reasoner :none :kb kb)
	 do (pushnew citation (gethash (#"replaceFirst" (uri-full uri) ".*/" "") id2cite) :test 'equalp))
    (with-open-file (in "obi:spreadsheets;efo-terms.txt")
      (with-open-file (out "obi:spreadsheets;efo-terms-cited.txt" :if-does-not-exist :create :if-exists :supersede :direction :output)
	(loop for line = (read-line in nil :eof)
	   until (eq line :eof)
	   for (term accession annotation-type) = (split-at-char line #\tab)
	   for citations = (gethash accession id2cite)
	   do (format out "~a	~a	~a	~{~a~^|~}~%" term accession annotation-type citations)
	     )))
    id2cite))
	 