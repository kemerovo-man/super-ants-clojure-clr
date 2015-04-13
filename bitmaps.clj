;---------------------------------------- BITMAPS ---------------------------------------------
(def rectangles-in-cells (atom []))
(def rendered-bitmaps (atom []))
(def bitmaps-dim 20)

(def ladybug-vert-bitmap  '(0 0 0 0 0 1 1 1 0 0 0 0 1 1 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 1 1 1 1 0 0 1 0 0 1 1 0
							0 0 0 0 0 0 0 1 7 1 1 7 1 0 0 0 1 0 0 0
							0 1 1 0 0 0 1 1 1 1 1 1 1 1 0 1 0 0 0 0
							1 0 0 1 0 1 7 1 1 1 1 1 1 7 1 0 0 0 0 0
							0 0 0 0 1 7 7 7 1 1 1 1 7 7 7 1 0 0 0 0
							0 0 0 1 6 1 7 1 1 1 1 1 1 7 1 6 1 0 0 0
							0 0 0 1 6 6 1 1 1 1 1 1 1 1 6 6 1 0 0 0
							1 0 1 6 1 6 6 6 6 1 1 6 6 6 6 1 6 1 0 0
							1 0 1 6 1 1 6 6 6 1 1 6 6 6 1 1 6 1 0 0
							0 1 1 6 6 6 6 6 6 1 1 6 6 6 6 6 6 1 0 0
							0 0 1 6 6 1 1 6 6 1 1 6 6 1 1 6 6 1 0 0
							0 0 1 6 1 1 1 1 6 1 1 6 1 1 1 1 6 1 1 0
							0 0 1 6 6 1 1 6 6 1 1 6 6 1 1 6 6 1 0 1
							0 0 1 6 6 6 6 8 8 4 4 8 8 6 6 6 6 1 0 1
							0 0 0 1 6 6 1 1 8 4 4 8 1 1 6 6 1 0 0 0
							0 0 1 1 1 6 6 1 6 1 1 6 1 6 6 1 0 0 0 0
							0 0 1 0 0 1 1 6 6 1 1 6 6 1 1 0 1 0 0 0
							1 1 0 0 0 0 0 1 1 1 1 1 1 0 0 0 0 1 0 0
							0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 1 1 1))
			
(def ladybug-diag-bitmap  '(0 1 1 1 0 0 1 1 1 0 0 0 0 0 0 1 1 0 0 0
							0 0 0 0 1 0 0 0 1 0 0 0 0 0 0 0 1 0 0 0
							0 0 0 0 1 0 0 0 1 0 1 1 1 1 1 1 1 0 0 1
							1 0 0 0 0 1 1 1 1 1 7 7 7 1 1 7 1 1 1 1
							0 1 1 0 1 6 6 6 6 1 7 7 7 1 1 1 7 1 0 0
							0 0 0 1 6 6 1 1 6 1 1 7 1 1 1 1 1 1 0 0
							0 0 1 6 6 6 6 1 6 1 1 1 1 1 1 1 1 1 0 0
							0 1 6 6 6 6 6 6 6 6 1 1 1 1 1 7 7 1 0 0
							0 1 6 6 1 1 6 6 6 6 1 1 1 1 7 7 7 1 0 0
							0 1 6 1 1 1 1 6 6 6 1 1 1 1 1 7 7 1 0 0
							0 1 6 6 1 1 6 6 6 1 6 6 6 1 1 1 1 0 0 0
							0 1 6 6 6 6 8 8 4 6 6 6 6 6 6 6 1 1 0 0
							0 1 6 1 1 6 8 4 8 6 6 6 6 1 1 6 1 0 1 0
							0 1 6 6 6 6 4 8 8 6 1 6 6 6 1 6 1 0 0 1
							0 1 6 6 6 1 6 6 6 1 1 1 6 6 6 6 1 1 0 1
							0 0 1 6 1 6 6 1 6 1 1 1 6 6 6 1 0 1 0 0
							0 0 0 1 6 6 6 1 6 6 1 6 6 6 1 1 0 1 0 0
							0 0 0 0 1 6 6 6 6 6 6 6 6 1 1 0 0 1 0 0
							0 0 0 0 0 1 1 1 1 1 1 1 1 1 0 0 0 0 1 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 0 0 0 1))						
						
(def ant-vert-bitmap  '(0 0 0 0 0 0 0 0 0 2 2 0 0 0 1 1 0 0 0 0
						0 0 0 0 0 1 1 0 2 2 2 2 0 1 0 0 0 0 0 0
						0 0 0 0 1 0 0 1 2 3 3 2 1 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 1 2 2 1 0 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0
						0 0 0 1 0 0 0 0 1 5 5 1 0 0 0 0 0 0 0 0
						0 0 0 0 1 0 0 0 1 4 4 1 0 0 0 1 0 0 0 0
						0 0 0 0 0 1 0 0 0 1 1 0 0 0 1 0 1 0 0 0
						0 0 0 0 0 0 1 0 1 5 5 1 0 1 0 0 0 1 0 0
						0 0 1 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0
						0 0 0 1 0 0 0 0 1 4 4 1 0 0 0 0 0 0 0 0
						0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0
						0 0 0 0 0 0 0 1 5 5 5 5 1 0 0 0 1 0 0 0
						0 0 0 0 0 0 0 1 5 5 5 5 1 1 1 0 0 1 0 0
						0 0 0 0 0 1 1 1 5 4 4 5 1 0 0 1 0 0 0 0
						0 0 0 0 1 0 0 0 1 1 1 1 0 0 0 0 1 0 0 0
						0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0
						0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0))

(def ant-diag-bitmap  '(0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 2 2 2 0 0
						0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 2 3 2 1 0
						0 0 0 0 0 0 0 0 0 1 0 0 0 1 1 2 2 1 0 1
						0 0 0 0 0 0 0 0 1 0 0 0 1 5 5 1 1 0 0 0
						0 0 0 0 0 0 0 0 1 0 0 0 1 4 5 1 0 0 0 0
						0 0 0 0 1 1 1 0 1 0 1 1 1 1 1 0 0 0 0 0
						0 0 0 0 0 0 1 0 1 1 1 5 1 0 0 0 0 0 0 0
						0 0 1 0 0 0 1 1 1 5 4 1 1 0 0 0 0 0 0 0
						0 1 0 1 0 1 5 5 5 1 5 1 0 1 1 0 0 0 0 0
						0 0 0 0 1 5 5 5 5 5 1 0 0 0 0 1 0 0 0 0
						0 0 0 0 1 5 4 5 5 5 1 1 1 1 0 0 0 0 0 0
						0 0 0 0 1 5 4 4 5 5 1 0 0 1 0 0 0 0 0 0
						0 0 0 0 1 5 5 5 5 1 0 0 0 0 0 0 0 0 0 0
						0 0 0 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0
						0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0))	

(def aphis-vert-bitmap    '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 1 0 0 0 1 1 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 1 0 0 0 1 1 0 0 1 1 0 0 0 0 0
							0 0 0 0 1 0 1 0 0 1 1 0 1 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 1 1 5 5 1 0 0 1 0 0 0 0 0
							0 0 0 0 0 0 0 0 1 4 4 1 1 1 0 1 0 0 0 0
							0 0 0 0 0 0 1 1 1 4 4 1 0 0 0 0 0 0 0 0
							0 0 0 0 0 1 0 0 1 5 5 1 1 0 0 0 0 0 0 0
							0 0 0 0 1 0 0 1 1 5 5 1 0 1 0 0 0 0 0 0
							0 0 0 0 0 0 1 0 0 1 1 0 0 0 1 0 0 0 0 0
							0 0 0 0 0 1 0 0 0 0 0 0 0 1 0 0 0 0 0 0
							0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0))
						
(def aphis-diag-bitmap    '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 1 0 0 1 0 0 0 0 0 0
							0 0 0 0 0 1 0 0 0 0 1 0 0 1 1 0 0 1 0 0
							0 0 0 0 0 0 1 0 0 0 1 0 1 1 1 1 1 0 0 0
							0 0 0 0 0 0 0 1 0 0 1 1 1 1 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 1 1 4 5 1 0 0 0 1 0 0 0
							0 0 0 0 0 1 1 1 1 4 4 4 1 1 1 1 0 0 0 0
							0 0 0 0 1 0 0 1 5 5 4 1 1 0 0 0 0 0 0 0
							0 0 0 1 0 0 0 1 5 5 1 0 0 1 0 0 0 0 0 0
							0 0 0 0 0 0 0 1 1 1 1 0 0 0 1 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 1 0 0 1 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0))

(def aphis-eggs-bitmap    '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 1 5 5 1 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0
							0 1 4 4 1 0 0 0 0 0 0 0 0 1 5 5 1 0 0 0
							0 1 5 5 1 0 0 0 0 0 0 0 0 1 4 4 1 0 0 0
							0 0 1 1 0 0 0 0 0 0 0 0 0 1 5 5 1 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 1 5 5 1 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 1 4 4 1 0 0 0 0 0 0 0 0 0 0 0 0 0
							0 0 0 1 5 5 1 0 0 0 0 0 1 1 0 0 0 0 0 0
							0 0 0 0 1 1 0 0 0 0 0 1 5 5 1 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 1 4 4 1 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 1 5 5 1 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0
							0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0))						
			

(defn prn-bitmap [bitmap] 
	(let [st-list (partition bitmaps-dim bitmap)] 
		(doseq [st st-list] (println (str/replace (str (pr-str st)) "0" " ")))))		

(defn turn-bitmap [bitmap]
	(let [st-list (reverse (partition bitmaps-dim bitmap))]
		(for [i (range bitmaps-dim) st st-list]
				(nth st i))))
				
(defn turn-bitmap-90 [bitmap]
	(turn-bitmap bitmap))
	
(defn turn-bitmap-180 [bitmap]
	(-> bitmap turn-bitmap turn-bitmap))

(defn turn-bitmap-270 [bitmap]
	(-> bitmap turn-bitmap turn-bitmap turn-bitmap))	
	
(defn flip-vert-bitmap[bitmap]
	(let [st-list (partition bitmaps-dim bitmap)]
		(flatten (for [st st-list]
			(reverse st)))))
			
(defn flip-diag-bitmap[bitmap]		
	(for [i (range bitmaps-dim) st (partition bitmaps-dim (reverse bitmap))] 
		(nth st i)))

(defn dir-bitmaps [vert-bitmap diag-bitmap]
	{0 vert-bitmap
	 1 diag-bitmap
	 2 (turn-bitmap-90 vert-bitmap) 
	 3 (turn-bitmap-90 diag-bitmap)	   
	 4 (turn-bitmap-180 vert-bitmap)
	 5 (turn-bitmap-180 diag-bitmap)		   
	 6 (turn-bitmap-270 vert-bitmap)		   
	 7 (turn-bitmap-270 diag-bitmap)})

(defn alter-dir-bitmaps [vert-bitmap diag-bitmap]
	{0 (-> vert-bitmap flip-vert-bitmap)
	 1 (-> diag-bitmap flip-diag-bitmap)
	 2 (-> vert-bitmap flip-vert-bitmap turn-bitmap-90) 
	 3 (-> diag-bitmap flip-diag-bitmap turn-bitmap-90)	   
	 4 (-> vert-bitmap flip-vert-bitmap turn-bitmap-180)
	 5 (-> diag-bitmap flip-diag-bitmap turn-bitmap-180)
	 6 (-> vert-bitmap flip-vert-bitmap turn-bitmap-270)		   
	 7 (-> diag-bitmap flip-diag-bitmap turn-bitmap-270)})					 
	
(def ant-dir-bitmaps 
	(dir-bitmaps ant-vert-bitmap ant-diag-bitmap))
(def alter-ant-dir-bitmaps 
	(alter-dir-bitmaps ant-vert-bitmap ant-diag-bitmap))
(def aphis-dir-bitmaps 
	(dir-bitmaps aphis-vert-bitmap aphis-diag-bitmap))
(def alter-aphis-dir-bitmaps 
	(alter-dir-bitmaps aphis-vert-bitmap aphis-diag-bitmap))
(def ladybug-dir-bitmaps 
	(dir-bitmaps ladybug-vert-bitmap ladybug-diag-bitmap))
(def alter-ladybug-dir-bitmaps 
	(alter-dir-bitmaps ladybug-vert-bitmap ladybug-diag-bitmap))

(defn prn-dir-bitmaps [dir-bitmaps]
	(for [dir (range 8)] (prn-bitmap (dir-bitmaps dir))))
	 

