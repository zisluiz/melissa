// Agent sample_agent in project melissa

/* Initial beliefs and rules */
energia(100).
maxEnergia(100).
lifespan(45).
niceTemperature(25).

/* Rules */

ta_quente(T) :-
	niceTemperature(NT) &
	T > NT + 1.

ta_frio(T) :-
	niceTemperature(NT) &
	T < NT - 1.
	
com_fome(E) :-
	maxEnergia(M) &
	E <= M * 0.2.
	
satisfeita(E) :-
	maxEnergia(M) &
	E > M * 0.9.
	
age_to_sentinel :-
	.my_name(Me) &
	play(Me, baba, _) &
	nascimento(N) &
	hoje(D) &
	D-N >= 18.
	
age_to_explorer :-
	.my_name(Me) &
	play(Me, sentinela, _) &
	nascimento(N) &
	hoje(D) &
	D-N >= 22.

too_old :-
	nascimento(N) &
	hoje(D) &
	D-N >= 45.
	
new_day(D) :-
	hoje(H) &
	H \== D.

/* Initial goals */

/* Organisational Plans */

+obligation(Ag,Norm,committed(Ag,Mission,Scheme),Deadline)
    : .my_name(Ag)
   <- //.print("I am obliged to commit to ",Mission," on ",Scheme);
      commitMission(Mission)[artifact_name(Scheme)].

/*   Basic Plans  */

+!registerBee[scheme(Sch)] : age(X)
<-	today(D);
	+hoje(D);
	+nascimento(D-X);
	if (X < 18) {
		adoptRole(baba);
		registerBee(baba);	
	} else { if (X < 22) {
		adoptRole(sentinela);
		registerBee(sentinela);
	} else {
		adoptRole(exploradora);
		registerBee(exploradora);
		!setPosition;
	}};
	-age(_);
	!updateDay.

+!registerBee[scheme(Sch)]
<-	lookupArtifact("Hive",AId);
	focus(AId);
	today(D);
	+hoje(D);
	+nascimento(D);
	adoptRole(baba);
	registerBee(baba);
	!updateDay. 

+!updateDay
<-	.wait(5000);
	today(D);
	if (new_day(D)) {
		-+hoje(D)
	}.
	
+age_to_sentinel <- changeRole(sentinela).
	
+age_to_explorer <- changeRole(exploradora).
	
+too_old
<-	.random(N);
	if(N < 0.5) {
		!suicide
	}.

+!setPosition
<-	lookupArtifact("Map",AId);
	focus(AId);
	?hive(X0,Y0,W,H)[artifact_id(AId)];
	.random(R1);
	X = X0 + math.floor(W*R1);
	.random(R2);
	Y = Y0 + math.floor(H*R2);
	setPosition(X,Y).

+!alimentarse: energia(E) & not satisfeita(E)
<-	comer(5);
	-+energia(E+10).

-!alimentarse <- .wait(100); !alimentarse.
+!alimentarse <- .wait(100); !alimentarse.

+energia(E) : E <= 0 <- !suicide.

+!suicide : .my_name(Me)
<- 	drop_all_intentions;
	unRegisterBee;
	ag_killed(Me).

/*   Baba Plans   */

+!fabricarMel : energia(E) & not com_fome(E)
<-	!tryPollen;	
	.wait(100);
	!fabricarMel;
	-+energia(E-1).

@tryPollen [atomic]
+!tryPollen
<- 	lookupArtifact("Hive",AId);
	focus(AId);
	if(pollen(P)[artifact_id(AId)] & P>1) {
		processPollen
	}.

-!fabricarMel
<-	.wait(500);
	!fabricarMel.

+!alimentarRainha : energia(E)
<-	.send(queen, achieve, comer(50));
	-+energia(E-1).

+!alimentarLarvas.

/* Sentinel Plans */

+!aquecer : resfriando & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		stop_resfriar;
		-resfriando;
		-+energia(E-1)
	};
	!aquecer.

+!aquecer : not aquecendo & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		aquecer;
		+aquecendo;
		-+energia(E-1)
	};
	!aquecer.
	
+!aquecer <- .wait(100+math.random(200)); !aquecer.

+!resfriar: aquecendo & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		stop_aquecer;
		-aquecendo;
		-+energia(E-1)
	};
	!resfriar.

+!resfriar: not resfriando & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		resfriar;
		+resfriando;
		-+energia(E-1)
	};
	!resfriar;.
	
+!resfriar <- .wait(100+math.random(200)); !resfriar.

/* Explorer Plans */
	
+!procurarPolen[scheme(Sch)] : energia(E) & not com_fome(E)
<-	lookupArtifact("Map",AId);
	focus(AId);
	.findall(r(LEVEL, X, Y, WIDTH, HEIGHT), pollenField(LEVEL, X, Y, WIDTH, HEIGHT)[artifact_id(AId)], List);
	!flyToField(List);
	if(collect) {
		-collect;
		!coletarPolen[scheme(Sch)];
		!trazerPolen[scheme(Sch)]
	} else {
		!procurarPolen[scheme(Sch)]
	}.

+!procurarPolen[scheme(Sch)] <- .wait(100); !procurarPolen[scheme(Sch)].

-!procurarPolen[error(ia_failed)] <- .print("Não consegui procurar!").
-!procurarPolen[error_msg(M)]     <- .print("Error in: ",M).

+!flyToField([r(Lvl,X0,Y0,W,H)|L])
<-	.random(N);
	if (N < 0.2) {
		.random(R1);
		X = X0 + math.floor(W*R1);
		.random(R2);
		Y = Y0 + math.floor(H*R2);
		//.print("Going to (", X, ",",Y,")");
		flyTo(X,Y);
		+collect
	} else { if(not .empty(L)) {
		!flyToField(L)
	} else {
		
	}}.

+!trazerPolen[scheme(Sch)]
<-	!flyToHive;
	!estocarPolen[scheme(Sch)].

-!trazerPolen[error(ia_failed)] <- .print("I didn't in!").
-!trazerPolen[error_msg(M)]     <- .print("Error in: ",M).

+!flyToHive
<-	lookupArtifact("Map",AId);
	focus(AId);
	?hive(X0,Y0,W,H)[artifact_id(AId)];
	.random(R1);
	X = X0 + math.floor(W*R1);
	.random(R2);
	Y = Y0 + math.floor(H*R2);
	//.print("Going to (", X, ",",Y,")");
	flyTo(X,Y).

+!estocarPolen[scheme(Sch)] : energia(E)
<-	delivery;
	-+energia(E-20);
	!procurarPolen[scheme(Sch)].

-!estocarPolen[error(ia_failed)].
-!estocarPolen[error_msg(M)]/* <- .print("Error: ", M) */.

+!coletarPolen <- collect.	// arrumar aqui

-!coletarPolen[scheme(Sch),error_msg(M)]
<-	.print("Error in: ",M);
	!procurarPolen[scheme(Sch)].

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }