--PRIORITY
 INSERT OR IGNORE INTO PRIORITY
	(ID,	NAME) VALUES
	(1,		'HIGH'), 
	(2,		'NORMAL'),
	(3,		'LOW');

--PERIOD
 INSERT OR IGNORE INTO PERIOD
	(ID,	MINUTES,	DAYS,	MONTHS) VALUES
	(1,		0,			1,		0),
	(2,		180,		0,		0);

--PERIOD
 INSERT OR IGNORE INTO PERIOD
	(ID,	MINUTES,	DAYS,	MONTHS) VALUES
	(1,		0,			0,		0),
	(2,		24,			0,		0),
	(3,		60,			0,		0),
	(4,		15,			0,		0);

--PRIORITY_PERIOD
 INSERT OR IGNORE INTO PRIORITY_PERIOD
	(ID,	PRIORITY_FK,	PERIOD_FK) VALUES
	(1,		1,				2),
	(2,		1,				3),
	(3,		1,				4),
	(5,		2,				3),
	(6,		2,				4);

--CATEGORY
 INSERT OR IGNORE INTO CATEGORY
	(ID,	NAME,	DESCRIPTION) VALUES
	(1,		'NONE',	'');
