;---------------------------------------- LOGIC ---------------------------------------------
(defn sleep [ms] 
	(. System.Threading.Thread (Sleep ms))) 	

(def world-time (atom 0))

(defstruct bug :type :dir)
(defstruct aphis :type :dir :syrup)
(defstruct cell :sugar :pher :herb)

(def ants (atom nil))
(def ladybugs (atom nil))
(def aphises (atom nil))

(def newborn-aphises-agent (agent nil))

(def world 
	(vec (map (fn [_] 
				(vec (map (fn [_] (ref (struct cell 0 0 0))) 
				 	 (range dim)))) 
		 (range dim))))

(defn place [[x y]]
	(-> world (nth x) (nth y)))

(defn chance? [x]
	(if (= (rand-int x) 0) true false))
	
(defn rand-dir [] (rand-int 8))	

(defn create-bug [loc type dir]
	(let [p (place loc)
		  bug (if (= type :aphis)
				(struct aphis type dir 0)
				(struct bug type dir))]
	(alter p assoc :bug bug))
	(agent [loc type]))

(defn create-ant [loc dir]
	(create-bug loc :ant dir))

(defn create-aphis [loc dir]
	(create-bug loc :aphis dir))

(defn create-ladybug [loc dir]
	(create-bug loc :ladybug dir))
	
(defn bug-typed [bug type]
	(if bug
		(if (= (:type bug) type) bug nil)))		
	
(defn aphis? [bug]
	(bug-typed bug :aphis))

(defn ant? [bug]
	(bug-typed bug :ant))

(defn ladybug? [bug]
	(bug-typed bug :ladybug))	
		
(defn setup []
	(doall
		(for [x anthill-range y anthill-range]
			(dosync
				(alter (place [x y]) assoc :anthill true))))
				
	(dosync 
		(let [
			aphises (doall 
						(for [i (range aphis-count)]
							(create-aphis [(rand-int dim) (rand-int dim)] (rand-dir))))
			ladybugs (filter #(if % true false)
						(doall 
							(for [i (range ladybugs-count)]
								(let [rand-x (rand-int dim)
									  rand-y (rand-int dim)
									  p (place [rand-x rand-y])]
								(when-not (:anthill @p)
									(create-ladybug [rand-x rand-y] (rand-dir)))))))	
			ants	(doall
						(for [x anthill-range y anthill-range]
							(create-ant [x y] (rand-dir))))]
		;(dotimes [i sugar-places]
		;	(let [rand-x (rand-int dim)
		;		  rand-y (rand-int dim)
		;		  p (place [rand-x rand-y])]
		;	(when-not (:anthill @p)
		;		(dosync (alter p assoc :sugar (rand-int sugar-range))))))
		
		;(dotimes [i (rand-int 100)]
		;	(let [rand-x (rand-int dim)
		;		  rand-y (rand-int dim)
		;		  p (place [rand-x rand-y])]
		;    (when-not (or (:anthill @p) (pos? (:sugar @p)))
		;		(dosync (alter p assoc :herb (rand-int herb-scale))))))	
		
		;(dotimes [i sugar-places]
		;	(let [rand-x (rand-int dim)
		;		  rand-y (rand-int dim)
		;		  p (place [rand-x rand-y])]
		;	(when-not (:anthill @p)
		;		(dosync (alter p assoc :aphis-eggs true)))))
									
		{:ants ants :ladybugs ladybugs :aphises aphises})))

(defn anthill-sugar-calc [] 
	(reduce + 
		(for [x anthill-range y anthill-range]
			(let [p (place [x y])]
				(:sugar @p)))))

(defn free-sugar-calc [] 
	(reduce + 
		(for [x (range dim) y (range dim)]
			(let [p (place [x y])
  				  ant (:bug @p)
				  sugar (if (:anthill @p) 0 (:sugar @p))]
				(+ sugar (if (:sugar ant) 1 0))))))	
				
(defn herb-calc [] 
	(reduce + 
		(for [x (range dim) y (range dim)]
			(let [p (place [x y])]
				(:herb @p)))))

(defn bound [b n]
	(let [r (rem n b)]
	(if (neg? r) 
		(+ r b) 
		r)))

(def dir-delta {0 [0 -1]
				1 [1 -1]
				2 [1 0]
				3 [1 1]
				4 [0 1]
				5 [-1 1]
				6 [-1 0]
				7 [-1 -1]})

(defn delta-loc [[x y] dir]
	(let [[dx dy] (dir-delta (bound 8 dir))]
	[(bound dim (+ x dx)) (bound dim (+ y dy))]))

(defn turn [loc amt]
	(dosync
		(let [p (place loc)
			  bug (:bug @p)]
		(alter p assoc :bug 
						  (assoc bug :dir 
										(bound 8 (+ (:dir bug) amt))))))
	loc)
	
(defn rand-turn [loc]
	(turn loc (rand-dir)))	

(defn turn-disor [loc amt]
	(let [p (place loc)
		  ant (:bug @p)]
	(dosync
		(alter p assoc :bug (dissoc ant :sugar-dir :anthill-dir))))
	(turn loc amt))	
	
(defn ahead [loc dir]		
	(place (delta-loc loc dir)))
		
(defn ahead-left [loc dir]		
	(place (delta-loc loc (dec dir))))	

(defn ahead-right [loc dir]		
	(place (delta-loc loc (inc dir))))

(defn block? [pos]	
	(if (or (:bug @pos) (:aphis-eggs @pos))	true false))
		
(defn pher-tail [old-p ant]
	(when (and (not (:anthill @old-p)) 
			   (or (:sugar-dir ant) (:anthill-dir ant)))
		(alter old-p assoc :pher (+ 50 (:pher @old-p))))
	(when-not (:anthill @old-p)
		(alter old-p assoc :pher (inc (:pher @old-p))))
	(when (and (not (:anthill @old-p)) (:anthill-dir ant) (:super-pher ant))
		(alter old-p assoc :pher (+ 200000000 (:pher @old-p)))))	
	
(defn move-ant [bug new-p old-p]
	(when (:anthill @new-p)
		(alter new-p assoc :bug (assoc bug :anthill-dir true))
		(when (and (:super-pher bug) (chance? 100))
			(alter new-p assoc :bug (dissoc bug :super-pher)))
		(when (chance? 10000)
			(alter new-p assoc :bug (assoc bug :super-pher true))))
	(pher-tail old-p bug))
	
(defn move [loc]
	(let [old-p (place loc)
		  bug (:bug @old-p)
		  new-loc (delta-loc loc (:dir bug))
		  new-p (place new-loc)]
	(dosync 
		(if (not (block? new-p)) 
			(do
				(alter new-p assoc :bug bug)
				(alter old-p dissoc :bug)
				(when (ant? bug)
					(move-ant bug new-p old-p))
				new-loc)
			loc))))


(defn take-sugar [loc]
	(let [p (place loc)
		  ant (:bug @p)] 
	(when (and (pos? (:sugar @p)) (not (:sugar ant))) 
		(dosync 
			(alter p assoc :sugar (dec (:sugar @p))
						   :bug (assoc ant :sugar true :sugar-dir true))))
	loc))

(defn drop-sugar [loc]
	(let [p (place loc)
		  ant (:bug @p)] 
	(dosync
		(when (:sugar ant)
				(alter p assoc :sugar (inc (:sugar @p))
								:bug (dissoc ant :sugar))))
	loc))

	
(def world-update-agent (agent [0 0]))
(defn world-update [loc]
	(sleep 1)
	(send *agent* world-update)
	(let [p (place loc)
			x (first loc)
			y (second loc)
			inc-x? (< x (dec dim))
			inc-y? (and (not inc-x?) (< y (dec dim)))
			reset? (and (= x (dec dim)) (= y (dec dim)))]
			(dosync 
				(alter p assoc :pher (Math/Round (* evap-rate (:pher @p)) 3))
				(when (and (not (or (:anthill @p) (pos? (:sugar @p)))) (< (:herb @p) herb-scale) (chance? 100))
					(alter p assoc :herb (+ (:herb @p) (rand-int herb-scale)))))
			(if reset? 
				[0 0]
				[(if inc-x? (inc x) 0) (if inc-y? (inc y) y)])))
		
(defn herb-grow []
	(let [p (place [(rand-int dim) (rand-int dim)])]
		(when (and (not (or (:anthill @p) (pos? (:sugar @p)))) (< (:herb @p) herb-scale))
			(dosync 
				(alter p assoc :herb (+ (:herb @p) (rand-int herb-scale)))))))

(defn aphis-eggs? [pos]
	(if (:aphis-eggs @pos) true false))
	
(defn aphises-count []
	(reduce + 
		(for [x (range dim) y (range dim)]
			(let [p (place [x y])]
				(if (aphis? (:bug @p)) 1 0)))))
	
(defn get-aphis [loc] 
	(first 
		(filter 
			(fn [x] 
				(if (= @x loc) true false))
			@aphises)))



(defn bug-sleep [type]
	(let [ms 
		(cond (= type :ant)
						@ant-sleep-ms
				 (= type :aphis)	
						@aphis-sleep-ms
			     (= type :ladybug)	
						@ladybug-sleep-ms
				:else 0)]
		(sleep (+ 50 (rand-int ms)))))	

(defn get-around [loc by] 
	(do (turn-disor loc (* by 1))
				;(bug-sleep :ant)
				(let [tmp-loc (move loc)
					  tmp-turn (turn-disor tmp-loc (* by -2))]
				;(bug-sleep :ant)
				(let [tmp2-loc (move tmp-loc)]
					;(bug-sleep :ant)
					(turn-disor tmp2-loc (* by 1))
					tmp2-loc))))	
					
(defn pher-on-the-way? [loc v]
	(let [pher-power 20
		p (place loc)
		bug (:bug @p)
		dir (:dir bug)
		ahead (ahead loc dir)
		ahead-left (ahead-left loc dir)
		ahead-right (ahead-right loc dir)]
		(and (> (* (v 0) (:pher @p)) (* pher-power (v 0)))
			 (> (* (v 1) (:pher @ahead)) (* pher-power (v 1)))
			 (> (* (v 2) (:pher @ahead-left)) (* pher-power (v 2)))
			 (> (* (v 3) (:pher @ahead-right)) (* pher-power (v 3))))))	


(defn aphises-born []
	(let [born-locs (filter #(if % true false)
						(for [x (range dim) y (range dim)]
							(let [loc [x y]
								  p (place loc)]
							(when (and (:aphis-eggs @p) (chance? 2)) [x y]))))]
	(filter #(if % true false) 
		(flatten 
			(for [loc born-locs]
				(let  [p (place loc)
					   dir (rand-dir)
					   ahead (ahead loc dir)
					   ahead-left (ahead-left loc dir)
					   ahead-right (ahead-right loc dir)]
				(dosync
					(alter p dissoc :aphis-eggs)
					(list (create-aphis loc dir)
						  (when-not (block? ahead) 
								(create-aphis (delta-loc loc dir) (rand-dir)))
						  (when-not (block? ahead-left)
								(create-aphis (delta-loc loc (dec dir)) (rand-dir)))
						  (when-not (block? ahead-right) 
								(create-aphis (delta-loc loc (inc dir)) (rand-dir)))))))))))
				