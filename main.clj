(defn main []
	(let [s (setup)]
		(reset! ants (:ants s))
		(reset! ladybugs (:ladybugs s))
		(reset! aphises (:aphises s)))
			
	(dorun (map #(set-error-handler! % agent-error-log) @ants))
	(dorun (map #(set-error-handler! % agent-error-log) @ladybugs))
	(dorun (map #(set-error-handler! % agent-error-log) @aphises))

	(dorun (map #(send-off % behave) @ants))
	(dorun (map #(send-off % behave) @ladybugs))
	(dorun (map #(send-off % behave) @aphises))

	(send-off newborn-aphises-agent newborn-aphises-process)
	(set-error-handler! newborn-aphises-agent agent-error-log)

	(send-off world-update-agent world-update)
	(set-error-handler! world-update-agent agent-error-log)
	
	(send-off win-app start-gui)
	(set-error-handler! win-app agent-error-log))



