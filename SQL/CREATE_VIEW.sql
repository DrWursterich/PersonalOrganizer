CREATE VIEW IF NOT EXISTS APPOINTMENTS_VIEW AS
	SELECT AG.NAME, AG.DESCRIPTION, A.START_DATE, A.END_DATE,
	(P.MINUTES + P.DAYS * 24 * 60) AS REPETITION_MINUTES,
	P.MONTHS AS REPETITION_MONTHS,
	IFNULL(C.END_DATE, A.START_DATE) AS REPETITION_END
		FROM APPOINTMENT_APPOINTMENT_GROUP AS AAG
		JOIN APPOINTMENT_GROUP AS AG
			ON AAG.APPOINTMENT_GROUP_FK = AG.ID
		JOIN APPOINTMENT AS A
			ON AAG.APPOINTMENT_FK = A.ID
		LEFT JOIN CIRCLE AS C
			ON A.CIRCLE_FK = C.ID
		LEFT JOIN PERIOD AS P
			ON C.PERIOD_FK = P.ID;
