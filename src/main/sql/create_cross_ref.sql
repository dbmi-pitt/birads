;; In the first pass birads we took all EMITTING_[1-7] app_status reports
;; and placed them in a production directory to be anafora annotated
;;
;; In a subsequent pass called two we are asked to bolster this collection
;; by ~180 reports.  Since there are 7 categories we would need 26 reports
;; from each category to reach the targeted 180 extras.  26 * 7 = 182


;;
;; First create an original sequence to accession codebook
;;

create table pass_one_codebook as select accession 
       from 
             report 
       where 
             application_status like 'EMITTED%' and
             application_status != 'EMITTED_0';
             
alter table pass_one_codebook add itemid int(10) FIRST;
alter table 
           pass_one_codebook 
      MODIFY itemid INT(10) UNSIGNED AUTO_INCREMENT
      PRIMARY KEY;
      
;;
;; Now bolster the EMITTED_ with EMITTED_PASS2_ random samples.
;;      

;;
;; We will rerun the birads original ReportWriter program but change 
;; the tag to be EMITTING_PASS2_
;; Also the sample size will be 26
;;

