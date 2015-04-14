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
		
(defn new-loc [loc]
	(let [
		p (place loc)
		bug (:bug @p)
		dir (:dir bug)
		bug-type (:type bug)
		ant (ant? bug)
		ladybug (ladybug? bug)
		aphis (aphis? bug)
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
		aphis-eggs-ahead-right (aphis-eggs? ahead-right)]
		(cond 
			(= bug-type :ant)
				(cond 
					(and (:sugar ant) (:anthill @p)) 
						(do (turn loc 4) (drop-sugar loc) loc)
					(and  (:sugar ant) (< (:pher @p) 1000) (> (:pher @ahead) 1000) (chance? 2))
						(let [tmp-loc (move loc)] (turn-disor tmp-loc 4) (drop-sugar tmp-loc) (move tmp-loc))
					(and (:sugar ant) (:anthill @ahead))
						(move loc)
					(and (:sugar ant) (:anthill @ahead-left)) 
						(do (turn-disor loc -1) loc)
					(and (:sugar ant) (:anthill @ahead-right))
						(do (turn-disor loc 1) loc)
						
					(and (not (:sugar ant)) (pos? (:sugar @p)) (not (:anthill @p))) 
						(do (turn-disor loc 4) (take-sugar loc) loc) 
					(and (not (:sugar ant)) (pos? (:sugar @ahead)) (not (:anthill @ahead)) (not bug-ahead))
						(move loc)
					(and (not (:sugar ant)) (pos? (:sugar @ahead-left)) (not (:anthill @ahead-left)))
						(do (turn-disor loc -1) loc)
					(and (not (:sugar ant)) (pos? (:sugar @ahead-right)) (not (:anthill @ahead-right)))
						(do (turn-disor loc 1) loc)	
						
					(block? ahead) 
						(block-ahead loc ahead ahead-left ahead-right)
					(pher-on-the-way? loc [-1 -1 1 1])
						(do (turn-disor loc -1) (let [tmp-loc (move loc)] (turn-disor tmp-loc -1) tmp-loc))
					(pher-on-the-way? loc [1 1 1 1])
						(cross-pheromon-road loc)
					(pher-on-the-way? loc [-1 1 1 1])
						(cross-pheromon-road loc)
					(pher-on-the-way? loc [-1 1 -1 1])
						(cross-pheromon-road2 loc 1)
					(pher-on-the-way? loc [-1 1 1 -1])
						(cross-pheromon-road2 loc -1)
					(chance? 1000000)
						(do (turn-disor loc (rand-dir)) loc)
					:else (move loc))
			
			(= bug-type :aphis)
				(cond 
					(and (>= (:syrup aphis) 50) (not (block? ahead)))
						(do	
							(alter p assoc :aphis-eggs true
												:bug (assoc aphis :syrup (- (:syrup aphis) 50)))
							(move loc))
					(and (= (:herb @p) 0) (>= (:syrup aphis) 10) (chance? 100))
						(do	
							(alter p assoc :sugar (inc (:sugar @p)) 
												:bug (assoc aphis :syrup (dec (:syrup aphis))))
							loc)
					(pos? (:herb @p))
						(do (alter p assoc  :herb (dec (:herb @p))
													:bug (assoc aphis :syrup (inc (:syrup aphis))))
							  loc)
					(and (pos? (:herb @ahead)) (not (block? ahead)))
						(move loc)
					(and (pos? (:herb @ahead-left)) (not (block? ahead-left)))
						(do (turn loc -1) loc)
					(and (pos? (:herb @ahead-right)) (not (block? ahead-right)))
						(do (turn loc 1) loc)
					:else
						(do 
							(when (chance? 10) (rand-turn loc))
							(if (chance? 20) (move loc) loc)))
			
			(= bug-type :ladybug)
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
				:else (move loc)))))			
			
(defn behave [v]
	(bug-sleep (second v))
	(dosync
		(let  
			[loc (first v)
			bug-type (second v)
			p (place loc)
			bug (:bug @p)
			check-bug? (bug-typed bug bug-type)]
			(if check-bug?
				(let
					[n-loc (new-loc loc)]
					(send-off *agent* behave)			
					[n-loc bug-type])
				(if (= bug-type :aphis) 
					(do (swap! aphises (fn [col] 
														(remove (fn [x] (nil? @x)) col))) nil)
					(prn *agent* "is out"))))))
	
(defn newborn-aphises-process [_]
	(let [nb-aphises (aphises-born)]
	(when (> (count nb-aphises) 0)
			(dorun (map #(set-error-handler! % agent-error-log) nb-aphises))
			(dorun (map #(send-off % behave) nb-aphises))
			(dorun (map #(swap! aphises conj %) nb-aphises)))
	(sleep 5000))
	(send-off *agent* newborn-aphises-process)
	nil)	