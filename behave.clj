;---------------------------------------- BEHAVE ---------------------------------------------
	
(defn block-ahead [loc ahead ahead-left ahead-right] 
	(let 
		[block-ahead? (block? ahead)
		 block-ahead-left? (block? ahead-left)
		 block-ahead-right? (block? ahead-right)]
	(when block-ahead?
		(cond
			(not block-ahead-left?)
				(get-around loc -1)  
			(not block-ahead-right?)
				(get-around loc 1)   
			:else (turn-disor loc (rand-dir))))))
 
(defn ant-have-sugar [loc place ahead ahead-left ahead-right] 
	(cond 
		(:anthill @place) 
			(do (turn loc 4) (drop-sugar loc) loc)
		(and (< (:pher @place) 1000) (> (:pher @ahead) 1000) (chance? 2))
			(let [tmp-loc (move loc)] (turn-disor tmp-loc 4) (drop-sugar tmp-loc) (move tmp-loc))
		(:anthill @ahead)
			(move loc)
		(:anthill @ahead-left) 
			(do (turn-disor loc -1) loc)
		(:anthill @ahead-right)
			(do (turn-disor loc 1) loc)
		:else (move loc)))
 
(defn ant-have-no-sugar [loc place ahead ahead-left ahead-right] 
	(cond 
		(and (pos? (:sugar @place)) (not (:anthill @place))) 
			(do (turn-disor loc 4) (take-sugar loc) loc) 
		(and (pos?(:sugar @ahead)) (not (:anthill @ahead)))
			(move loc)
		(and (pos?(:sugar @ahead-left)) (not (:anthill @ahead-left)))
			(do (turn-disor loc -1) loc)
		(and (pos?(:sugar @ahead-right)) (not (:anthill @ahead-right)))
			(do (turn-disor loc 1) loc)
		:else (move loc)))
		
(defn cross-pheromon-road [loc] 
	(let [tmp-loc (move loc)]
		(if (chance? 2)
			(turn-disor tmp-loc -2)
			(turn-disor tmp-loc 2))))
			
(defn cross-pheromon-road2 [loc param] 
	(let [tmp-loc (move loc)]
		(if	(chance? 2)
			(turn-disor tmp-loc (* -1 param))
			(turn-disor tmp-loc (* 3 param)))))			
			
(defn behave-ladybug [v]
	(ladybug-sleep)
	(let  
		[loc (first v)
		p (place loc)
		bug (:bug @p)
		ladybug (if (ladybug? bug) bug nil)]
		(if ladybug
			(dosync
				(let
					[dir (:dir ladybug)
					ahead (ahead loc dir)
					ahead-left (ahead-left loc dir)
					ahead-right (ahead-right loc dir)
					bug-ahead (:bug @ahead)
					bug-ahead-left (:bug @ahead-left)
					bug-ahead-right (:bug @ahead-right)
					aphis-ahead (if (aphis? bug-ahead) bug-ahead nil)
					aphis-ahead-left (if (aphis? bug-ahead-left) bug-ahead-left nil)
					aphis-ahead-right (if (aphis? bug-ahead-right) bug-ahead-right nil)
					aphis-eggs-ahead (aphis-eggs? ahead)
					aphis-eggs-ahead-left (aphis-eggs? ahead-left)
					aphis-eggs-ahead-right (aphis-eggs? ahead-right)
					new-loc 
						(cond 
							(:anthill @ahead)
								(do (turn loc -1) loc)
							aphis-ahead 
								(do (alter ahead dissoc :bug)
									(move loc))
							aphis-eggs-ahead 
								(do	(alter ahead dissoc :aphis-eggs)
									(move loc))							
							(or aphis-ahead-left aphis-eggs-ahead-left)
								(turn loc -1)
							(or aphis-ahead-right aphis-eggs-ahead-right)
								(turn loc 1)
							(chance? 10) 
								(rand-turn loc)
							:else (move loc))]			
			(send-off *agent* behave-ladybug)			
			[new-loc :ladybug]))
			(do (prn *agent* "is out")
				(dosync 
					(alter p assoc :problem true))))))

(defn behave-aphis [v]
	(aphis-sleep)
	(let 
		[loc (first v)
		p (place loc)
		bug (:bug @p)
		aphis (if (aphis? bug) bug nil)]
	(if aphis
		(let [dir (:dir aphis)
			ahead (ahead loc dir)
			ahead-left (ahead-left loc dir)
			ahead-right (ahead-right loc dir)
			new-loc 
				(cond 
					(and (>= (:syrup aphis) 50) (not (block? ahead)))
						(do	(dosync 
								(alter p assoc :aphis-eggs true :bug (assoc aphis :syrup (- (:syrup aphis) 50))) (move loc)))
					(and (= (:herb @p) 0) (>= (:syrup aphis) 10) (chance? 100))
						(do	(dosync 
								(alter p assoc :sugar (inc (:sugar @p))
										:bug (assoc aphis :syrup (dec (:syrup aphis))))) loc)
					(pos? (:herb @p))
						(do (dosync 
								(alter p assoc  :herb (dec (:herb @p))
												:bug (assoc aphis :syrup (inc (:syrup aphis))))) loc)
					(and (pos? (:herb @ahead)) (not (block? ahead)))
						(move loc)
					(and (pos? (:herb @ahead-left)) (not (block? ahead-left)))
						(do (turn loc -1) loc)
					(and (pos? (:herb @ahead-right)) (not (block? ahead-right)))
						(do (turn loc 1) loc)
					:else
						(do 
							(when (chance? 10) (rand-turn loc))
							(if (chance? 20) (move loc) loc)))]
		(send-off *agent* behave-aphis)
		[new-loc :aphis])
		;(prn *agent* "is out")
		)))
	
(defn behave-ant [v behave-num]
	(ant-sleep)
	(let [loc (first v)
		p (place loc)
		bug (:bug @p)
		ant (if (ant? bug) bug nil)]
		(if ant
			(let
				[dir (:dir ant)
				ahead (ahead loc dir)
				ahead-left (ahead-left loc dir)
				ahead-right (ahead-right loc dir)]
				(cond (= behave-num 0)
							(let [new-loc 
									(if (:sugar ant)
										(ant-have-sugar loc p ahead ahead-left ahead-right)
										(ant-have-no-sugar loc p ahead ahead-left ahead-right))]
								(send-off *agent* behave-ant (inc behave-num))
								[new-loc :ant])
					  (= behave-num 1)		
							(let [new-loc (cond 
									(block? ahead) 
										(block-ahead loc ahead ahead-left ahead-right)
									(pher-on-the-way? p ahead ahead-left ahead-right [-1 -1 1 1])
										(do (turn-disor loc -1) (let [tmp-loc (move loc)] (turn-disor tmp-loc -1) tmp-loc))
									(pher-on-the-way? p ahead ahead-left ahead-right [1 1 1 1])
										(cross-pheromon-road loc)
									(pher-on-the-way? p ahead ahead-left ahead-right [-1 1 1 1])
										(cross-pheromon-road loc)
									(pher-on-the-way? p ahead ahead-left ahead-right [-1 1 -1 1])
										(cross-pheromon-road2 loc 1)
									(pher-on-the-way? p ahead ahead-left ahead-right [-1 1 1 -1])
										(cross-pheromon-road2 loc -1)
									(chance? 1000000)
										(do (turn-disor loc (rand-dir)) loc)
									:else (move loc))]
			(send-off *agent* behave-ant 0)
			[new-loc :ant])))
			(do (prn *agent* "is out")
				(dosync 
					(alter p assoc :problem true)))
			)))
		